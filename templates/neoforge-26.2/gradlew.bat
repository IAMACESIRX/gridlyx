@echo off
REM Remote bootstrap shim. The complete prepared vault carries the official Gradle wrapper JAR.
where gradle >nul 2>nul
if %ERRORLEVEL%==0 (
  gradle %*
  exit /b %ERRORLEVEL%
)
echo Gradle 9.2.1 is required. Install Gradle or import the complete supplied MDK wrapper binary. 1>&2
exit /b 127
