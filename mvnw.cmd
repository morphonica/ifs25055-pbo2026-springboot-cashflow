@echo off
setlocal
set "BASE_DIR=%~dp0"
set "PROPS=%BASE_DIR%.mvn\wrapper\maven-wrapper.properties"
set "DIST_URL="
for /f "usebackq tokens=1,* delims==" %%A in ("%PROPS%") do (
  if "%%A"=="distributionUrl" set "DIST_URL=%%B"
)
if "%DIST_URL%"=="" (
  echo distributionUrl tidak ditemukan di %PROPS% 1>&2
  exit /b 1
)
set "ZIP_URL=%DIST_URL:.tar.gz=.zip%"
for %%F in ("%ZIP_URL%") do set "ZIP_NAME=%%~nxF"
set "DIR_NAME=%ZIP_NAME:-bin.zip=%"
set "DISTS=%USERPROFILE%\.m2\wrapper\dists"
set "MVN_HOME=%DISTS%\%DIR_NAME%"
if not exist "%MVN_HOME%\bin\mvn.cmd" (
  echo Mengunduh Maven: %ZIP_URL% 1>&2
  if not exist "%DISTS%" mkdir "%DISTS%"
  powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference='Stop'; [Net.ServicePointManager]::SecurityProtocol=[Net.SecurityProtocolType]::Tls12; $z=Join-Path $env:TEMP '%ZIP_NAME%'; Invoke-WebRequest -UseBasicParsing -Uri '%ZIP_URL%' -OutFile $z; Expand-Archive -Force -Path $z -DestinationPath '%DISTS%'; Remove-Item $z"
  if errorlevel 1 exit /b 1
)
call "%MVN_HOME%\bin\mvn.cmd" %*
exit /b %ERRORLEVEL%
