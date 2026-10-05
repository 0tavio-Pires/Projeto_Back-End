param()
$ErrorActionPreference = 'Stop'
$executable = Join-Path (Split-Path -Parent $PSScriptRoot) 'desktop/release/win-unpacked/Ferrovia.exe'
if (-not (Test-Path -LiteralPath $executable)) { throw 'Gere o aplicativo primeiro com scripts/build-desktop.ps1.' }
# The requested interactive application needs its own visible window.
Start-Process -FilePath $executable
