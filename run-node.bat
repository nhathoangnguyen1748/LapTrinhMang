@echo off
chcp 65001 >nul
title Blockchain E-Voting Node (HUTECH)
echo =========================================================================
echo    HỆ THỐNG BẦU CỬ ĐIỆN TỬ BLOCKCHAIN P2P NODE (JAVA 21)
echo    Nhóm: Nguyễn Hoàng Nhật (Lead) - Nguyễn Đỗ Trung Lương - Lê Quang Minh
echo =========================================================================
echo.

cd /d "%~dp0"

:: TU DONG TIM KIEM JAVA 21+ TREN MAY
set "JAVA_CMD="

where java >nul 2>nul
if %errorlevel% equ 0 (
    set "JAVA_CMD=java"
    goto :RUN_NODE
)

if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" (
        set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
        set "PATH=%JAVA_HOME%\bin;%PATH%"
        goto :RUN_NODE
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
        goto :RUN_NODE
    )
)

echo [LOI] Khong tim thay Java 21 tren may cua ban!
echo Vui long cai dat JDK 21 tai: https://adoptium.net hoac https://www.oracle.com/java/
pause
exit /b 1

:RUN_NODE
if exist "target\blockchain-evoting-node.jar" (
    echo [INFO] Đang khởi chạy Node từ file JAR đóng gói: target\blockchain-evoting-node.jar...
    "%JAVA_CMD%" -jar target\blockchain-evoting-node.jar %*
) else (
    echo [INFO] Đang khởi chạy Node từ mã nguồn compiled classes...
    "%JAVA_CMD%" -cp target\classes com.pow.simulator.web.WebLauncher %*
)

if %errorlevel% neq 0 (
    echo.
    echo [LOI] Khong the khoi chay Node. Vui long kiem tra da cai dat Java 21+ chua.
    pause
)
