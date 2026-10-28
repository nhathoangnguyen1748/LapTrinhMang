@echo off
chcp 65001 > nul
title NGROK TCP TUNNEL - KET NOI P2P NODE XUYEN INTERNET
echo =========================================================================
echo    NGROK TCP TUNNEL - MO CONG P2P SOCKET RA INTERNET (CHO BAN BE O XA)
echo =========================================================================
echo.
cd /d "%~dp0"

if not exist "ngrok.exe" (
    echo [LOI] Khong tim thay ngrok.exe trong thu muc!
    pause
    exit /b 1
)

echo [THONG TIN] Dang mo tunnel TCP cho cong P2P 8001...
echo.
echo HUONG DAN SU DUNG:
echo 1. Nhin vao dong "Forwarding", ban se thay dia chi co dang:
echo       tcp://0.tcp.ap.ngrok.io:12345
echo 2. Chi can copy phan "0.tcp.ap.ngrok.io:12345" (bo chu tcp://)
echo 3. Dien dia chi do vao file peer_seed.txt tren may ban be:
echo       0.tcp.ap.ngrok.io:12345
echo 4. Ban be chi can chay run_peer.bat la tu dong ket noi xuyen Internet!
echo =========================================================================
echo.

ngrok.exe tcp 8001
pause
