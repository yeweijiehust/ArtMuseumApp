$ErrorActionPreference = "Stop"
$baseUrl = "https://artmuseum-w9mm.onrender.com"
$fixtureDir = Join-Path $PSScriptRoot "..\app\build\contract-fixtures"
New-Item -ItemType Directory -Force -Path $fixtureDir | Out-Null

function Get-Fixture {
    param(
        [string]$Path,
        [string]$FileName,
        [int]$ExpectedStatus
    )
    $output = Join-Path $fixtureDir $FileName
    $status = & curl.exe -sS -o $output -w "%{http_code}" "$baseUrl$Path"
    if ($LASTEXITCODE -ne 0 -or [int]$status -ne $ExpectedStatus) {
        throw "Expected HTTP $ExpectedStatus for $Path but received $status"
    }
    Get-Content $output -Raw | ConvertFrom-Json | Out-Null
}

Get-Fixture "/api/health" "health.json" 200
Get-Fixture "/api/images?limit=2" "gallery.json" 200
$gallery = Get-Content (Join-Path $fixtureDir "gallery.json") -Raw | ConvertFrom-Json
if ($gallery.items.Count -lt 1) {
    throw "The live gallery must contain at least one image"
}
Get-Fixture "/api/images/$($gallery.items[0].id)" "image.json" 200
Get-Fixture "/api/docs/json" "openapi.json" 200
Get-Fixture "/api/auth/me" "unauthorized.json" 401

$env:ARTMUSEUM_CONTRACT_FIXTURE_DIR = (Resolve-Path $fixtureDir).Path
try {
    & (Join-Path $PSScriptRoot "..\gradlew.bat") testDebugUnitTest --tests "*LiveContractDeserializationTest"
    if ($LASTEXITCODE -ne 0) {
        throw "Live contract tests failed"
    }
} finally {
    Remove-Item Env:ARTMUSEUM_CONTRACT_FIXTURE_DIR -ErrorAction SilentlyContinue
}
