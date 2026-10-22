#!/usr/bin/env bash
# Blockchain E-Voting Node Launcher for Linux/MacOS
echo "========================================================================="
echo "   HỆ THỐNG BẦU CỬ ĐIỆN TỬ BLOCKCHAIN P2P NODE (JAVA 21)"
echo "========================================================================="

if [ -f "target/blockchain-evoting-node.jar" ]; then
    echo "[INFO] Running from JAR package: target/blockchain-evoting-node.jar..."
    java -jar target/blockchain-evoting-node.jar "$@"
else
    echo "[INFO] Running from compiled classes..."
    java -cp target/classes com.pow.simulator.web.WebLauncher "$@"
fi
