@echo off
chcp 65001 >nul
title Blockchain E-Voting Node (HUTECH)
echo =========================================================================
echo    HỆ THỐNG BẦU CỬ ĐIỆN TỬ BLOCKCHAIN P2P NODE (JAVA 21)
echo    Nhóm: Nguyễn Hoàng Nhật (Lead) - Nguyễn Đỗ Trung Lương - Lê Quang Minh
echo =========================================================================
echo.

if exist "target\blockchain-evoting-node.jar" (
    echo [INFO] Đang khởi chạy Node từ file JAR đóng gói: target\blockchain-evoting-node.jar...
    java -jar target\blockchain-evoting-node.jar %*
) else (
    echo [INFO] Đang khởi chạy Node từ mã nguồn compiled classes...
    java -cp target\classes com.pow.simulator.web.WebLauncher %*
)

if %errorlevel% neq 0 (
    echo.
    echo [LOI] Khong the khoi chay Node. Vui long kiem tra da cai dat Java 21+ chua.
    pause
)
