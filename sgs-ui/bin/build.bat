@echo off
echo.
echo [Ϣ] Weḅdistļ
echo.

%~d0
cd %~dp0

cd ..
npm run build:prod

pause