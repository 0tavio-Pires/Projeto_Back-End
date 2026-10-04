param()
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
Push-Location $projectRoot
try {
    $artifact = Join-Path $projectRoot 'target/ProjetoCPTM-1.0.0-SNAPSHOT.jar'
    if (-not (Test-Path -LiteralPath $artifact)) { throw 'Compile primeiro: .\scripts\build.ps1' }
    $javaCommand = if ($env:JAVA_HOME) { Join-Path $env:JAVA_HOME 'bin/java.exe' } else { 'java' }
    & $javaCommand -jar $artifact
    if ($LASTEXITCODE -ne 0) { throw 'O servidor encerrou com erro.' }
} finally { Pop-Location }
