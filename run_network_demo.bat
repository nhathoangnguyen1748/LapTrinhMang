@echo off
chcp 65001 > nul
title DEMO MANG P2P TCP SOCKETS - LAP TRINH MANG
echo =========================================================================
echo    DANG KHOI CHAY DEMO MANG P2P TCP SOCKETS CLUSTER (PORT 8001-8004)
echo =========================================================================
echo.

cd /d "%~dp0"

:: TU DONG TIM KIEM JAVA 21+ TREN MAY
set "JAVA_CMD="

where java >nul 2>nul
if %errorlevel% equ 0 (
    set "JAVA_CMD=java"
    goto :RUN_DEMO
)

if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" (
        set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
        set "PATH=%JAVA_HOME%\bin;%PATH%"
        goto :RUN_DEMO
    )
)

for %%D in (
    "C:\Program Files\Java\jdk-21*"
    "C:\Program Files\Eclipse Adoptium\jdk-21*"
    "C:\Program Files\Microsoft\jdk-21*"
    "C:\Program Files\BellSoft\LibericaJDK-21*"
    "C:\Program Files\Amazon Corretto\jdk21*"
    "C:\Program Files\Apache NetBeans\jdk"
    "C:\Program Files\Java\jdk*"
) do (
    if exist "%%~fD\bin\java.exe" (
        set "JAVA_HOME=%%~fD"
        set "JAVA_CMD=%%~fD\bin\java.exe"
        set "PATH=%%~fD\bin;%PATH%"
        goto :RUN_DEMO
    )
)

echo [LOI] Khong tim thay Java 21 tren may cua ban!
pause
exit /b 1

:RUN_DEMO
if not exist "target\classes\com\pow\simulator\network\NetworkDemo.class" (
    echo [Thong bao] Dang bien dich ma nguon Java bang Maven / Javac...
    where mvn >nul 2>nul
    if %errorlevel% equ 0 (
        call mvn compile -DskipTests
    ) else if exist "C:\Program Files\Apache NetBeans\java\maven\bin\mvn.cmd" (
        call "C:\Program Files\Apache NetBeans\java\maven\bin\mvn.cmd" compile -DskipTests
    )
)

"%JAVA_CMD%" -cp "target\classes;target\blockchain-evoting-node.jar" com.pow.simulator.network.NetworkDemo

echo.
echo =========================================================================
echo    DEMO MANG P2P HOAN TAT! BAM PHIM BAT KY DE DONG CUA SO.
echo =========================================================================
pause > nul
