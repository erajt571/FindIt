@echo off
setlocal enabledelayedexpansion

set "ROOT_DIR=%~dp0"
set "TOOLS_DIR=%ROOT_DIR%.tools"
set "MAVEN_DIR=%TOOLS_DIR%\apache-maven-3.9.9"
set "MAVEN_ZIP=%TOOLS_DIR%\apache-maven-3.9.9-bin.zip"
set "MAVEN_URL=https://archive.apache.org/dist/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.zip"

if not exist "%MAVEN_DIR%\bin\mvn.cmd" (
    if not exist "%TOOLS_DIR%" mkdir "%TOOLS_DIR%"
    powershell -NoProfile -ExecutionPolicy Bypass -Command "$ProgressPreference='SilentlyContinue'; Invoke-WebRequest -Uri '%MAVEN_URL%' -OutFile '%MAVEN_ZIP%'; Expand-Archive -Path '%MAVEN_ZIP%' -DestinationPath '%TOOLS_DIR%' -Force"
)

"%MAVEN_DIR%\bin\mvn.cmd" %*
