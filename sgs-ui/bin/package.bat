@echo off
echo.
echo [Ϣ] װWeḅnode_modulesļ
echo.

%~d0
cd %~dp0

cd ..
npm install --registry=https://registry.npmmirror.com

pause