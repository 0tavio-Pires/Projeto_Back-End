param()
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
Push-Location $projectRoot
try {
    Push-Location frontend
    try {
        & npm.cmd ci --no-fund
        if ($LASTEXITCODE -ne 0) { throw 'Falha na instalação do frontend.' }
        & npm.cmd test
        if ($LASTEXITCODE -ne 0) { throw 'Testes do frontend falharam.' }
        & npm.cmd run build
        if ($LASTEXITCODE -ne 0) { throw 'Build do frontend falhou.' }
    } finally { Pop-Location }
    & .\mvnw.cmd -B -ntp clean verify
    if ($LASTEXITCODE -ne 0) { throw 'Validação do backend falhou.' }
    Write-Host 'Pronto: target/ProjetoCPTM-1.0.0-SNAPSHOT.jar. Execute scripts/run.ps1.'
} finally { Pop-Location }
