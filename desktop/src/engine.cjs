'use strict';
const { spawn } = require('node:child_process');
const { randomBytes } = require('node:crypto');
const fs = require('node:fs');
const path = require('node:path');
const { EventEmitter } = require('node:events');
const { createInterface } = require('node:readline');
const { parseReady } = require('./policy.cjs');

class Engine extends EventEmitter {
  constructor({ resources, dataDirectory, timeoutMs = 90000 }) {
    super();
    this.resources = resources;
    this.dataDirectory = dataDirectory;
    this.timeoutMs = timeoutMs;
    this.token = randomBytes(32).toString('hex');
    this.origin = null;
    this.child = null;
    this.stopping = false;
    this.stopPromise = null;
  }

  async start() {
    if (this.child) throw new Error('O motor já foi iniciado.');
    const java = path.join(this.resources, 'runtime', 'bin', 'java.exe');
    const jar = path.join(this.resources, 'engine', 'ferrovia.jar');
    if (!fs.existsSync(java) || !fs.existsSync(jar)) {
      throw new Error('Componentes do aplicativo ausentes. Execute scripts/build-desktop.ps1 ou reinstale o Ferrovia.');
    }
    fs.mkdirSync(path.join(this.dataDirectory, 'data'), { recursive: true });
    const logs = path.join(this.dataDirectory, 'logs');
    fs.mkdirSync(logs, { recursive: true });
    this.logPath = path.join(logs, 'engine.log');
    if (fs.existsSync(this.logPath) && fs.statSync(this.logPath).size > 5 * 1024 * 1024) {
      fs.copyFileSync(this.logPath, path.join(logs, 'engine.previous.log'));
      fs.truncateSync(this.logPath);
    }
    this.log = fs.createWriteStream(this.logPath, { flags: 'a' });
    this.log.on('error', () => {}); // A log failure must not crash the parent and orphan the engine.
    this.log.write(`\n[desktop ${new Date().toISOString()}] Iniciando motor\n`);
    const environment = { ...process.env };
    // The desktop owns its database/profile and never inherits arbitrary JVM options.
    for (const key of Object.keys(environment)) {
      if (/^(SPRING_|SERVER_|MANAGEMENT_|RAIL_|JAVA_TOOL_OPTIONS$|JDK_JAVA_OPTIONS$|_JAVA_OPTIONS$|CLASSPATH$)/i.test(key)) delete environment[key];
    }
    environment.RAIL_DESKTOP_TOKEN = this.token;
    const args = ['-Xms64m', '-Xmx512m', '-Dfile.encoding=UTF-8', '-jar', jar,
      '--spring.profiles.active=desktop', '--server.address=127.0.0.1', '--server.port=0',
      '--spring.datasource.url=jdbc:h2:file:./data/ferrovia;DB_CLOSE_ON_EXIT=FALSE',
      '--spring.datasource.username=sa', '--spring.datasource.password=',
      '--spring.config.location=classpath:/application.properties,classpath:/application-desktop.properties'];
    const child = spawn(java, args, { cwd: this.dataDirectory, env: environment, windowsHide: true, stdio: ['pipe', 'pipe', 'pipe'] });
    this.child = child;
    // EOF on this pipe also shuts Java down if Electron crashes or is terminated.
    child.stdin.on('error', () => {});
    child.on('error', error => this.log.write(`[desktop] Falha de processo: ${error.message}\n`));
    child.stdout.on('data', data => this.log.write(data));
    child.stderr.on('data', data => this.log.write(data));
    this.exited = new Promise(resolve => {
      child.once('close', (code, signal) => {
        this.log.end(`[desktop] Motor encerrado: ${code ?? signal}\n`);
        resolve({ code, signal });
        if (!this.stopping) this.emit('unexpected-exit', { code, signal });
      });
    });
    try {
      const port = await new Promise((resolve, reject) => {
        const lines = createInterface({ input: child.stdout });
        const timer = setTimeout(() => finish(new Error('O motor não respondeu a tempo. Consulte os registros do aplicativo.')), this.timeoutMs);
        const onError = error => finish(error);
        const onExit = code => finish(new Error(`O motor encerrou durante a abertura (${code}). Consulte os registros do aplicativo.`));
        function finish(error, value) {
          clearTimeout(timer); lines.close(); child.removeListener('error', onError); child.removeListener('exit', onExit);
          error ? reject(error) : resolve(value);
        }
        lines.on('line', line => { const ready = parseReady(line); if (ready) finish(null, ready); });
        child.once('error', onError); child.once('exit', onExit);
      });
      this.origin = `http://127.0.0.1:${port}`;
      const response = await this.get('/api/v1/session');
      if (response.desktop !== true || response.username !== 'local') throw new Error('O serviço iniciado não confirmou a identidade desktop.');
      return this.origin;
    } catch (error) {
      await this.stop();
      throw error;
    }
  }

  async fetch(route, options = {}) {
    if (!this.origin || !route.startsWith('/') || route.startsWith('//')) throw new Error('Serviço local indisponível.');
    return fetch(this.origin + route, {
      ...options, redirect: 'error', signal: AbortSignal.timeout(10000),
      headers: { ...options.headers, 'X-Ferrovia-Desktop': this.token }
    });
  }

  async get(route) {
    const response = await this.fetch(route);
    if (!response.ok) throw new Error(`O motor recusou a consulta (${response.status}).`);
    return response.json();
  }

  async command(route, body = {}) {
    const csrfResponse = await this.fetch('/auth/csrf');
    if (!csrfResponse.ok) throw new Error('Não foi possível abrir a sessão de comando.');
    const csrf = await csrfResponse.json();
    const cookies = csrfResponse.headers.getSetCookie().map(cookie => cookie.split(';')[0]).join('; ');
    const response = await this.fetch(route, { method: 'POST', headers: {
      'Content-Type': 'application/json', [csrf.header]: csrf.token, Cookie: cookies
    }, body: JSON.stringify(body) });
    if (!response.ok) throw new Error(`Comando recusado (${response.status}): ${(await response.json()).detail ?? route}`);
    return response.json();
  }

  stop() {
    if (this.stopPromise) return this.stopPromise;
    this.stopping = true;
    this.stopPromise = (async () => {
      if (!this.child || this.child.exitCode !== null || this.child.signalCode) return;
      try { if (this.origin) await this.command('/desktop/shutdown'); } catch { /* Pipe EOF is the fallback. */ }
      this.child.stdin.end();
      let timer;
      const timedOut = await Promise.race([this.exited.then(() => false), new Promise(resolve => { timer = setTimeout(() => resolve(true), 10000); })]);
      clearTimeout(timer);
      if (timedOut) { this.child.kill(); await this.exited; }
    })();
    return this.stopPromise;
  }
}

module.exports = { Engine };
