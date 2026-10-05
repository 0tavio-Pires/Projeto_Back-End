param([switch]$SkipCoreBuild)
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
if (-not $SkipCoreBuild) { & (Join-Path $PSScriptRoot 'build.ps1') }
& (Join-Path $PSScriptRoot 'prepare-desktop.ps1')
Push-Location (Join-Path $projectRoot 'desktop')
try {
    & npm.cmd ci --no-fund
    if ($LASTEXITCODE -ne 0) { throw 'Falha ao instalar dependências desktop.' }
    & npm.cmd test
    if ($LASTEXITCODE -ne 0) { throw 'Testes desktop falharam.' }
    & npm.cmd run test:integration
    if ($LASTEXITCODE -ne 0) { throw 'Teste do motor empacotado falhou.' }
    & npm.cmd run dist
    if ($LASTEXITCODE -ne 0) { throw 'Falha na geração do instalador.' }
    & (Join-Path $PSScriptRoot 'test-desktop.ps1')
    $installer = Get-ChildItem -LiteralPath release -Filter 'Ferrovia-Setup-*.exe' | Sort-Object LastWriteTime -Descending | Select-Object -First 1
    $hash = (Get-FileHash -LiteralPath $installer.FullName -Algorithm SHA256).Hash.ToLowerInvariant()
    Set-Content -LiteralPath "$($installer.FullName).sha256" -Value "$hash  $($installer.Name)" -Encoding ascii
    Write-Host "Instalador: $($installer.FullName)"
    Write-Host 'Para abrir sem instalar: scripts/run-desktop.ps1'
} finally { Pop-Location }
