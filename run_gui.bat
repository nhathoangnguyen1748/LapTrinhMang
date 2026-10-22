@echo off
chcp 65001 > nul
title GIAO DIEN TRUC QUAN P2P BLOCKCHAIN - PORT 8000
echo =========================================================================
echo    DANG KHOI CHAY GIAO DIEN TRUC QUAN P2P BLOCKCHAIN (PORT 8000)
echo    Nhom: Nguyen Hoang Nhat - Nguyen Do Trung Luong - Le Quang Minh
echo =========================================================================
echo.

cd /d "%~dp0"

:: 1. TU DONG TIM KIEM JAVA 21+ TREN MAY
set "JAVA_CMD="

where java >nul 2>nul
if %errorlevel% equ 0 (
    set "JAVA_CMD=java"
    goto :CHECK_BUILD
)

if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" (
        set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
        set "PATH=%JAVA_HOME%\bin;%PATH%"
        goto :CHECK_BUILD
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
        goto :CHECK_BUILD
    )
)

echo [LOI] Khong tim thay Java 21 tren may cua ban!
echo Vui long cai dat JDK 21 tai: https://adoptium.net hoac https://www.oracle.com/java/
pause
exit /b 1

:CHECK_BUILD
:: 2. KIEM TRA NEU CHUA BIEN DICH THI TU DONG BIEN DICH
if not exist "target\classes\com\pow\simulator\web\WebLauncher.class" (
    echo [Thong bao] Dang bien dich ma nguon Java bang Maven / Javac...
    where mvn >nul 2>nul
    if %errorlevel% equ 0 (
        call mvn compile -DskipTests
    ) else if exist "C:\Program Files\Apache NetBeans\java\maven\bin\mvn.cmd" (
        call "C:\Program Files\Apache NetBeans\java\maven\bin\mvn.cmd" compile -DskipTests
    ) else (
        echo [Thong bao] Bien dich truc tiep bang javac...
        if not exist "target\classes" mkdir "target\classes"
        dir /s /b "src\main\java\*.java" > "target\sources.txt"
        javac -encoding UTF-8 -d target\classes @"target\sources.txt"
        del "target\sources.txt" >nul 2>nul
    )
    echo [Thong bao] Bien dich hoan tat!
)

:: 3. KHOI CHAY TRINH DUYET VA MAY CHU
echo [1/2] Dang mo trinh duyet mac dinh tai: http://localhost:8000 ...
start "" "http://localhost:8000"

echo [2/2] Dang khoi dong may chu Web va Cum mang P2P TCP Sockets...
echo.
"%JAVA_CMD%" -cp "target\classes;target\blockchain-evoting-node.jar" com.pow.simulator.web.WebLauncher

echo.
echo =========================================================================
echo    MAY CHU DA DUNG. BAM PHIM BAT KY DE DONG CUA SO.
echo =========================================================================
pause > nul
