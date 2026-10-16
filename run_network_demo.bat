@echo off
chcp 65001 > nul
title DEMO MANG P2P TCP SOCKETS - LAP TRINH MANG
echo =========================================================================
echo    DANG KHOI CHAY DEMO MANG P2P TCP SOCKETS CLUSTER (PORT 8001-8004)
echo =========================================================================
echo.

set "JAVA_HOME=C:\Program Files\Apache NetBeans\jdk"
set "PATH=%JAVA_HOME%\bin;%PATH%"

cd /d "%~dp0"

if not exist "target\classes\com\pow\simulator\network\NetworkDemo.class" (
    echo [Thong bao] Dang bien dich ma nguon Java bang Maven...
    call "C:\Program Files\Apache NetBeans\java\maven\bin\mvn.cmd" compile
)

java -cp "target\classes" com.pow.simulator.network.NetworkDemo

echo.
echo =========================================================================
echo    DEMO MANG P2P HOAN TAT! BAM PHIM BAT KY DE DONG CUA SO.
echo =========================================================================
pause > nul
