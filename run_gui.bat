@echo off
chcp 65001 > nul
title GIAO DIEN TRUC QUAN P2P BLOCKCHAIN - LAP TRINH MANG
echo =========================================================================
echo    DANG KHOI CHAY GIAO DIEN TRUC QUAN P2P BLOCKCHAIN (PORT 8080)
echo =========================================================================
echo.

set "JAVA_HOME=C:\Program Files\Apache NetBeans\jdk"
set "PATH=%JAVA_HOME%\bin;%PATH%"

cd /d "%~dp0"

echo Dang bien dich va khoi dong Web Dashboard...
echo Giao dien se tu dong mo tai http://localhost:8080 tren trinh duyet!
echo.

call "C:\Program Files\Apache NetBeans\java\maven\bin\mvn.cmd" compile exec:java -Dexec.mainClass="com.pow.simulator.web.WebLauncher"

echo.
echo =========================================================================
echo    CHUONG TRINH DA DONG! BAM PHIM BAT KY DE THOAT.
echo =========================================================================
pause > nul
