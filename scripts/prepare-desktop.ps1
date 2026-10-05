param()
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$desktopRoot = Join-Path $projectRoot 'desktop'
$jar = Join-Path $projectRoot 'target/ProjetoCPTM-1.0.0-SNAPSHOT.jar'
if (-not [Environment]::Is64BitOperatingSystem -or $env:OS -ne 'Windows_NT') { throw 'O pacote desktop exige Windows x64.' }
if (-not (Test-Path -LiteralPath $jar)) { throw 'Compile o sistema com scripts/build.ps1 antes de preparar o desktop.' }
$lock = Get-Content -Raw (Join-Path $desktopRoot 'runtime-lock.json') | ConvertFrom-Json
$cache = Join-Path $desktopRoot '.cache'
$resources = Join-Path $desktopRoot 'resources'
$build = Join-Path $desktopRoot 'build'
New-Item -ItemType Directory -Force $cache, $resources, $build | Out-Null
$archive = Join-Path $cache "temurin-$($lock.version)-windows-x64.zip"
if (-not (Test-Path -LiteralPath $archive)) {
    Write-Host "Obtendo Eclipse Temurin $($lock.version)..."
    Invoke-WebRequest -Uri $lock.url -OutFile "$archive.partial"
    if ((Get-FileHash -LiteralPath "$archive.partial" -Algorithm SHA256).Hash.ToLowerInvariant() -ne $lock.sha256) { throw 'Checksum do Java inválido. O arquivo não será usado.' }
    Move-Item -LiteralPath "$archive.partial" -Destination $archive -Force
}
if ((Get-FileHash -LiteralPath $archive -Algorithm SHA256).Hash.ToLowerInvariant() -ne $lock.sha256) { throw 'Checksum do Java em cache inválido. Remova apenas o ZIP em desktop/.cache e tente novamente.' }
$runtime = Join-Path $resources 'runtime'
$stamp = Join-Path $runtime '.runtime-sha256'
if (-not (Test-Path -LiteralPath $stamp) -or (Get-Content -Raw $stamp).Trim() -ne $lock.sha256 -or -not (Test-Path (Join-Path $runtime 'bin/java.exe'))) {
    $staging = Join-Path $cache ('extract-' + [guid]::NewGuid().ToString('N'))
    Expand-Archive -LiteralPath $archive -DestinationPath $staging
    $extracted = @(Get-ChildItem -LiteralPath $staging -Directory)
    if ($extracted.Count -ne 1 -or -not (Test-Path (Join-Path $extracted[0].FullName 'bin/java.exe'))) { throw 'Estrutura inesperada no pacote Temurin.' }
    # Only the generated runtime directory inside this workspace can be replaced.
    $resolvedRuntime = [IO.Path]::GetFullPath($runtime)
    $allowedRoot = [IO.Path]::GetFullPath($resources) + [IO.Path]::DirectorySeparatorChar
    if (-not $resolvedRuntime.StartsWith($allowedRoot, [StringComparison]::OrdinalIgnoreCase)) { throw 'Destino fora dos recursos desktop.' }
    if (Test-Path -LiteralPath $resolvedRuntime) { Remove-Item -LiteralPath $resolvedRuntime -Recurse -Force }
    Move-Item -LiteralPath $extracted[0].FullName -Destination $resolvedRuntime
    Set-Content -LiteralPath $stamp -Value $lock.sha256 -Encoding ascii
}
New-Item -ItemType Directory -Force (Join-Path $resources 'engine'), (Join-Path $resources 'manual') | Out-Null
Copy-Item -LiteralPath $jar -Destination (Join-Path $resources 'engine/ferrovia.jar') -Force
Copy-Item -LiteralPath (Join-Path $projectRoot 'README.md') -Destination (Join-Path $resources 'manual/README.md') -Force
Copy-Item -LiteralPath (Join-Path $projectRoot 'docs/AUDITORIA_IMPLEMENTADA.md') -Destination (Join-Path $resources 'manual/AUDITORIA_IMPLEMENTADA.md') -Force
Copy-Item -LiteralPath (Join-Path $desktopRoot 'THIRD-PARTY-NOTICES.md') -Destination (Join-Path $resources 'manual/THIRD-PARTY-NOTICES.md') -Force
# A self-contained offline manual, without script or remote dependencies.
$manual = [System.Net.WebUtility]::HtmlEncode((Get-Content -Raw -Encoding utf8 (Join-Path $projectRoot 'README.md')))
$html = '<!doctype html><html lang="pt-BR"><meta charset="utf-8"><meta name="viewport" content="width=device-width"><meta http-equiv="Content-Security-Policy" content="default-src ''none''; style-src ''unsafe-inline''"><title>Ferrovia — Manual</title><style>body{margin:3rem auto;padding:0 2rem;max-width:1000px;background:#0b1220;color:#e6edf5;font:16px/1.7 system-ui}pre{white-space:pre-wrap;overflow-wrap:anywhere;font:inherit}h1{color:#6ee7b7}</style><h1>Ferrovia · Manual de uso</h1><p>Use Ctrl+F para procurar um assunto. Este manual também está disponível em README.md na pasta de instalação.</p><pre>' + $manual + '</pre></html>'
Set-Content -LiteralPath (Join-Path $resources 'manual/index.html') -Value $html -Encoding utf8
# Native icon generated from simple geometry; the PNG is embedded in an ICO container.
Add-Type -AssemblyName System.Drawing
$bitmap = New-Object System.Drawing.Bitmap 256, 256
$graphics = [System.Drawing.Graphics]::FromImage($bitmap)
$graphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
$graphics.Clear([System.Drawing.Color]::FromArgb(11, 18, 32))
$green = New-Object System.Drawing.SolidBrush ([System.Drawing.Color]::FromArgb(52, 211, 153))
$dark = New-Object System.Drawing.SolidBrush ([System.Drawing.Color]::FromArgb(11, 18, 32))
$pen = New-Object System.Drawing.Pen ([System.Drawing.Color]::FromArgb(52, 211, 153)), 12
$graphics.FillRectangle($green, 62, 34, 132, 160)
$graphics.FillRectangle($dark, 78, 54, 100, 64)
$graphics.FillEllipse($dark, 80, 148, 23, 23)
$graphics.FillEllipse($dark, 153, 148, 23, 23)
$graphics.DrawLine($pen, 94, 190, 68, 224)
$graphics.DrawLine($pen, 162, 190, 188, 224)
$graphics.DrawLine($pen, 77, 213, 179, 213)
$png = New-Object IO.MemoryStream
$bitmap.Save($png, [System.Drawing.Imaging.ImageFormat]::Png)
$bytes = $png.ToArray()
[IO.File]::WriteAllBytes((Join-Path $build 'icon.png'), $bytes)
$ico = [IO.File]::Create((Join-Path $build 'icon.ico'))
$writer = New-Object IO.BinaryWriter $ico
try {
    $writer.Write([uint16]0); $writer.Write([uint16]1); $writer.Write([uint16]1)
    $writer.Write([byte]0); $writer.Write([byte]0); $writer.Write([byte]0); $writer.Write([byte]0)
    $writer.Write([uint16]1); $writer.Write([uint16]32); $writer.Write([uint32]$bytes.Length); $writer.Write([uint32]22)
    $writer.Write($bytes)
} finally { $writer.Dispose(); $png.Dispose(); $graphics.Dispose(); $bitmap.Dispose(); $green.Dispose(); $dark.Dispose(); $pen.Dispose() }
Write-Host 'Motor, Java redistribuível, manual e ícones preparados.'
