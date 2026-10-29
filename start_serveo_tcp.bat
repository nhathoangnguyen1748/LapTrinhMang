@echo off
chcp 65001 > nul
title SERVEO TCP TUNNEL - MO CONG P2P SOCKET RA INTERNET
echo =========================================================================
echo    SERVEO TCP TUNNEL - DUNG SSH CO SAN CUA WINDOWS (KHONG CAN THE VISA)
echo =========================================================================
echo.
cd /d "%~dp0"

echo [THONG TIN] Dang ket noi toi serveo.net de mo cong TCP 8001 ra Internet...
echo.
echo =========================================================================
echo HUONG DAN SU DUNG:
echo 1. Nhin vao dong: "Allocated port XXXXX for remote forward..."
echo    hoac "Forwarding TCP connections from serveousercontent.com:XXXXX"
echo.
echo 2. Ban copy dia chi dang:
echo       serveo.net:XXXXX
echo    (Thay XXXXX bang con so port ma Serveo vua cap tren man hinh)
echo.
echo 3. Gui dia chi "serveo.net:XXXXX" cho ban be dien vao peer_seed.txt
echo    roi ban be chi can nhap dup run_peer.bat la tu dong thong mang!
echo.
echo 4. LUU Y: GIU NGUYEN CUA SO NAY TRONG SUOT QUA TRINH DEMO!
echo =========================================================================
echo.

ssh -R 0:localhost:8001 -o StrictHostKeyChecking=no serveo.net

if %errorlevel% neq 0 (
    echo.
    echo [THONG BAO] Ket noi Serveo da dung lai.
    pause
)
