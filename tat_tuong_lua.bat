@echo off
chcp 65001 > nul
title TAT TUONG LUA WINDOWS FIREWALL
echo Dang yeu cau quyen Administrator de tat tuong lua Windows...
powershell -Command "Start-Process powershell -Verb RunAs -ArgumentList '-NoProfile -ExecutionPolicy Bypass -Command `"netsh advfirewall set allprofiles state off; Write-Host ''`n[THANH CONG] DA TAT HOAN TOAN TUONG LUA WINDOWS FIREWALL!'' -ForegroundColor Green; Start-Sleep -Seconds 3`"'"
