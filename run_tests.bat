@echo off
chcp 65001 > nul
title KIEM THU TU DONG JUNIT 5 (21/21 TESTS)
echo =========================================================================
echo    DANG CHAY BO KIEM THU TU DONG JUNIT 5 (BLOCKCHAIN & P2P NETWORK)
echo =========================================================================
echo.

set "JAVA_HOME=C:\Program Files\Apache NetBeans\jdk"
set "PATH=%JAVA_HOME%\bin;%PATH%"

cd /d "%~dp0"

call "C:\Program Files\Apache NetBeans\java\maven\bin\mvn.cmd" test

echo.
echo =========================================================================
echo    KIEM THU HOAN TAT! BAM PHIM BAT KY DE DONG CUA SO.
echo =========================================================================
pause > nul
