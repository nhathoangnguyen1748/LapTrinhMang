@echo off
chcp 65001 > nul
title KIEM THU TU DONG JUNIT 5 (34/34 TESTS)
echo =========================================================================
echo    DANG CHAY BO KIEM THU TU DONG JUNIT 5 (BLOCKCHAIN & P2P NETWORK)
echo =========================================================================
echo.

cd /d "%~dp0"

:: TU DONG TIM KIEM JAVA 21+ TREN MAY
set "JAVA_CMD="

where java >nul 2>nul
if %errorlevel% equ 0 (
    set "JAVA_CMD=java"
    goto :RUN_TESTS
)

if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\java.exe" (
        set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
        set "PATH=%JAVA_HOME%\bin;%PATH%"
        goto :RUN_TESTS
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
        goto :RUN_TESTS
    )
)

:RUN_TESTS
where mvn >nul 2>nul
if %errorlevel% equ 0 (
    call mvn test
) else if exist "C:\Program Files\Apache NetBeans\java\maven\bin\mvn.cmd" (
    call "C:\Program Files\Apache NetBeans\java\maven\bin\mvn.cmd" test
) else (
    echo [LOI] Khong tim thay Maven de chay bo test.
)

echo.
echo =========================================================================
echo    KIEM THU HOAN TAT! BAM PHIM BAT KY DE DONG CUA SO.
echo =========================================================================
pause > nul
