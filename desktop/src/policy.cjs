'use strict';

function isBackendUrl(value, origin) {
  if (!origin) return false;
  try {
    const url = new URL(value);
    return url.origin === origin && url.protocol === 'http:' && url.hostname === '127.0.0.1'
      && !url.username && !url.password;
  } catch { return false; }
}

function isExternalReference(value) {
  try {
    const url = new URL(value);
    return url.protocol === 'https:' && !url.username && !url.password
      && ['www.metro.sp.gov.br', 'cptm.sp.gov.br', 'www.cptm.sp.gov.br'].includes(url.hostname);
  } catch { return false; }
}

function parseReady(line) {
  const match = /^FERROVIA_DESKTOP_READY:(\d{1,5})$/.exec(line.trim());
  const port = match ? Number(match[1]) : 0;
  return port > 0 && port <= 65535 ? port : null;
}

module.exports = { isBackendUrl, isExternalReference, parseReady };
