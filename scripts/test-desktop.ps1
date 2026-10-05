param()
$ErrorActionPreference = 'Stop'
$desktopRoot = Join-Path (Split-Path -Parent $PSScriptRoot) 'desktop'
$executable = Join-Path $desktopRoot 'release/win-unpacked/Ferrovia.exe'
if (-not (Test-Path -LiteralPath $executable)) { throw 'Compile o aplicativo antes de executar o smoke test empacotado.' }
$reports = Join-Path $desktopRoot '.smoke'
New-Item -ItemType Directory -Force $reports | Out-Null
$runId = [guid]::NewGuid().ToString('N')
$stdout = Join-Path $reports "$runId.stdout.log"
$stderr = Join-Path $reports "$runId.stderr.log"
$process = Start-Process -FilePath $executable -ArgumentList '--smoke-test' -WindowStyle Hidden -PassThru -RedirectStandardOutput $stdout -RedirectStandardError $stderr
try {
    $deadline = [DateTime]::UtcNow.AddMinutes(2)
    while (-not $process.WaitForExit(1000)) {
        if ([DateTime]::UtcNow -gt $deadline) { throw "Tempo limite no smoke test. Consulte $stderr" }
    }
    $process.WaitForExit()
    if ($process.ExitCode -ne 0) { throw "Aplicativo encerrou com código $($process.ExitCode). Consulte $stderr" }
    $line = Get-Content -LiteralPath $stdout | Where-Object { $_ -match '^\{"smoke":"passed"' } | Select-Object -Last 1
    if (-not $line) { throw "O aplicativo não confirmou o smoke test. Consulte $stdout e $stderr" }
    $result = $line | ConvertFrom-Json
    if (Get-Process -Id $result.enginePid -ErrorAction SilentlyContinue) { throw 'O processo Java permaneceu aberto após o encerramento do aplicativo.' }
    Copy-Item -LiteralPath $result.screenshot -Destination (Join-Path $reports 'desktop-smoke.png') -Force
    Copy-Item -LiteralPath (Join-Path $result.dataDirectory 'logs/engine.log') -Destination (Join-Path $reports 'packaged-engine.log') -Force
    Set-Content -LiteralPath (Join-Path $reports 'packaged-result.json') -Value $line -Encoding utf8
    Write-Host "Desktop empacotado validado: abertura, comando, exportação e encerramento. Imagem: $reports/desktop-smoke.png"
} finally {
    if (-not $process.HasExited) { $process.Kill(); $process.WaitForExit(10000) | Out-Null }
    $process.Dispose()
}
