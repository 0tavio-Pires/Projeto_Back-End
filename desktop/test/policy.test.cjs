'use strict';
const test = require('node:test');
const assert = require('node:assert/strict');
const { isBackendUrl, isExternalReference, parseReady } = require('../src/policy.cjs');
test('a credencial só pode seguir para a origem exata do motor local', () => {
  const origin = 'http://127.0.0.1:45123';
  assert.equal(isBackendUrl(origin + '/api/v1/network', origin), true);
  for (const value of ['http://127.0.0.1:45124/', 'http://localhost:45123/', 'https://127.0.0.1:45123/',
    'http://127.0.0.1:45123.evil.test/', 'http://127.0.0.1:45123@evil.test/', 'http://user@127.0.0.1:45123/', 'file:///etc/passwd']) {
    assert.equal(isBackendUrl(value, origin), false, value);
  }
  assert.equal(isBackendUrl(origin, null), false);
});
test('links externos limitados às referências oficiais HTTPS', () => {
  assert.equal(isExternalReference('https://www.metro.sp.gov.br/'), true);
  for (const value of ['http://www.metro.sp.gov.br/', 'https://www.metro.sp.gov.br.evil.test/', 'file:///C:/Windows', 'javascript:alert(1)', 'https://user@www.metro.sp.gov.br/']) {
    assert.equal(isExternalReference(value), false, value);
  }
});
test('o handshake exige uma linha exata e porta válida', () => {
  assert.equal(parseReady('FERROVIA_DESKTOP_READY:45123'), 45123);
  for (const line of ['log FERROVIA_DESKTOP_READY:80', 'FERROVIA_DESKTOP_READY:0', 'FERROVIA_DESKTOP_READY:65536', 'FERROVIA_DESKTOP_READY:80/path']) assert.equal(parseReady(line), null);
});
