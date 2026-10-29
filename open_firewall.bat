@echo off
chcp 65001 > nul
title MO TUONG LUA WINDOWS FIREWALL - CONG 8000 DEN 8010
echo =========================================================================
echo    DANG YEU CAU QUYEN ADMINISTRATOR DE MO TUONG LUA CHO CONG P2P
echo =========================================================================
echo.
powershell -Command "Start-Process powershell -Verb RunAs -ArgumentList '-NoProfile -ExecutionPolicy Bypass -Command `"netsh advfirewall firewall add rule name=''Blockchain_P2P_Ports'' dir=in action=allow protocol=TCP localport=8000-8010; Write-Host ''`n[THANH CONG] DA MO TUONG LUA CHO CAC CONG 8000 DEN 8010!'' -ForegroundColor Green; Start-Sleep -Seconds 3`"'"
