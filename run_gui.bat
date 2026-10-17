@echo off
chcp 65001 > nul
title GIAO DIEN TRUC QUAN P2P BLOCKCHAIN - PORT 8000
echo =========================================================================
echo    DANG KHOI CHAY GIAO DIEN TRUC QUAN P2P BLOCKCHAIN (PORT 8000)
echo =========================================================================
echo.

set "JAVA_HOME=C:\Program Files\Apache NetBeans\jdk"
set "PATH=%JAVA_HOME%\bin;%PATH%"

cd /d "%~dp0"

:: Kiem tra neu chua bien dich thi tien hanh bien dich
if not exist "target\classes\com\pow\simulator\web\WebLauncher.class" (
    echo [Thong bao] Dang bien dich ma nguon Java bang Maven...
    call "C:\Program Files\Apache NetBeans\java\maven\bin\mvn.cmd" compile
    echo [Thong bao] Bien dich hoan tat!
)

echo [1/2] Dang mo trinh duyet mac dinh tai: http://localhost:8000 ...
start "" "http://localhost:8000"

echo [2/2] Dang khoi dong may chu Web va Cum mang P2P TCP Sockets...
echo.
java -cp "target\classes" com.pow.simulator.web.WebLauncher

echo.
echo =========================================================================
echo    MAY CHU DA DUNG. BAM PHIM BAT KY DE DONG CUA SO.
echo =========================================================================
pause > nul
