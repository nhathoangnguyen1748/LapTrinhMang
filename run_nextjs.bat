@echo off
chcp 65001 > nul
title GIAO DIEN NEXT.JS 14 - MO PHONG P2P BLOCKCHAIN
echo =========================================================================
echo    DANG KHOI CHAY GIAO DIEN NEXT.JS 14 (PORT 3000)
echo =========================================================================
echo.
echo Luu y: Hay dam bao ban da bat Backend Java bang cach chay run_gui.bat truoc!
echo.

cd /d "%~dp0frontend-nextjs"

if not exist "node_modules\" (
    echo Phat hien chua cai dat thu vien. Dang chay npm install...
    call npm install
)

echo Dang khoi dong Next.js Dev Server...
echo Giao dien se mo tai: http://localhost:3000
echo.

call npm run dev

pause > nul
