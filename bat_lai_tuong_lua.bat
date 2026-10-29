@echo off
chcp 65001 > nul
title BAT LAI TUONG LUA WINDOWS FIREWALL
echo Dang yeu cau quyen Administrator de bat lai tuong lua Windows...
powershell -Command "Start-Process powershell -Verb RunAs -ArgumentList '-NoProfile -ExecutionPolicy Bypass -Command `"netsh advfirewall set allprofiles state on; Write-Host ''`n[THANH CONG] DA BAT LAI TUONG LUA WINDOWS FIREWALL!'' -ForegroundColor Green; Start-Sleep -Seconds 3`"'"
