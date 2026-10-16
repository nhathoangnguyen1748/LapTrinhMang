package com.pow.simulator.web;

/**
 * Modern Cyber Visual Dashboard for the PoW Blockchain P2P Network Simulation.
 * Served natively by Java without requiring any external Node.js/npm dependencies.
 */
public final class DashboardHtmlTemplate {

    private DashboardHtmlTemplate() {
    }

    public static String getHtml() {
        return """
<!DOCTYPE html>
<html lang="vi" class="dark">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>P2P Blockchain Network Simulator - Lập Trình Mạng</title>
    <!-- Tailwind CSS CDN -->
    <script src="https://cdn.tailwindcss.com"></script>
    <script>
        tailwind.config = {
            darkMode: 'class',
            theme: {
                extend: {
                    colors: {
                        cyber: {
                            bg: '#090d16',
                            card: '#0f172a',
                            border: '#1e293b',
                            accent: '#38bdf8',
                            miner: '#f59e0b',
                            relay: '#06b6d4',
                            wallet: '#8b5cf6',
                            sync: '#10b981',
                            danger: '#ef4444'
                        }
                    },
                    fontFamily: {
                        mono: ['"JetBrains Mono"', 'Consolas', 'monospace'],
                        sans: ['Inter', 'system-ui', 'sans-serif']
                    }
                }
            }
        }
    </script>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&family=JetBrains+Mono:wght@400;500;700&display=swap" rel="stylesheet">
    <style>
        body {
            background-color: #090d16;
            color: #f1f5f9;
            font-family: 'Inter', sans-serif;
            background-image: radial-gradient(rgba(56, 189, 248, 0.04) 1px, transparent 1px);
            background-size: 24px 24px;
        }
        .code-font {
            font-family: 'JetBrains Mono', monospace;
        }
        /* Custom scrollbar */
        ::-webkit-scrollbar {
            width: 6px;
            height: 6px;
        }
        ::-webkit-scrollbar-track {
            background: #090d16;
        }
        ::-webkit-scrollbar-thumb {
            background: #334155;
            border-radius: 3px;
        }
        ::-webkit-scrollbar-thumb:hover {
            background: #475569;
        }
        @keyframes pulseGlow {
            0%, 100% { opacity: 0.4; }
            50% { opacity: 0.9; }
        }
        .pulse-glow {
            animation: pulseGlow 2s infinite ease-in-out;
        }
    </style>
</head>
<body class="min-h-screen flex flex-col">

    <!-- Top Navigation Header -->
    <header class="border-b border-slate-800 bg-slate-950/80 backdrop-blur sticky top-0 z-50 px-6 py-3">
        <div class="max-w-7xl mx-auto flex flex-col md:flex-row items-center justify-between gap-4">
            <div class="flex items-center gap-3">
                <div class="w-10 h-10 rounded-xl bg-gradient-to-tr from-cyan-500 to-indigo-600 flex items-center justify-center shadow-lg shadow-cyan-500/20 text-white font-bold text-xl">
                    ⛓️
                </div>
                <div>
                    <div class="flex items-center gap-2">
                        <h1 class="text-lg font-bold text-white tracking-wide">P2P Blockchain Network Simulator</h1>
                        <span class="px-2 py-0.5 text-xs font-semibold rounded bg-cyan-950 text-cyan-400 border border-cyan-800">
                            TCP Sockets & Virtual Threads
                        </span>
                    </div>
                    <p class="text-xs text-slate-400">
                        Đồ Án Môn Học: <span class="text-slate-300 font-medium">Lập Trình Mạng</span> | Trường Đại Học Công Nghệ TP.HCM (HUTECH)
                    </p>
                </div>
            </div>

            <!-- Team Members & Connection Status -->
            <div class="flex items-center gap-3 text-xs">
                <div class="hidden lg:flex items-center gap-2 bg-slate-900 border border-slate-800 px-3 py-1.5 rounded-lg text-slate-300">
                    <span class="text-slate-400">Thành viên:</span>
                    <span class="font-semibold text-amber-400">Hoàng Nhật (2380601549)</span> •
                    <span class="font-semibold text-cyan-400">Trung Lương (2380601305)</span> •
                    <span class="font-semibold text-purple-400">Quang Minh (2380601342)</span>
                </div>
                <div id="connectionStatus" class="flex items-center gap-2 px-3 py-1.5 rounded-lg bg-emerald-950/80 border border-emerald-700/50 text-emerald-400 font-medium">
                    <span class="w-2 h-2 rounded-full bg-emerald-400 animate-ping"></span>
                    <span>Backend Port 8080: Online</span>
                </div>
            </div>
        </div>
    </header>

    <!-- Main Content Container -->
    <main class="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 py-6 space-y-6">

        <!-- Control Center (Action Buttons) -->
        <section class="bg-slate-900/90 border border-slate-800 rounded-2xl p-5 shadow-xl backdrop-blur">
            <div class="flex flex-col md:flex-row items-center justify-between gap-4 mb-4">
                <div>
                    <h2 class="text-base font-bold text-white flex items-center gap-2">
                        <span>⚡ Trung Tâm Điều Khiển Mạng P2P</span>
                        <span class="text-xs font-normal text-slate-400">(Tác vụ phát tán TCP Gossip & Khai thác Đồng thuận)</span>
                    </h2>
                </div>
                <div class="flex items-center gap-2">
                    <button onclick="resetNetwork()" class="px-3 py-1.5 text-xs font-medium bg-slate-800 hover:bg-slate-700 text-slate-300 rounded-lg border border-slate-700 transition flex items-center gap-1.5">
                        <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"/></svg>
                        Reset Genesis
                    </button>
                    <button onclick="fetchStatusAndChain()" class="px-3 py-1.5 text-xs font-medium bg-cyan-950 hover:bg-cyan-900 text-cyan-300 rounded-lg border border-cyan-800 transition flex items-center gap-1.5">
                        <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"/></svg>
                        Làm Mới Dữ Liệu
                    </button>
                </div>
            </div>

            <!-- Action Grid -->
            <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
                <!-- Action 1: Send Transaction -->
                <button onclick="sendTransaction()" class="group relative overflow-hidden bg-gradient-to-br from-purple-950/60 to-slate-900 border border-purple-800/50 hover:border-purple-500/80 rounded-xl p-4 text-left transition-all duration-200 hover:shadow-lg hover:shadow-purple-950/50">
                    <div class="flex items-center justify-between mb-2">
                        <span class="text-2xl">💸</span>
                        <span class="text-[10px] font-semibold uppercase px-2 py-0.5 rounded bg-purple-900/60 text-purple-300 border border-purple-700/50">Gossip TCP</span>
                    </div>
                    <div class="text-sm font-bold text-white group-hover:text-purple-300 transition">1. Gửi Giao Dịch</div>
                    <p class="text-xs text-slate-400 mt-1">Node C (Ví) phát tán Tx qua Node B rồi tới Node A vào Mempool.</p>
                </button>

                <!-- Action 2: Mine Block -->
                <button onclick="mineBlock()" id="btnMine" class="group relative overflow-hidden bg-gradient-to-br from-amber-950/60 to-slate-900 border border-amber-800/50 hover:border-amber-500/80 rounded-xl p-4 text-left transition-all duration-200 hover:shadow-lg hover:shadow-amber-950/50">
                    <div class="flex items-center justify-between mb-2">
                        <span class="text-2xl">⛏️</span>
                        <span class="text-[10px] font-semibold uppercase px-2 py-0.5 rounded bg-amber-900/60 text-amber-300 border border-amber-700/50">Proof-of-Work</span>
                    </div>
                    <div class="text-sm font-bold text-white group-hover:text-amber-300 transition flex items-center gap-2">
                        <span>2. Khai Thác Block</span>
                        <span id="miningSpinner" class="hidden w-3 h-3 border-2 border-amber-400 border-t-transparent rounded-full animate-spin"></span>
                    </div>
                    <p class="text-xs text-slate-400 mt-1">Node A chạy Virtual Threads tìm Nonce, đóng block và broadcast TCP.</p>
                </button>

                <!-- Action 3: Connect Node D (Sync) -->
                <button onclick="connectNodeD()" class="group relative overflow-hidden bg-gradient-to-br from-emerald-950/60 to-slate-900 border border-emerald-800/50 hover:border-emerald-500/80 rounded-xl p-4 text-left transition-all duration-200 hover:shadow-lg hover:shadow-emerald-950/50">
                    <div class="flex items-center justify-between mb-2">
                        <span class="text-2xl">🌐</span>
                        <span class="text-[10px] font-semibold uppercase px-2 py-0.5 rounded bg-emerald-900/60 text-emerald-300 border border-emerald-700/50">Sync Nakamoto</span>
                    </div>
                    <div class="text-sm font-bold text-white group-hover:text-emerald-300 transition">3. Kết Nối Node D</div>
                    <p class="text-xs text-slate-400 mt-1">Bật Node D (Port 8004), gửi TCP Handshake & Sync toàn bộ Ledger.</p>
                </button>

                <!-- Action 4: Tamper Attack -->
                <button onclick="tamperAttack()" class="group relative overflow-hidden bg-gradient-to-br from-rose-950/60 to-slate-900 border border-rose-800/50 hover:border-rose-500/80 rounded-xl p-4 text-left transition-all duration-200 hover:shadow-lg hover:shadow-rose-950/50">
                    <div class="flex items-center justify-between mb-2">
                        <span class="text-2xl">⚠️</span>
                        <span class="text-[10px] font-semibold uppercase px-2 py-0.5 rounded bg-rose-900/60 text-rose-300 border border-rose-700/50">Audit Check</span>
                    </div>
                    <div class="text-sm font-bold text-white group-hover:text-rose-300 transition">4. Thử Tấn Công Giả Mạo</div>
                    <p class="text-xs text-slate-400 mt-1">Sửa lén Tx trong Block #1. Kiểm tra tính toàn vẹn Merkle Root & Hash.</p>
                </button>
            </div>
        </section>

        <!-- Dynamic Attack Alert Banner (if tampered) -->
        <div id="tamperAlertBanner" class="hidden bg-rose-950/90 border-2 border-rose-600 text-rose-200 p-4 rounded-xl shadow-2xl flex items-start gap-4">
            <div class="text-3xl">🚨</div>
            <div class="flex-1">
                <div class="font-bold text-white text-base">CẢNH BÁO TẤN CÔNG GIẢ MẠO ĐÃ ĐƯỢC PHÁT HIỆN!</div>
                <div id="tamperAlertText" class="text-xs text-rose-300 mt-1 code-font"></div>
                <div class="mt-2 text-xs text-rose-400">
                    Tính chất mật mã học (SHA-256 + Merkle Tree) giúp toàn mạng phát hiện sự sai lệch ngay lập tức. Bấm "Reset Genesis" để khôi phục.
                </div>
            </div>
        </div>

        <!-- Section 2: Interactive P2P Network Topology & Node Cards -->
        <section class="grid grid-cols-1 lg:grid-cols-12 gap-6">

            <!-- Left: Visual Canvas Network Graph (7 cols) -->
            <div class="lg:col-span-7 bg-slate-900/90 border border-slate-800 rounded-2xl p-5 shadow-xl flex flex-col">
                <div class="flex items-center justify-between mb-3">
                    <div class="flex items-center gap-2">
                        <span class="text-lg">🛰️</span>
                        <h2 class="text-sm font-bold text-white">Sơ Đồ Mạng P2P TCP Sockets (Topology)</h2>
                    </div>
                    <span class="text-xs text-cyan-400 code-font">Frame: 4-Byte Prefix + JSON</span>
                </div>

                <!-- Canvas Container -->
                <div class="relative w-full h-[320px] bg-slate-950 rounded-xl border border-slate-800/80 overflow-hidden flex items-center justify-center">
                    <canvas id="networkCanvas" width="700" height="320" class="w-full h-full block"></canvas>
                    <div class="absolute bottom-2 left-3 text-[11px] text-slate-500 flex items-center gap-3">
                        <span class="flex items-center gap-1"><span class="w-2 h-2 rounded-full bg-amber-400 inline-block"></span> Node A (Miner)</span>
                        <span class="flex items-center gap-1"><span class="w-2 h-2 rounded-full bg-cyan-400 inline-block"></span> Node B (Relay)</span>
                        <span class="flex items-center gap-1"><span class="w-2 h-2 rounded-full bg-purple-400 inline-block"></span> Node C (Wallet)</span>
                        <span class="flex items-center gap-1"><span class="w-2 h-2 rounded-full bg-emerald-400 inline-block"></span> Node D (Sync)</span>
                    </div>
                </div>
                <p class="text-xs text-slate-400 mt-3 italic">
                    * Các hạt màu chạy dọc theo đường kết nối biểu diễn luồng gói tin TCP thực tế giữa các cổng 8001, 8002, 8003 và 8004.
                </p>
            </div>

            <!-- Right: Realtime Node Metric Cards (5 cols) -->
            <div class="lg:col-span-5 flex flex-col gap-3">
                <div class="text-sm font-bold text-white flex items-center justify-between">
                    <span class="flex items-center gap-1.5">
                        <span>📊</span> Trạng Thái Các Nút (Peer Nodes)
                    </span>
                    <span id="activeNodesBadge" class="text-xs bg-slate-800 text-slate-300 px-2 py-0.5 rounded border border-slate-700">3 Nodes Online</span>
                </div>

                <div id="nodesList" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-1 gap-2.5 flex-1 overflow-y-auto max-h-[340px]">
                    <!-- Dynamically populated node cards -->
                </div>
            </div>
        </section>

        <!-- Section 3: Visual Blockchain Ledger Explorer -->
        <section class="bg-slate-900/90 border border-slate-800 rounded-2xl p-5 shadow-xl">
            <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-2 mb-4">
                <div>
                    <h2 class="text-base font-bold text-white flex items-center gap-2">
                        <span>🧱 Sổ Cái Phân Tán (Blockchain Ledger Explorer)</span>
                    </h2>
                    <p class="text-xs text-slate-400">Các khối được liên kết chặt chẽ bằng SHA-256 Hash con trỏ (Previous Hash) và Merkle Root</p>
                </div>
                <div class="text-xs code-font text-slate-400 flex items-center gap-2">
                    <span>Tổng Chiều Cao Chuỗi:</span>
                    <span id="chainHeightDisplay" class="font-bold text-cyan-400 bg-cyan-950 px-2 py-0.5 rounded border border-cyan-800">1 Blocks</span>
                </div>
            </div>

            <!-- Horizontal Chain Blocks Viewer -->
            <div id="chainContainer" class="flex gap-4 overflow-x-auto pb-4 pt-2">
                <!-- Dynamically populated blocks -->
            </div>
        </section>

        <!-- Section 4: Live Event Logs Terminal -->
        <section class="bg-slate-950 border border-slate-800 rounded-2xl p-5 shadow-xl font-mono">
            <div class="flex items-center justify-between mb-3 border-b border-slate-800 pb-3">
                <div class="flex items-center gap-2">
                    <span class="w-3 h-3 rounded-full bg-rose-500 inline-block"></span>
                    <span class="w-3 h-3 rounded-full bg-amber-500 inline-block"></span>
                    <span class="w-3 h-3 rounded-full bg-emerald-500 inline-block"></span>
                    <span class="text-xs font-bold text-slate-300 ml-2">Console Nhật Ký TCP Socket & Đồng Thuận (Live Stream)</span>
                </div>
                <div class="flex items-center gap-2">
                    <label class="text-[11px] text-slate-400 flex items-center gap-1 cursor-pointer">
                        <input type="checkbox" id="autoScroll" checked class="rounded bg-slate-900 border-slate-700 text-cyan-500 focus:ring-0">
                        Auto-scroll
                    </label>
                    <button onclick="clearLogs()" class="text-[11px] text-slate-400 hover:text-slate-200 px-2 py-0.5 rounded bg-slate-900 border border-slate-800">
                        Xóa màn hình
                    </button>
                </div>
            </div>

            <div id="terminalLogs" class="h-48 overflow-y-auto text-xs space-y-1 text-slate-300 code-font select-text">
                <!-- Live logs populated here -->
            </div>
        </section>

        <!-- Section 5: Network Programming Architecture & Academic Explanation -->
        <section class="bg-slate-900/60 border border-slate-800/80 rounded-2xl p-5 text-xs text-slate-400 space-y-3">
            <h3 class="font-bold text-sm text-slate-200 flex items-center gap-2">
                <span>📘 Kiến Trúc Kỹ Thuật Môn Lập Trình Mạng Được Triển Khai</span>
            </h3>
            <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div class="p-3 bg-slate-950/60 rounded-xl border border-slate-800">
                    <div class="font-semibold text-cyan-400 mb-1">1. TCP Sockets & Framing</div>
                    <p class="text-slate-400">
                        Sử dụng <code class="text-cyan-300">ServerSocket</code> và <code class="text-cyan-300">Socket</code> nguyên bản. Thiết kế 4-byte Big-Endian length-prefix chống hiện tượng phân mảnh gói tin TCP (packet fragmentation/stream boundary issues).
                    </p>
                </div>
                <div class="p-3 bg-slate-950/60 rounded-xl border border-slate-800">
                    <div class="font-semibold text-purple-400 mb-1">2. Java 21 Virtual Threads</div>
                    <p class="text-slate-400">
                        Mỗi kết nối TCP và mỗi worker đào PoW được vận hành trên một <code class="text-purple-300">Virtual Thread</code> siêu nhẹ. Khả năng scale hàng nghìn kết nối đồng thời với chi phí RAM cực thấp.
                    </p>
                </div>
                <div class="p-3 bg-slate-950/60 rounded-xl border border-slate-800">
                    <div class="font-semibold text-emerald-400 mb-1">3. Gossip Protocol & Nakamoto</div>
                    <p class="text-slate-400">
                        Chuyển tiếp đa bước (Multi-hop relay) với cơ chế chống bão broadcast bằng UUID MessageId. Khi Node D kết nối, thuật toán Nakamoto tự động đồng bộ theo chuỗi hợp lệ dài nhất.
                    </p>
                </div>
            </div>
        </section>

    </main>

    <!-- Footer -->
    <footer class="border-t border-slate-800 py-4 text-center text-xs text-slate-500">
        Đồ Án Lập Trình Mạng • Proof-of-Work P2P Blockchain Simulator • Java 21 & WebSocket/REST Architecture
    </footer>

    <!-- JavaScript Logic & Network Simulation Scripts -->
    <script>
        // State variables
        let lastLogCount = 0;
        let isMining = false;
        let canvasParticles = [];

        // Fetch status and chain on load and every 1.2s
        window.addEventListener('DOMContentLoaded', () => {
            initCanvas();
            fetchStatusAndChain();
            setInterval(fetchStatusAndChain, 1200);
            animateCanvas();
        });

        async function fetchStatusAndChain() {
            try {
                const [statusRes, chainRes] = await Promise.all([
                    fetch('/api/status'),
                    fetch('/api/chain')
                ]);

                if (!statusRes.ok || !chainRes.ok) throw new Error('API response not OK');

                const status = await statusRes.json();
                const chain = await chainRes.json();

                updateStatusUI(status);
                updateChainUI(chain, status);
                updateLogsUI(status.logs);

                document.getElementById('connectionStatus').className = 
                    "flex items-center gap-2 px-3 py-1.5 rounded-lg bg-emerald-950/80 border border-emerald-700/50 text-emerald-400 font-medium";
                document.getElementById('connectionStatus').innerHTML = 
                    '<span class="w-2 h-2 rounded-full bg-emerald-400 animate-ping"></span><span>Backend Port 8080: Online</span>';

            } catch (err) {
                console.warn('Backend fetch error:', err);
                document.getElementById('connectionStatus').className = 
                    "flex items-center gap-2 px-3 py-1.5 rounded-lg bg-rose-950/80 border border-rose-700/50 text-rose-400 font-medium";
                document.getElementById('connectionStatus').innerHTML = 
                    '<span class="w-2 h-2 rounded-full bg-rose-400"></span><span>Backend Disconnected</span>';
            }
        }

        function updateStatusUI(status) {
            // Update Tamper Alert Banner
            const alertBanner = document.getElementById('tamperAlertBanner');
            const alertText = document.getElementById('tamperAlertText');
            if (status.isTampered) {
                alertBanner.classList.remove('hidden');
                alertText.textContent = 'Khối #' + status.tamperedBlockIndex + ' bị vi phạm: ' + status.tamperReason;
            } else {
                alertBanner.classList.add('hidden');
            }

            // Update Nodes List
            const nodesContainer = document.getElementById('nodesList');
            const nodes = status.nodes || [];
            document.getElementById('activeNodesBadge').textContent = nodes.length + ' Nodes Online';

            nodesContainer.innerHTML = nodes.map(node => {
                let badgeColor = "bg-amber-950 text-amber-400 border-amber-800";
                let dotColor = "bg-amber-400";
                if (node.id.includes("Node-B")) {
                    badgeColor = "bg-cyan-950 text-cyan-400 border-cyan-800";
                    dotColor = "bg-cyan-400";
                } else if (node.id.includes("Node-C")) {
                    badgeColor = "bg-purple-950 text-purple-400 border-purple-800";
                    dotColor = "bg-purple-400";
                } else if (node.id.includes("Node-D")) {
                    badgeColor = "bg-emerald-950 text-emerald-400 border-emerald-800";
                    dotColor = "bg-emerald-400";
                }

                return `
                    <div class="bg-slate-950 border border-slate-800 rounded-xl p-3 flex flex-col justify-between hover:border-slate-700 transition">
                        <div class="flex items-center justify-between">
                            <div class="flex items-center gap-2">
                                <span class="w-2.5 h-2.5 rounded-full ${dotColor}"></span>
                                <span class="font-bold text-white text-xs">${node.id}</span>
                            </div>
                            <span class="text-[10px] px-2 py-0.5 rounded code-font border ${badgeColor}">Port ${node.port}</span>
                        </div>
                        <div class="grid grid-cols-3 gap-2 mt-2 pt-2 border-t border-slate-900 text-center">
                            <div>
                                <div class="text-[10px] text-slate-500">Độ Cao</div>
                                <div class="text-xs font-bold text-cyan-400 code-font">${node.chainHeight}</div>
                            </div>
                            <div>
                                <div class="text-[10px] text-slate-500">Mempool</div>
                                <div class="text-xs font-bold text-amber-400 code-font">${node.mempoolCount} tx</div>
                            </div>
                            <div>
                                <div class="text-[10px] text-slate-500">Peers</div>
                                <div class="text-xs font-bold text-emerald-400 code-font">${node.peerCount} kết nối</div>
                            </div>
                        </div>
                    </div>
                `;
            }).join('');
        }

        function updateChainUI(chain, status) {
            const container = document.getElementById('chainContainer');
            document.getElementById('chainHeightDisplay').textContent = chain.length + ' Blocks';

            if (!chain || chain.length === 0) {
                container.innerHTML = '<div class="text-xs text-slate-500">Chưa có block nào...</div>';
                return;
            }

            container.innerHTML = chain.map((block, idx) => {
                const isGenesis = block.index === 0;
                const isCorrupted = block.isCompromised;
                
                let borderColor = isCorrupted ? 'border-rose-500 bg-rose-950/20' : (isGenesis ? 'border-cyan-800 bg-slate-950' : 'border-slate-800 bg-slate-950');
                let headerBadge = isCorrupted ? '<span class="px-2 py-0.5 text-[10px] rounded bg-rose-900 text-rose-300 font-bold">TAMPERED</span>' : (isGenesis ? '<span class="px-2 py-0.5 text-[10px] rounded bg-cyan-900 text-cyan-300 font-bold">GENESIS</span>' : '<span class="px-2 py-0.5 text-[10px] rounded bg-emerald-900 text-emerald-300 font-bold">VALID</span>');

                return `
                    <div class="flex items-center">
                        <div class="w-72 flex-shrink-0 border ${borderColor} rounded-xl p-4 shadow-lg transition hover:border-slate-600 flex flex-col justify-between">
                            <div>
                                <div class="flex items-center justify-between pb-2 border-b border-slate-800">
                                    <div class="flex items-center gap-1.5 font-bold text-white text-xs">
                                        <span>Block #${block.index}</span>
                                    </div>
                                    ${headerBadge}
                                </div>

                                <div class="mt-2.5 space-y-1.5 text-[11px] code-font">
                                    <div>
                                        <span class="text-slate-500 block text-[9px] uppercase">Hash:</span>
                                        <span class="text-cyan-300 font-bold truncate block" title="${block.hash}">
                                            ${block.shortHash || block.hash.substring(0, 16) + '...'}
                                        </span>
                                    </div>
                                    <div>
                                        <span class="text-slate-500 block text-[9px] uppercase">Prev Hash:</span>
                                        <span class="text-slate-400 truncate block" title="${block.previousHash}">
                                            ${block.previousHash.length > 16 ? block.previousHash.substring(0, 16) + '...' : block.previousHash}
                                        </span>
                                    </div>
                                    <div class="grid grid-cols-2 gap-2 pt-1">
                                        <div>
                                            <span class="text-slate-500 text-[9px] uppercase block">Nonce:</span>
                                            <span class="text-amber-400 font-bold">${block.nonce.toLocaleString()}</span>
                                        </div>
                                        <div>
                                            <span class="text-slate-500 text-[9px] uppercase block">Diff Bits:</span>
                                            <span class="text-purple-400 font-bold">${block.difficultyBits}</span>
                                        </div>
                                    </div>
                                    <div class="pt-1">
                                        <span class="text-slate-500 text-[9px] uppercase block">Merkle Root:</span>
                                        <span class="text-slate-400 truncate block text-[10px]" title="${block.merkleRoot}">
                                            ${block.merkleRoot.substring(0, 16)}...
                                        </span>
                                    </div>
                                </div>
                            </div>

                            <div class="mt-3 pt-2 border-t border-slate-900">
                                <div class="text-[10px] text-slate-400 font-semibold mb-1 flex items-center justify-between">
                                    <span>Giao dịch (${block.transactions.length}):</span>
                                </div>
                                <div class="max-h-24 overflow-y-auto space-y-1 pr-1">
                                    ${block.transactions.map(tx => `
                                        <div class="bg-slate-900 px-2 py-1 rounded text-[10px] border border-slate-800 flex items-center justify-between">
                                            <span class="text-slate-300 truncate max-w-[120px]">${tx.sender} → ${tx.recipient}</span>
                                            <span class="text-amber-400 font-bold ml-1">${tx.amount} COIN</span>
                                        </div>
                                    `).join('')}
                                </div>
                            </div>
                        </div>

                        ${idx < chain.length - 1 ? `
                            <div class="flex items-center justify-center px-2 text-cyan-500 text-lg">
                                ➔
                            </div>
                        ` : ''}
                    </div>
                `;
            }).join('');
        }

        function updateLogsUI(logs) {
            if (!logs || logs.length === 0) return;
            const container = document.getElementById('terminalLogs');

            if (logs.length !== lastLogCount) {
                lastLogCount = logs.length;
                container.innerHTML = logs.map(line => {
                    let color = 'text-slate-300';
                    if (line.includes('ATTACK') || line.includes('❌') || line.includes('Error')) {
                        color = 'text-rose-400 font-bold';
                    } else if (line.includes('MINED') || line.includes('PoW') || line.includes('⛏️')) {
                        color = 'text-amber-300';
                    } else if (line.includes('TX_BROADCAST') || line.includes('💸')) {
                        color = 'text-purple-300';
                    } else if (line.includes('TCP') || line.includes('HANDSHAKE') || line.includes('Node-')) {
                        color = 'text-cyan-300';
                    } else if (line.includes('SYNC')) {
                        color = 'text-emerald-300';
                    }
                    return `<div class="${color}">${escapeHtml(line)}</div>`;
                }).join('');

                if (document.getElementById('autoScroll').checked) {
                    container.scrollTop = container.scrollHeight;
                }
            }
        }

        function clearLogs() {
            document.getElementById('terminalLogs').innerHTML = '<div class="text-slate-500 italic">Đã xóa nhật ký hiển thị. Chờ sự kiện mới...</div>';
            lastLogCount = 0;
        }

        // ==================== Actions Calling Java REST API ====================

        async function sendTransaction() {
            const sender = 'Student_' + Math.floor(Math.random() * 900 + 100);
            const recipient = 'Merchant_Cafe_' + Math.floor(Math.random() * 50 + 1);
            const amount = parseFloat((Math.random() * 25 + 1).toFixed(2));

            triggerPacketAnimation('C', 'B');
            setTimeout(() => triggerPacketAnimation('B', 'A'), 200);

            try {
                const res = await fetch('/api/send-tx', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ sender, recipient, amount })
                });
                const data = await res.json();
                fetchStatusAndChain();
            } catch (err) {
                alert('Lỗi gửi Tx: ' + err.message);
            }
        }

        async function mineBlock() {
            if (isMining) return;
            isMining = true;
            document.getElementById('miningSpinner').classList.remove('hidden');

            try {
                const res = await fetch('/api/mine', { method: 'POST' });
                const data = await res.json();

                // Trigger block broadcast packets outward from Node A
                triggerPacketAnimation('A', 'B');
                setTimeout(() => {
                    triggerPacketAnimation('B', 'C');
                    triggerPacketAnimation('A', 'D');
                }, 200);

                fetchStatusAndChain();
            } catch (err) {
                alert('Lỗi khai thác: ' + err.message);
            } finally {
                isMining = false;
                document.getElementById('miningSpinner').classList.add('hidden');
            }
        }

        async function connectNodeD() {
            try {
                triggerPacketAnimation('D', 'A');
                const res = await fetch('/api/connect-node-d', { method: 'POST' });
                const data = await res.json();
                fetchStatusAndChain();
            } catch (err) {
                alert('Lỗi kết nối Node D: ' + err.message);
            }
        }

        async function tamperAttack() {
            try {
                const res = await fetch('/api/tamper', { method: 'POST' });
                const data = await res.json();
                if (!data.success && data.error) {
                    alert(data.error);
                }
                fetchStatusAndChain();
            } catch (err) {
                alert('Lỗi giả lập tấn công: ' + err.message);
            }
        }

        async function resetNetwork() {
            if (!confirm('Bạn có chắc muốn Reset mạng P2P về khối Genesis ban đầu?')) return;
            try {
                await fetch('/api/reset', { method: 'POST' });
                fetchStatusAndChain();
            } catch (err) {
                alert('Lỗi reset: ' + err.message);
            }
        }

        function escapeHtml(str) {
            return str.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;");
        }

        // ==================== Canvas P2P Topology Animation ====================

        const nodePositions = {
            'A': { x: 350, y: 70,  label: 'Node A (Miner)',  port: '8001', color: '#f59e0b' },
            'B': { x: 180, y: 220, label: 'Node B (Relay)',  port: '8002', color: '#06b6d4' },
            'C': { x: 520, y: 220, label: 'Node C (Wallet)', port: '8003', color: '#8b5cf6' },
            'D': { x: 350, y: 270, label: 'Node D (Sync)',   port: '8004', color: '#10b981' }
        };

        function initCanvas() {
            const canvas = document.getElementById('networkCanvas');
            // Responsive width
            canvas.width = canvas.parentElement.clientWidth;
            canvas.height = 320;
            updateNodePositions(canvas.width, canvas.height);
        }

        function updateNodePositions(w, h) {
            nodePositions['A'].x = w * 0.5;
            nodePositions['A'].y = h * 0.22;

            nodePositions['B'].x = w * 0.22;
            nodePositions['B'].y = h * 0.65;

            nodePositions['C'].x = w * 0.78;
            nodePositions['C'].y = h * 0.65;

            nodePositions['D'].x = w * 0.5;
            nodePositions['D'].y = h * 0.82;
        }

        window.addEventListener('resize', initCanvas);

        function triggerPacketAnimation(from, to) {
            if (!nodePositions[from] || !nodePositions[to]) return;
            canvasParticles.push({
                from: from,
                to: to,
                progress: 0,
                speed: 0.035,
                color: nodePositions[from].color
            });
        }

        function animateCanvas() {
            const canvas = document.getElementById('networkCanvas');
            if (!canvas) return;
            const ctx = canvas.getContext('2d');
            const w = canvas.width;
            const h = canvas.height;

            ctx.clearRect(0, 0, w, h);

            // Draw TCP links
            const links = [
                ['B', 'A'], // TCP Socket B <-> A
                ['C', 'B'], // TCP Socket C <-> B
                ['D', 'A']  // TCP Socket D <-> A
            ];

            links.forEach(([from, to]) => {
                const p1 = nodePositions[from];
                const p2 = nodePositions[to];

                ctx.beginPath();
                ctx.moveTo(p1.x, p1.y);
                ctx.lineTo(p2.x, p2.y);
                ctx.strokeStyle = '#1e293b';
                ctx.lineWidth = 2;
                ctx.setLineDash([4, 4]);
                ctx.stroke();
                ctx.setLineDash([]);
            });

            // Update & draw flying TCP packets
            for (let i = canvasParticles.length - 1; i >= 0; i--) {
                const p = canvasParticles[i];
                p.progress += p.speed;
                if (p.progress >= 1) {
                    canvasParticles.splice(i, 1);
                    continue;
                }

                const p1 = nodePositions[p.from];
                const p2 = nodePositions[p.to];
                const curX = p1.x + (p2.x - p1.x) * p.progress;
                const curY = p1.y + (p2.y - p1.y) * p.progress;

                ctx.beginPath();
                ctx.arc(curX, curY, 6, 0, Math.PI * 2);
                ctx.fillStyle = p.color;
                ctx.shadowColor = p.color;
                ctx.shadowBlur = 10;
                ctx.fill();
                ctx.shadowBlur = 0;
            }

            // Draw Nodes
            Object.keys(nodePositions).forEach(key => {
                const node = nodePositions[key];
                
                // Outer glow circle
                ctx.beginPath();
                ctx.arc(node.x, node.y, 22, 0, Math.PI * 2);
                ctx.fillStyle = '#0f172a';
                ctx.strokeStyle = node.color;
                ctx.lineWidth = 2.5;
                ctx.shadowColor = node.color;
                ctx.shadowBlur = 12;
                ctx.fill();
                ctx.stroke();
                ctx.shadowBlur = 0;

                // Center dot
                ctx.beginPath();
                ctx.arc(node.x, node.y, 7, 0, Math.PI * 2);
                ctx.fillStyle = node.color;
                ctx.fill();

                // Text labels
                ctx.font = 'bold 11px Inter, sans-serif';
                ctx.fillStyle = '#f8fafc';
                ctx.textAlign = 'center';
                ctx.fillText(node.label, node.x, node.y - 28);

                ctx.font = '9px "JetBrains Mono", monospace';
                ctx.fillStyle = '#94a3b8';
                ctx.fillText('TCP :' + node.port, node.x, node.y + 35);
            });

            requestAnimationFrame(animateCanvas);
        }
    </script>
</body>
</html>
""";
    }
}
