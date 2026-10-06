'use strict';
const { app, BrowserWindow, Menu, dialog, shell } = require('electron');
const path = require('node:path');
const fs = require('node:fs');
const os = require('node:os');
const { Engine } = require('./engine.cjs');
const { isBackendUrl, isExternalReference } = require('./policy.cjs');

app.setName('Ferrovia');
app.setAppUserModelId('br.com.ferrovia.desktop');
app.enableSandbox();
const smoke = process.argv.includes('--smoke-test');
const dataDirectory = smoke ? fs.mkdtempSync(path.join(os.tmpdir(), 'ferrovia-smoke-'))
  : path.join(app.getPath('appData'), 'Ferrovia');
app.setPath('userData', dataDirectory);
const resources = app.isPackaged ? process.resourcesPath : path.join(__dirname, '..', 'resources');
let window, engine, origin = null, quitting = false, booting = false;

if (!app.requestSingleInstanceLock()) app.quit();
else {
  app.on('second-instance', () => { if (window) { if (window.isMinimized()) window.restore(); window.show(); window.focus(); } });
  app.on('before-quit', event => {
    event.preventDefault();
    if (quitting) return;
    quitting = true;
    if (window && !window.isDestroyed()) window.destroy();
    Promise.resolve(engine?.stop()).catch(error => console.error(error)).finally(() => app.exit(process.exitCode ?? 0));
  });
  app.on('window-all-closed', () => { if (!quitting) app.quit(); });
  app.whenReady().then(createWindow).catch(fatal);
}

async function createWindow() {
  window = new BrowserWindow({
    width: 1440, height: 980, minWidth: 1000, minHeight: 700, show: !smoke,
    title: 'Ferrovia · Centro de Operações', backgroundColor: '#0b1220',
    icon: path.join(__dirname, '..', 'build', 'icon.ico'),
    webPreferences: { nodeIntegration: false, contextIsolation: true, sandbox: true,
      webSecurity: true, allowRunningInsecureContent: false, webviewTag: false,
      devTools: !app.isPackaged, partition: 'ferrovia-session' }
  });
  const session = window.webContents.session;
  const contentsId = window.webContents.id;
  session.setPermissionRequestHandler((_contents, _permission, callback) => callback(false));
  session.setPermissionCheckHandler(() => false);
  session.webRequest.onBeforeSendHeaders((details, callback) => {
    const headers = { ...details.requestHeaders };
    for (const key of Object.keys(headers)) if (key.toLowerCase() === 'x-ferrovia-desktop') delete headers[key];
    if (isBackendUrl(details.url, origin) && details.webContentsId === contentsId) headers['X-Ferrovia-Desktop'] = engine.token;
    callback({ requestHeaders: headers });
  });
  session.webRequest.onBeforeRequest((details, callback) => {
    const allowed = isBackendUrl(details.url, origin) || details.url.startsWith('file:') || details.url.startsWith('devtools:');
    callback({ cancel: !allowed });
  });
  session.on('will-download', (_event, item) => {
    if (!isBackendUrl(item.getURL(), origin)) { item.cancel(); return; }
    item.setSaveDialogOptions({ title: 'Exportar cenário ferroviário', defaultPath: 'ferrovia-cenario.json', filters: [{ name: 'Cenário JSON', extensions: ['json'] }] });
  });
  window.webContents.setWindowOpenHandler(({ url }) => { openReference(url); return { action: 'deny' }; });
  window.webContents.on('will-navigate', (event, url) => {
    if (!isBackendUrl(url, origin)) { event.preventDefault(); openReference(url); }
  });
  window.webContents.on('will-attach-webview', event => event.preventDefault());
  window.on('close', event => { if (!quitting) { event.preventDefault(); app.quit(); } });
  Menu.setApplicationMenu(Menu.buildFromTemplate([
    { label: 'Arquivo', submenu: [
      { label: 'Exportar cenário…', accelerator: 'CmdOrCtrl+Shift+S', click: () => exportScenario().catch(error => dialog.showMessageBox(window, {
        type: 'error', title: 'Não foi possível exportar', message: error.message, detail: 'Escolha um local em que sua conta possa gravar e tente novamente.'
      })) },
      { label: 'Abrir pasta de dados', click: () => shell.openPath(path.join(dataDirectory, 'data')) },
      { label: 'Abrir registros', click: () => shell.openPath(path.join(dataDirectory, 'logs')) },
      { type: 'separator' }, { label: 'Sair', accelerator: 'Alt+F4', click: () => app.quit() }
    ] },
    { label: 'Editar', submenu: [{ role: 'undo', label: 'Desfazer' }, { role: 'redo', label: 'Refazer' }, { type: 'separator' },
      { role: 'cut', label: 'Recortar' }, { role: 'copy', label: 'Copiar' }, { role: 'paste', label: 'Colar' }, { role: 'selectAll', label: 'Selecionar tudo' }] },
    { label: 'Exibir', submenu: [{ role: 'reload', label: 'Recarregar painel' }, { role: 'resetZoom', label: 'Tamanho original' },
      { role: 'zoomIn', label: 'Ampliar' }, { role: 'zoomOut', label: 'Reduzir' }, { role: 'togglefullscreen', label: 'Tela cheia' }] },
    { label: 'Ajuda', submenu: [
      { label: 'Manual de uso', click: () => shell.openPath(path.join(resources, 'manual', 'index.html')) },
      { label: 'Sobre o Ferrovia', click: () => dialog.showMessageBox(window, { type: 'info', title: 'Ferrovia',
        message: `Ferrovia ${app.getVersion()}`, detail: 'Central de supervisão e simulação ferroviária.\nCenário histórico de São Paulo · 2024.\nDados locais associados à sua conta do Windows.' }) }
    ] }
  ]));
  await window.loadFile(path.join(__dirname, 'startup.html'));
  await boot();
}

