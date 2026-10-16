@echo off
chcp 65001 > nul
title CHUONG TRINH P2P BLOCKCHAIN & LAP TRINH MANG (TCP SOCKETS)
echo =========================================================================
echo    DANG KHOI CHAY HE THONG P2P BLOCKCHAIN & LEDGER NETWORK (JAVA 21)
echo =========================================================================
echo.

set "JAVA_HOME=C:\Program Files\Apache NetBeans\jdk"
set "PATH=%JAVA_HOME%\bin;%PATH%"

cd /d "%~dp0"

echo Dang bien dich va khoi chay chuong trinh chinh (App.java)...
echo.

call "C:\Program Files\Apache NetBeans\java\maven\bin\mvn.cmd" compile exec:java

echo.
echo =========================================================================
echo    CHUONG TRINH DA CHAY XONG! BAM PHIM BAT KY DE DONG CUA SO.
echo =========================================================================
pause > nul
