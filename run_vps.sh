#!/bin/bash
# =========================================================================
#   SCRIPT KHOI CHAY P2P BLOCKCHAIN TREN VPS LINUX (UBUNTU / DEBIAN)
# =========================================================================

echo "========================================================================="
echo "   KHOI CHAY P2P BLOCKCHAIN NETWORK TREN VPS LINUX (PORT 8000)          "
echo "========================================================================="

# 1. Kiem tra Java
if ! command -v java &> /dev/null; then
    echo "[!] Khong tim thay Java. Vui long cai dat Java 21 bang lenh:"
    echo "    sudo apt update && sudo apt install -y openjdk-21-jdk maven"
    exit 1
fi

# 2. Kiem tra bien dich
if [ ! -f "target/classes/com/pow/simulator/web/WebLauncher.class" ]; then
    echo "[*] Dang bien dich du an bang Maven..."
    mvn clean compile
fi

# 3. Lay dia chi IP cong cong cua VPS
PUBLIC_IP=$(curl -s https://ifconfig.me || curl -s https://api.ipify.org || echo "IP_VPS")

echo ""
echo "========================================================================="
echo " >>> BLOCKCHAIN NODE DA SAN SANG! <<<"
echo " Truy cap giao dien tu may tinh hoac dien thoai qua dia chi:"
echo " http://${PUBLIC_IP}:8000"
echo "========================================================================="
echo ""

# 4. Khoi chay WebLauncher
java -cp "target/classes" com.pow.simulator.web.WebLauncher
