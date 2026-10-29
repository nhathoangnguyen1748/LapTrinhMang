@echo off
chcp 65001 > nul
title NGROK TCP TUNNEL - KET NOI P2P NODE XUYEN INTERNET
echo =========================================================================
echo    NGROK TCP TUNNEL - MO CONG P2P SOCKET RA INTERNET (CHO BAN BE O XA)
echo =========================================================================
echo.
cd /d "%~dp0"

if not exist "ngrok.exe" (
    echo [THONG BAO] Dang tai ngrok phien ban moi nhat tu trang chu ngrok...
    powershell -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; try { Invoke-WebRequest -Uri 'https://bin.equinox.io/c/bNyj1mQVY4c/ngrok-v3-stable-windows-amd64.zip' -OutFile 'ngrok_latest.zip'; Expand-Archive -Path 'ngrok_latest.zip' -DestinationPath '.' -Force; Remove-Item 'ngrok_latest.zip'; Write-Host '[THANH CONG] Da tai va giai nen ngrok moi nhat!' } catch { Write-Host '[CANH BAO] File bi Windows Defender chan. Vui long bam Allow trong Windows Security!' }"
)

if not exist "ngrok.exe" (
    echo.
    echo [HUONG DAN] Neu van chua co ngrok.exe:
    echo 1. Vao cua so Windows Security vua mo tren man hinh
    echo 2. Bam vao muc bi chan (Threat blocked / PUA)
    echo 3. Bam "Actions" -> Chon "Allow on device"
    echo 4. Chay lai file start_ngrok_tcp.bat nay!
    echo.
    pause
    exit /b 1
)

echo [THONG TIN] Dang mo tunnel TCP cho cong P2P 8001...
echo.
echo =========================================================================
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
