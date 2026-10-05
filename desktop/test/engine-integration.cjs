'use strict';
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const { Engine } = require('../src/engine.cjs');
const resources = path.resolve(__dirname, '../resources');
const root = path.resolve(__dirname, '../.smoke');
fs.mkdirSync(root, { recursive: true });
const dataDirectory = fs.mkdtempSync(path.join(root, 'engine with spaces '));
let engine;
(async () => {
  engine = new Engine({ resources, dataDirectory });
  const origin = await engine.start();
  assert.equal((await fetch(origin + '/api/v1/session')).status, 403);
  assert.equal((await engine.get('/api/v1/session')).desktop, true);
  assert.equal((await engine.fetch('/api/v1/clock', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: '{"running":true,"timeScale":1}' })).status, 403);
  assert.equal((await engine.fetch('/api/v1/session', { headers: { Origin: 'https://untrusted.test' } })).status, 403);
  const network = await engine.get('/api/v1/export');
  const template = Object.values(network.trains)[0];
  await engine.command('/api/v1/trains', { id: 'DESKTOP-TEST', number: '99999', model: 'Persistência desktop',
    profileId: template.profileId, capacity: 1200, carriages: 6, lineId: template.currentLineId, nodeId: template.nodeId, active: false });
  await engine.command('/api/v1/clock', { running: true, timeScale: 1 });
  await engine.stop();
  assert.equal(engine.child.exitCode, 0, 'encerramento limpo do Java');
  engine = new Engine({ resources, dataDirectory });
  await engine.start();
  const restored = await engine.get('/api/v1/export');
  assert.ok(restored.trains['DESKTOP-TEST'], 'composição recuperada após reinício');
  assert.equal(restored.running, false, 'relógio recuperado pausado');
  // Simulate loss of the parent: the pipe closes without calling the shutdown endpoint.
  engine.child.stdin.end();
  let timer;
  const exited = await Promise.race([engine.exited, new Promise((_, reject) => { timer = setTimeout(() => reject(new Error('Motor órfão após EOF')), 15000); })]).finally(() => clearTimeout(timer));
  assert.equal(exited.code, 0);
  console.log(JSON.stringify({ integration: 'passed', dataDirectory, checks: ['private transport', 'CSRF', 'origin', 'persistence', 'graceful shutdown', 'parent EOF'] }));
})().catch(error => { console.error(error); process.exitCode = 1; }).finally(async () => { await engine?.stop(); });