async function boot() {
  if (booting || quitting) return;
  booting = true; origin = null;
  try {
    await engine?.stop();
    engine = new Engine({ resources, dataDirectory });
    origin = await engine.start();
    engine.on('unexpected-exit', () => { if (!quitting) fatal(new Error('O motor ferroviário encerrou inesperadamente. O último estado confirmado está preservado.')); });
    if (quitting) { await engine.stop(); return; }
    await window.loadURL(origin);
    if (smoke) {
      const snapshot = await engine.get('/api/v1/network');
      const deadline = Date.now() + 15000;
      let rendered = false;
      while (!rendered && Date.now() < deadline) {
        rendered = await window.webContents.executeJavaScript("document.body.innerText.includes('Todos os movimentos. Uma central.') && document.body.innerText.includes('Aplicativo local')");
        if (!rendered) await new Promise(resolve => setTimeout(resolve, 100));
      }
      if (!rendered) throw new Error('A janela não apresentou o painel autenticado.');
      const commandWorked = await window.webContents.executeJavaScript(`(async () => {
        const session = await (await fetch('/api/v1/session')).json();
        const response = await fetch('/api/v1/step', {method:'POST',headers:{'Content-Type':'application/json',[session.csrfHeader]:session.csrf},body:JSON.stringify({seconds:1})});
        return response.ok;
      })()`);
      if (!commandWorked) throw new Error('O painel não conseguiu executar um comando protegido por CSRF.');
      const output = path.join(dataDirectory, 'desktop-smoke.png');
      fs.writeFileSync(output, (await window.webContents.capturePage()).toPNG());
      const backup = path.join(dataDirectory, 'export-smoke.json');
      const downloaded = new Promise((resolve, reject) => {
        const timer = setTimeout(() => reject(new Error('A exportação pela janela não terminou.')), 15000);
        window.webContents.session.once('will-download', (_event, item) => {
          item.setSavePath(backup);
          item.once('done', (_done, state) => { clearTimeout(timer); state === 'completed' ? resolve() : reject(new Error(`Exportação: ${state}`)); });
        });
      });
      // Exercise the same URL/download behavior as the React export link.
      await window.webContents.executeJavaScript(`(() => { const link=document.createElement('a'); link.href='/api/v1/export'; link.download='ferrovia-cenario.json'; document.body.appendChild(link); link.click(); link.remove(); })()`);
      await downloaded;
      if (Object.keys(JSON.parse(fs.readFileSync(backup, 'utf8')).trains).length !== 26) throw new Error('Exportação desktop inválida.');
      console.log(JSON.stringify({ smoke: 'passed', activeTrains: snapshot.metrics.active, enginePid: engine.child.pid, screenshot: output, dataDirectory }));
      app.quit();
    }
  } catch (error) { await fatal(error); }
  finally { booting = false; }
}

function openReference(url) {
  if (isExternalReference(url)) void shell.openExternal(url);
}

async function exportScenario() {
  if (!origin) return;
  const data = await engine.get('/api/v1/export');
  const result = await dialog.showSaveDialog(window, { title: 'Exportar cenário ferroviário', defaultPath: 'ferrovia-cenario.json', filters: [{ name: 'Cenário JSON', extensions: ['json'] }] });
  if (!result.canceled && result.filePath) await fs.promises.writeFile(result.filePath, JSON.stringify(data, null, 2), 'utf8');
}

async function fatal(error) {
  if (quitting) return;
  if (smoke) { console.error(error); process.exitCode = 1; app.quit(); return; }
  const result = await dialog.showMessageBox(window, { type: 'error', title: 'Ferrovia · Não foi possível continuar',
    message: error.message, detail: 'Você pode consultar os registros e tentar abrir o motor novamente.',
    buttons: ['Tentar novamente', 'Abrir registros', 'Sair'], defaultId: 0, cancelId: 2 });
  if (result.response === 2) { app.quit(); return; }
  if (result.response === 1) await shell.openPath(path.join(dataDirectory, 'logs'));
  // The current boot must unwind before a new attempt begins.
  setTimeout(() => { if (!quitting) void boot(); }, 500);
}
