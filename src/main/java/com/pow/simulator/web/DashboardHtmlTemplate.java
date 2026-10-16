package com.pow.simulator.web;

/**
 * Modern Cyber Visual Dashboard with Ultra Slow-Motion Step-by-Step Simulation.
 * Provides deep-dive pedagogical visualization of TCP sockets, gossip relay, Merkle trees, and PoW nonce search.
 * Split into modular sections to stay well within the JVM 65,535-byte constant pool string limit.
 */
public final class DashboardHtmlTemplate {

    private DashboardHtmlTemplate() {
    }

    public static String getHtml() {
        StringBuilder sb = new StringBuilder(50000);
        sb.append(getHeadHtml());
        sb.append(getHeaderAndActionsHtml());
        sb.append(getSlowMoSectionHtml());
        sb.append(getTopologyAndCardsHtml());
        sb.append(getLedgerAndTerminalHtml());
        sb.append(getScriptsHtml());
        return sb.toString();
    }

    private static String getHeadHtml() {
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
    <link rel="preconnect" href="https://fonts.gstatic.com">
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
        @keyframes stepGlow {
            0%, 100% { border-color: rgba(56, 189, 248, 0.4); box-shadow: 0 0 10px rgba(56, 189, 248, 0.2); }
            50% { border-color: rgba(56, 189, 248, 1); box-shadow: 0 0 20px rgba(56, 189, 248, 0.4); }
        }
        .active-step-glow {
            animation: stepGlow 1.5s infinite ease-in-out;
        }
    </style>
</head>
<body class="min-h-screen flex flex-col">
""";
    }

    private static String getHeaderAndActionsHtml() {
        return """
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
                        Đồ Án Môn Học: <span class="text-slate-300 font-medium">Lập Trình Mạng</span> | HUTECH
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
                    <span id="backendPortLabel">Backend Port: Online</span>
                </div>
            </div>
        </div>
    </header>

    <!-- Main Content Container -->
    <main class="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 py-6 space-y-6">
""";
    }

    private static String getSlowMoSectionHtml() {
        return """
        <!-- ==================== SECTION: ULTRA SLOW-MOTION STEP-BY-STEP SIMULATION ==================== -->
        <section class="bg-gradient-to-br from-slate-900 via-slate-950 to-slate-900 border-2 border-cyan-500/60 rounded-2xl p-6 shadow-2xl relative overflow-hidden">
            <!-- Header bar with Mode badge & Controls -->
            <div class="flex flex-col md:flex-row md:items-center justify-between gap-4 pb-4 border-b border-slate-800">
                <div class="flex items-center gap-3">
                    <div class="w-12 h-12 rounded-xl bg-cyan-950 border border-cyan-700 flex items-center justify-center text-2xl shadow-lg shadow-cyan-950">
                        🐢
                    </div>
                    <div>
                        <div class="flex items-center gap-2">
                            <h2 class="text-base sm:text-lg font-bold text-white tracking-wide">
                                Chế Độ Mô Phỏng Siêu Chậm (Ultra Slow-Mo Stepper)
                            </h2>
                            <span class="px-2 py-0.5 text-[10px] font-bold uppercase rounded bg-cyan-950 text-cyan-300 border border-cyan-700 animate-pulse">
                                Quan Sát Từng Tích Tắc
                            </span>
                        </div>
                        <p class="text-xs text-slate-400">
                            Lần theo hành trình của 1 giao dịch: Từ sinh mã băm SHA-256, đóng khung TCP 4-byte, chuyển tiếp qua Socket, dựng Cây Merkle, đến vòng lặp thử Nonce PoW!
                        </p>
                    </div>
                </div>

                <!-- Slow-Mo Controls -->
                <div class="flex flex-wrap items-center gap-2">
                    <button id="btnSlowMoPlay" onclick="toggleSlowMoPlay()" class="px-3.5 py-1.5 text-xs font-bold bg-cyan-500 hover:bg-cyan-400 text-slate-950 rounded-lg shadow-lg shadow-cyan-500/25 transition flex items-center gap-1.5">
                        <span id="slowMoPlayIcon">▶️</span> <span id="slowMoPlayText">Bắt Đầu Chạy Chậm</span>
                    </button>
                    <button onclick="prevSlowMoStep()" class="px-3 py-1.5 text-xs font-medium bg-slate-800 hover:bg-slate-700 text-slate-200 rounded-lg border border-slate-700 transition">
                        ⏮️ Bước Trước
                    </button>
                    <button onclick="nextSlowMoStep()" class="px-3.5 py-1.5 text-xs font-semibold bg-indigo-600 hover:bg-indigo-500 text-white rounded-lg shadow transition flex items-center gap-1">
                        Bước Kế Tiếp ⏭️
                    </button>
                    <button onclick="resetSlowMo()" class="px-2.5 py-1.5 text-xs font-medium bg-slate-800 hover:bg-slate-700 text-slate-400 rounded-lg border border-slate-700 transition" title="Làm lại từ đầu">
                        🔄
                    </button>

                    <div class="flex items-center gap-1.5 bg-slate-950 border border-slate-800 px-2.5 py-1 rounded-lg text-xs">
                        <span class="text-slate-400 text-[11px]">Tốc độ:</span>
                        <select id="slowMoSpeedSelect" onchange="changeSlowMoSpeed(this.value)" class="bg-transparent text-cyan-300 font-semibold text-xs focus:outline-none cursor-pointer">
                            <option value="6000" class="bg-slate-900 text-slate-200">Siêu Chậm (6s / bước)</option>
                            <option value="3500" selected class="bg-slate-900 text-slate-200">Rất Chậm (3.5s / bước)</option>
                            <option value="1800" class="bg-slate-900 text-slate-200">Chậm Vừa (1.8s / bước)</option>
                            <option value="manual" class="bg-slate-900 text-slate-200">Thủ Công (Bấm Next)</option>
                        </select>
                    </div>
                </div>
            </div>

            <!-- 9-Step Progress Stepper Navigation Pills -->
            <div class="grid grid-cols-3 sm:grid-cols-5 lg:grid-cols-9 gap-1.5 my-4" id="slowMoStepperContainer">
                <!-- Dynamically rendered 9 pills -->
            </div>

            <!-- Spotlight Visual Stage Box (Màn hình diễn họa chi tiết) -->
            <div id="slowMoStageBox" class="bg-slate-950 border border-cyan-900/50 rounded-xl p-5 shadow-inner">
                <!-- Content injected by renderSlowMoStep() -->
            </div>
        </section>

        <!-- Control Center (Quick Action Buttons) -->
        <section class="bg-slate-900/90 border border-slate-800 rounded-2xl p-5 shadow-xl backdrop-blur">
            <div class="flex flex-col md:flex-row items-center justify-between gap-4 mb-4">
                <div>
                    <h2 class="text-base font-bold text-white flex items-center gap-2">
                        <span>⚡ Trung Tâm Điều Khiển Nhanh</span>
                        <span class="text-xs font-normal text-slate-400">(Tác vụ phát tán TCP Gossip & Khai thác trực tiếp)</span>
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
                <button onclick="sendTransaction()" class="group relative overflow-hidden bg-gradient-to-br from-purple-950/60 to-slate-900 border border-purple-800/50 hover:border-purple-500/80 rounded-xl p-4 text-left transition-all duration-200 hover:shadow-lg hover:shadow-purple-950/50">
                    <div class="flex items-center justify-between mb-2">
                        <span class="text-2xl">💸</span>
                        <span class="text-[10px] font-semibold uppercase px-2 py-0.5 rounded bg-purple-900/60 text-purple-300 border border-purple-700/50">Gossip TCP</span>
                    </div>
                    <div class="text-sm font-bold text-white group-hover:text-purple-300 transition">1. Gửi Giao Dịch</div>
                    <p class="text-xs text-slate-400 mt-1">Node C (Ví) phát tán Tx qua Node B rồi tới Node A vào Mempool.</p>
                </button>

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

                <button onclick="connectNodeD()" class="group relative overflow-hidden bg-gradient-to-br from-emerald-950/60 to-slate-900 border border-emerald-800/50 hover:border-emerald-500/80 rounded-xl p-4 text-left transition-all duration-200 hover:shadow-lg hover:shadow-emerald-950/50">
                    <div class="flex items-center justify-between mb-2">
                        <span class="text-2xl">🌐</span>
                        <span class="text-[10px] font-semibold uppercase px-2 py-0.5 rounded bg-emerald-950/60 text-emerald-300 border border-emerald-700/50">Sync Nakamoto</span>
                    </div>
                    <div class="text-sm font-bold text-white group-hover:text-emerald-300 transition">3. Kết Nối Node D</div>
                    <p class="text-xs text-slate-400 mt-1">Bật Node D (Port 8004), gửi TCP Handshake & Sync toàn bộ Ledger.</p>
                </button>

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
""";
    }

    private static String getTopologyAndCardsHtml() {
        return """
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
""";
    }

    private static String getLedgerAndTerminalHtml() {
        return """
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
""";
    }

    private static String getScriptsHtml() {
        return """
    <!-- JavaScript Logic & Network Simulation Scripts -->
    <script>
        // State variables
        let lastLogCount = 0;
        let isMining = false;
        let canvasParticles = [];

        // Fetch status and chain on load and every 1.2s
        window.addEventListener('DOMContentLoaded', () => {
            initCanvas();
            initSlowMoStepper();
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
                    '<span class="w-2 h-2 rounded-full bg-emerald-400 animate-ping"></span><span>Backend Port ' + (window.location.port || '8000') + ': Online</span>';

            } catch (err) {
                console.warn('Backend fetch error:', err);
                document.getElementById('connectionStatus').className = 
                    "flex items-center gap-2 px-3 py-1.5 rounded-lg bg-rose-950/80 border border-rose-700/50 text-rose-400 font-medium";
                document.getElementById('connectionStatus').innerHTML = 
                    '<span class="w-2 h-2 rounded-full bg-rose-400"></span><span>Backend Disconnected</span>';
            }
        }

        function updateStatusUI(status) {
            const alertBanner = document.getElementById('tamperAlertBanner');
            const alertText = document.getElementById('tamperAlertText');
            if (status.isTampered) {
                alertBanner.classList.remove('hidden');
                alertText.textContent = 'Khối #' + status.tamperedBlockIndex + ' bị vi phạm: ' + status.tamperReason;
            } else {
                alertBanner.classList.add('hidden');
            }

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
                ['B', 'A'],
                ['C', 'B'],
                ['D', 'A']
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

                ctx.beginPath();
                ctx.arc(node.x, node.y, 7, 0, Math.PI * 2);
                ctx.fillStyle = node.color;
                ctx.fill();

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

        // =========================================================================
        // ==================== ULTRA SLOW-MOTION SIMULATION LOGIC =================
        // =========================================================================

        let slowMoState = {
            currentStep: 1,
            isPlaying: false,
            speedMs: 3500,
            timer: null
        };

        const slowMoDefinitions = [
            {
                step: 1,
                title: "1. Khởi Tạo Giao Dịch & Băm SHA-256",
                short: "1. Tạo Tx",
                icon: "📝",
                badge: "CLIENT / WALLET",
                desc: "Alice tạo giao dịch chuyển 15.5 COIN cho Bob. Hàm băm SHA-256 tính toán mã vân tay 32-byte duy nhất (TxID).",
                render: () => `
                    <div class="space-y-4">
                        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div class="bg-slate-900 border border-purple-800/60 rounded-xl p-4">
                                <div class="text-xs text-purple-400 font-bold uppercase mb-2 flex items-center justify-between">
                                    <span>Thông tin giao dịch gốc (Payload):</span>
                                    <span class="text-[10px] bg-purple-950 px-2 py-0.5 rounded text-purple-300 border border-purple-800">Node C (Port 8003)</span>
                                </div>
                                <div class="space-y-2 text-xs code-font text-slate-300">
                                    <div><span class="text-slate-500">Người gửi (Sender):</span> <span class="text-white font-bold">Alice_Ví_101</span></div>
                                    <div><span class="text-slate-500">Người nhận (Recipient):</span> <span class="text-white font-bold">Bob_Coffee_HUTECH</span></div>
                                    <div><span class="text-slate-500">Số lượng (Amount):</span> <span class="text-amber-400 font-bold">15.50 COIN</span></div>
                                    <div><span class="text-slate-500">Thời gian (Timestamp):</span> <span class="text-cyan-400">${Date.now()}</span></div>
                                </div>
                            </div>

                            <div class="bg-slate-900 border border-slate-800 rounded-xl p-4 flex flex-col justify-center">
                                <div class="text-xs text-cyan-400 font-bold uppercase mb-2">Phép băm toán học SHA-256:</div>
                                <div class="bg-slate-950 p-2.5 rounded border border-slate-800 text-[11px] code-font text-cyan-300 break-all">
                                    SHA256(Alice + Bob + 15.5 + Timestamp)
                                </div>
                                <div class="text-center my-1.5 text-cyan-400 text-sm">⬇️ Kết Quả Sinh Ra Mã Định Danh TxID</div>
                                <div class="bg-purple-950/70 p-2.5 rounded border border-purple-700/60 text-[11px] code-font text-purple-200 font-bold break-all">
                                    TxID: 077fe90ff7ec4eb12bf70380be405b66558f37344f982a5c73e248a057c93e45
                                </div>
                            </div>
                        </div>
                        <div class="bg-purple-950/30 border border-purple-800/40 rounded-lg p-3 text-xs text-slate-300">
                            💡 <strong class="text-purple-300">Bản chất:</strong> TxID là dấu vân tay điện tử cố định 256 bits (32 bytes). Nếu kẻ gian sửa đổi số tiền dù chỉ 0.0001 xu, mã TxID này sẽ biến đổi hoàn toàn và bị phát hiện ngay lập tức.
                        </div>
                    </div>
                `
            },
            {
                step: 2,
                title: "2. Đóng Khung Tin TCP (4-Byte Length-Prefix Framing)",
                short: "2. Khung TCP",
                icon: "📦",
                badge: "GIAO THỨC MẠNG",
                desc: "Đóng gói thông điệp thành khung tin: Thêm 4 byte Big-Endian ở đầu để Socket bên nhận biết chính xác độ dài dữ liệu.",
                render: () => `
                    <div class="space-y-4">
                        <div class="bg-slate-900 border border-cyan-800/60 rounded-xl p-4">
                            <div class="text-xs text-cyan-400 font-bold uppercase mb-2">Cấu trúc khung tin TCP Stream (Frame Anatomy):</div>
                            <div class="grid grid-cols-1 md:grid-cols-12 gap-3 items-stretch">
                                <div class="md:col-span-4 bg-cyan-950/70 border-2 border-cyan-500 rounded-lg p-3 flex flex-col justify-between">
                                    <div>
                                        <span class="text-[10px] uppercase font-bold text-cyan-300">Length Prefix (4 Bytes Big-Endian):</span>
                                        <div class="text-base font-bold text-white code-font mt-1">[ 00 00 01 2A ]</div>
                                    </div>
                                    <div class="text-[10px] text-cyan-400 mt-2">
                                        = 298 Bytes (Kích thước gói tin)
                                    </div>
                                </div>
                                <div class="md:col-span-8 bg-slate-950 border border-slate-800 rounded-lg p-3 text-xs code-font text-slate-300 overflow-x-auto">
                                    <span class="text-[10px] uppercase font-bold text-slate-500 block mb-1">Payload JSON Stream (298 Bytes):</span>
                                    <span class="text-amber-300">{"type":</span><span class="text-emerald-300">"TX_BROADCAST"</span>,
                                    <span class="text-amber-300">"messageId":</span><span class="text-purple-300">"e4f9b1-..."</span>,
                                    <span class="text-amber-300">"tx":</span><span class="text-slate-300">{"txId":"077fe90...", "amount":15.5}}</span>
                                </div>
                            </div>
                        </div>
                        <div class="bg-cyan-950/30 border border-cyan-800/40 rounded-lg p-3 text-xs text-slate-300">
                            📡 <strong class="text-cyan-300">Kiến thức Lập Trình Mạng quan trọng:</strong> Giao thức TCP là luồng byte liên tục (Stream-oriented), không có ranh giới gói tin. Nếu không có 4 byte độ dài này, hai gói tin gửi sát nhau sẽ bị dính chùm vào nhau (Sticky Packets). Nhờ kỹ thuật này, hàm <code class="text-cyan-200">readNBytes(length)</code> luôn đọc trọn vẹn từng thông điệp!
                        </div>
                    </div>
                `
            },
            {
                step: 3,
                title: "3. Truyền Socket TCP: Node C ➔ Node B",
                short: "3. Socket C➔B",
                icon: "🚀",
                badge: "TCP SOCKET STREAM",
                desc: "Gói tin được đẩy vào OutputStream của Socket tại cổng 8003 và bay chậm rãi qua dây mạng tới cổng 8002.",
                render: () => `
                    <div class="space-y-4">
                        <div class="bg-slate-900 border border-purple-800/60 rounded-xl p-4 flex flex-col items-center justify-center">
                            <div class="flex items-center justify-between w-full max-w-lg mb-3">
                                <div class="text-center">
                                    <div class="w-12 h-12 rounded-xl bg-purple-950 border-2 border-purple-500 flex items-center justify-center text-lg mx-auto shadow-lg shadow-purple-950">📱</div>
                                    <div class="text-xs font-bold text-white mt-1">Node C (Wallet)</div>
                                    <div class="text-[10px] text-purple-400 code-font">Port :8003</div>
                                </div>
                                <div class="flex-1 mx-4 text-center">
                                    <div class="text-[11px] text-purple-300 font-bold mb-1">Luồng Gói Tin TCP Di Chuyển...</div>
                                    <div class="relative w-full h-3 bg-slate-950 rounded-full border border-purple-800/80 overflow-hidden">
                                        <div class="absolute top-0 bottom-0 left-0 bg-gradient-to-r from-purple-500 to-cyan-400 rounded-full animate-pulse" style="width: 85%;"></div>
                                    </div>
                                    <div class="text-[10px] text-slate-500 code-font mt-1">Duplex TCP Connection Established</div>
                                </div>
                                <div class="text-center">
                                    <div class="w-12 h-12 rounded-xl bg-cyan-950 border-2 border-cyan-500 flex items-center justify-center text-lg mx-auto shadow-lg shadow-cyan-950">📡</div>
                                    <div class="text-xs font-bold text-white mt-1">Node B (Relay)</div>
                                    <div class="text-[10px] text-cyan-400 code-font">Port :8002</div>
                                </div>
                            </div>
                            <div class="text-xs code-font bg-slate-950 p-2.5 rounded-lg border border-slate-800 text-slate-300 text-center w-full max-w-lg">
                                Java Code: <span class="text-purple-300">socket.getOutputStream().write(frameBytes);</span>
                            </div>
                        </div>
                        <div class="bg-slate-900/60 border border-slate-800 rounded-lg p-3 text-xs text-slate-300">
                            ⚙️ <strong class="text-slate-200">Cơ chế hoạt động:</strong> Node C mở kết nối TCP duplex tới Node B. Virtual Thread tại Node B liên tục đọc InputStream để tiếp nhận khung tin ngay khi các byte đầu tiên cập bến.
                        </div>
                    </div>
                `
            },
            {
                step: 4,
                title: "4. Relay Gossip Protocol & Lưu Vào Mempool",
                short: "4. Relay ➔ Node A",
                icon: "🔄",
                badge: "GOSSIP RELAY & MEMPOOL",
                desc: "Node B thẩm định UUID để chống bão broadcast, lưu giao dịch vào Mempool, rồi chuyển tiếp tiếp tới Node A.",
                render: () => `
                    <div class="space-y-4">
                        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <div class="bg-slate-900 border border-cyan-800/60 rounded-xl p-4">
                                <div class="text-xs text-cyan-400 font-bold uppercase mb-2">Thao Tác Tại Node B (Relay):</div>
                                <ul class="space-y-2 text-xs text-slate-300">
                                    <li class="flex items-center gap-2">
                                        <span class="text-emerald-400">✔️</span>
                                        <span>Kiểm tra <code class="text-cyan-300">seenMessageIds</code>: Gói tin mới, chưa thấy bao giờ.</span>
                                    </li>
                                    <li class="flex items-center gap-2">
                                        <span class="text-emerald-400">✔️</span>
                                        <span>Thêm vào hàng đợi chờ: <code class="text-amber-300">Mempool (+1 Tx)</code>.</span>
                                    </li>
                                    <li class="flex items-center gap-2">
                                        <span class="text-cyan-400">➡️</span>
                                        <span>Chuyển tiếp (Forward) qua TCP Socket tới <strong class="text-amber-300">Node A (Port 8001)</strong>.</span>
                                    </li>
                                </ul>
                            </div>

                            <div class="bg-slate-900 border border-amber-800/60 rounded-xl p-4 flex flex-col justify-center">
                                <div class="text-xs text-amber-400 font-bold uppercase mb-2">Mempool Của Thợ Đào (Node A):</div>
                                <div class="bg-slate-950 p-3 rounded-lg border border-slate-800 text-xs code-font">
                                    <div class="text-slate-500 mb-1">// Phòng chờ giao dịch chưa xác nhận (Unconfirmed Txs)</div>
                                    <div class="text-amber-300 font-bold">[TxID: 077fe90f] Alice ➔ Bob : 15.50 COIN</div>
                                </div>
                            </div>
                        </div>
                        <div class="bg-cyan-950/30 border border-cyan-800/40 rounded-lg p-3 text-xs text-slate-300">
                            🛡️ <strong class="text-cyan-300">Chống bão Broadcast Storm:</strong> Trong mạng ngang hàng (P2P), nếu không có cơ chế khử lặp (Deduplication) bằng UUID, các nút sẽ gửi qua gửi lại thông điệp vô tận làm sập đường truyền mạng.
                        </div>
                    </div>
                `
            },
            {
                step: 5,
                title: "5. Gom Giao Dịch & Xây Dựng Cây Nhị Phân Merkle Tree",
                short: "5. Cây Merkle",
                icon: "🌳",
                badge: "MẬT MÃ MERKLE TREE",
                desc: "Thợ đào Node A lấy các giao dịch trong Mempool băm theo cặp đôi để sinh ra mã Merkle Root duy nhất.",
                render: () => `
                    <div class="space-y-4">
                        <div class="bg-slate-900 border border-indigo-800/60 rounded-xl p-4 flex flex-col items-center">
                            <div class="text-xs text-indigo-400 font-bold uppercase mb-3">Sơ Đồ Cây Merkle Tree Nhị Phân (Binary Tree):</div>
                            
                            <div class="bg-gradient-to-r from-indigo-900 to-purple-900 border-2 border-indigo-400 px-4 py-2 rounded-xl text-center shadow-lg shadow-indigo-950 mb-2">
                                <div class="text-[10px] uppercase font-bold text-indigo-300">Đỉnh Cây: Merkle Root (32 Bytes)</div>
                                <div class="text-xs font-bold text-white code-font">077fe90ff7ec4eb12bf70380...</div>
                            </div>
                            <div class="text-indigo-400 text-sm my-1">▲ Băm Gộp: SHA256(Hash_A + Hash_B)</div>

                            <div class="flex items-center gap-8 my-1">
                                <div class="bg-slate-950 border border-indigo-700/60 px-3 py-1.5 rounded-lg text-center">
                                    <div class="text-[9px] text-slate-500 uppercase">Nhánh Con Hash_A</div>
                                    <div class="text-[11px] code-font text-cyan-300">9c4e1a0b...</div>
                                </div>
                                <div class="bg-slate-950 border border-indigo-700/60 px-3 py-1.5 rounded-lg text-center">
                                    <div class="text-[9px] text-slate-500 uppercase">Nhánh Con Hash_B</div>
                                    <div class="text-[11px] code-font text-cyan-300">3f88d22e...</div>
                                </div>
                            </div>
                            <div class="text-slate-600 text-xs my-1">▲ Các Lá Giao Dịch</div>

                            <div class="grid grid-cols-2 gap-4 text-center w-full max-w-md">
                                <div class="bg-slate-950 border border-slate-800 p-2 rounded text-[11px] code-font text-slate-300">
                                    Tx1: Alice ➔ Bob (15.5)
                                </div>
                                <div class="bg-slate-950 border border-slate-800 p-2 rounded text-[11px] code-font text-slate-300">
                                    Tx2: Coinbase (Reward 50)
                                </div>
                            </div>
                        </div>
                        <div class="bg-indigo-950/30 border border-indigo-800/40 rounded-lg p-3 text-xs text-slate-300">
                            🌲 <strong class="text-indigo-300">Sức mạnh của Cây Merkle:</strong> Dù khối có 10.000 giao dịch, đỉnh Merkle Root vẫn chỉ dài đúng 32 bytes. Nếu bất kỳ ai sửa 1 giao dịch ở tầng lá, gốc cây Merkle Root sẽ lập tức sai lệch!
                        </div>
                    </div>
                `
            },
            {
                step: 6,
                title: "6. Vòng Lặp Đào Proof-of-Work: Thử Nghiệm Nonce",
                short: "6. Đào PoW",
                icon: "⛏️",
                badge: "GIẢI ĐỐ THUẬT TOÁN POW",
                desc: "Thợ đào Node A liên tục tăng biến Nonce (0, 1, 2, ...) tìm kiếm mã băm SHA-256 thỏa mãn điều kiện độ khó.",
                render: () => `
                    <div class="space-y-4">
                        <div class="bg-slate-900 border border-amber-800/60 rounded-xl p-4">
                            <div class="flex items-center justify-between mb-3">
                                <div>
                                    <span class="text-xs text-amber-400 font-bold uppercase">Mục Tiêu Độ Khó (Target Difficulty):</span>
                                    <div class="text-xs text-slate-300 mt-0.5">Yêu cầu mã băm phải bắt đầu bằng <strong class="text-amber-400 code-font">10 bit 0</strong> (khoảng 3 chữ số '0' ở đầu)</div>
                                </div>
                                <span class="text-xs bg-amber-950 text-amber-300 px-2.5 py-1 rounded border border-amber-800 font-bold code-font">Target: 000...</span>
                            </div>

                            <div class="bg-slate-950 rounded-xl p-3 border border-slate-800 space-y-2 font-mono text-xs">
                                <div class="text-slate-500">// Quá trình máy tính thử nghiệm giải đố từng con số Nonce:</div>
                                <div class="text-rose-400 flex items-center justify-between">
                                    <span>Thử Nonce = 0 &nbsp;➔ SHA256(BlockHeader + 0) = d83a21b4...</span>
                                    <span class="text-[10px] bg-rose-950 px-1.5 py-0.5 rounded text-rose-300 border border-rose-800">❌ SAI</span>
                                </div>
                                <div class="text-rose-400 flex items-center justify-between">
                                    <span>Thử Nonce = 1 &nbsp;➔ SHA256(BlockHeader + 1) = 4f9812ee...</span>
                                    <span class="text-[10px] bg-rose-950 px-1.5 py-0.5 rounded text-rose-300 border border-rose-800">❌ SAI</span>
                                </div>
                                <div class="text-rose-400 flex items-center justify-between">
                                    <span>Thử Nonce = 2 &nbsp;➔ SHA256(BlockHeader + 2) = 7c1a90d3...</span>
                                    <span class="text-[10px] bg-rose-950 px-1.5 py-0.5 rounded text-rose-300 border border-rose-800">❌ SAI</span>
                                </div>
                                <div class="text-slate-600 text-center py-1">... [Đã thử hàng ngàn số Nonce bằng Java 21 Virtual Threads] ...</div>
                                <div class="text-emerald-400 font-bold bg-emerald-950/60 p-2 rounded border border-emerald-700 flex items-center justify-between">
                                    <span>🎉 Thử Nonce = 4404 ➔ SHA256 = 002af149...8587f2</span>
                                    <span class="text-[10px] bg-emerald-900 px-2 py-0.5 rounded text-emerald-200 border border-emerald-600 font-bold">TRÚNG ĐÍCH!</span>
                                </div>
                            </div>
                        </div>
                        <div class="bg-amber-950/30 border border-amber-800/40 rounded-lg p-3 text-xs text-slate-300">
                            ⛏️ <strong class="text-amber-300">Bản chất Proof-of-Work:</strong> Hàm băm SHA-256 hoàn toàn ngẫu nhiên và không thể tính ngược. Cách duy nhất là thử vận may từng con số Nonce. Con số 4404 tìm thấy chính là "Bằng chứng" chứng minh thợ đào đã bỏ công sức tính toán nghiêm túc!
                        </div>
                    </div>
                `
            },
            {
                step: 7,
                title: "7. Niêm Phong Khối & Móc Xích (Previous Hash)",
                short: "7. Đóng Khối",
                icon: "⛓️",
                badge: "LIÊN KẾT CHUỖI KHỐI",
                desc: "Khối mới được đóng gói và lưu giữ con trỏ mã băm Previous Hash trỏ về Khối trước đó.",
                render: () => `
                    <div class="space-y-4">
                        <div class="flex flex-col md:flex-row items-center justify-center gap-4 bg-slate-900 border border-cyan-800/60 rounded-xl p-4">
                            <div class="w-full md:w-56 bg-slate-950 border border-slate-700 rounded-xl p-3 text-xs code-font opacity-80">
                                <div class="font-bold text-cyan-400 border-b border-slate-800 pb-1 mb-2">Block #0 (Genesis)</div>
                                <div class="text-[10px] text-slate-500">Hash Khối Cũ:</div>
                                <div class="text-cyan-300 font-bold truncate">d997a9fa...449ed9</div>
                            </div>

                            <div class="text-cyan-400 font-bold text-center">
                                <div class="text-lg">➔</div>
                                <div class="text-[9px] text-cyan-300 uppercase code-font">PreviousHash</div>
                            </div>

                            <div class="w-full md:w-72 bg-gradient-to-br from-emerald-950/60 to-slate-950 border-2 border-emerald-500 rounded-xl p-3.5 text-xs code-font shadow-lg shadow-emerald-950/50">
                                <div class="flex items-center justify-between border-b border-emerald-800 pb-1 mb-2">
                                    <span class="font-bold text-emerald-300">Block #1 (Mới Đào Xong)</span>
                                    <span class="text-[9px] bg-emerald-900 text-emerald-200 px-1.5 py-0.2 rounded font-bold">SEALED</span>
                                </div>
                                <div class="space-y-1 text-[11px]">
                                    <div><span class="text-slate-500 text-[10px]">PrevHash:</span> <span class="text-cyan-400 font-bold">d997a9fa...449ed9</span></div>
                                    <div><span class="text-slate-500 text-[10px]">Winning Nonce:</span> <span class="text-amber-400 font-bold">4404</span></div>
                                    <div><span class="text-slate-500 text-[10px]">Merkle Root:</span> <span class="text-purple-300">077fe90f...</span></div>
                                    <div><span class="text-slate-500 text-[10px]">Block Hash:</span> <span class="text-emerald-400 font-bold">002af149...8587f2</span></div>
                                </div>
                            </div>
                        </div>
                        <div class="bg-cyan-950/30 border border-cyan-800/40 rounded-lg p-3 text-xs text-slate-300">
                            🔗 <strong class="text-cyan-300">Tại sao gọi là Blockchain?</strong> Các khối móc xích vào nhau bằng <code class="text-cyan-200">previousHash</code>. Nếu kẻ gian sửa đổi Khối #0, mã băm của nó sẽ đổi, làm cho mối nối của Khối #1 lập tức bị đứt gãy và vô hiệu hóa toàn bộ chuỗi phía sau.
                        </div>
                    </div>
                `
            },
            {
                step: 8,
                title: "8. Phát Tán Khối Mới Qua Socket TCP (Block Broadcast)",
                short: "8. Broadcast",
                icon: "📡",
                badge: "PHÁT TÁN MẠNG TCP",
                desc: "Node A phát tín hiệu BLOCK_BROADCAST qua Socket tới toàn bộ Node B, C, D để yêu cầu đồng thuận.",
                render: () => `
                    <div class="space-y-4">
                        <div class="bg-slate-900 border border-amber-800/60 rounded-xl p-4 flex flex-col items-center">
                            <div class="text-xs text-amber-400 font-bold uppercase mb-2">Tín Hiệu Phát Tán Khối Mới Toàn Mạng (TCP Broadcast):</div>
                            <div class="w-full max-w-lg bg-slate-950 border border-slate-800 rounded-xl p-3 code-font text-xs space-y-2">
                                <div class="flex items-center gap-2 text-cyan-300">
                                    <span>[Port 8001] Node-A ➔ Node-B:</span>
                                    <span class="bg-cyan-950 text-cyan-200 px-2 py-0.5 rounded text-[10px] border border-cyan-800">BLOCK_BROADCAST (Block #1)</span>
                                </div>
                                <div class="flex items-center gap-2 text-purple-300">
                                    <span>[Port 8002] Node-B ➔ Node-C:</span>
                                    <span class="bg-purple-950 text-purple-200 px-2 py-0.5 rounded text-[10px] border border-purple-800">BLOCK_BROADCAST (Block #1)</span>
                                </div>
                                <div class="text-[11px] text-slate-400 pt-1 border-t border-slate-900">
                                    Frame Size: <strong class="text-white">612 Bytes</strong> | Trạng thái: Đã chuyển tiếp thành công tới mọi Peer
                                </div>
                            </div>
                        </div>
                        <div class="bg-amber-950/30 border border-amber-800/40 rounded-lg p-3 text-xs text-slate-300">
                            ⚡ <strong class="text-amber-300">Đồng bộ tốc độ cao:</strong> Các nút nhận được khối sẽ tạm dừng việc đào vô ích trên khối cũ, thẩm định tính hợp lệ và sẵn sàng gom giao dịch mới để đào khối tiếp theo.
                        </div>
                    </div>
                `
            },
            {
                step: 9,
                title: "9. Toàn Mạng Thẩm Định & Cập Nhật Sổ Cái Cục Bộ",
                short: "9. Sổ Cái Cập Nhật",
                icon: "✅",
                badge: "ĐỒNG THUẬN NAKAMOTO HOÀN TẤT",
                desc: "Các Node kiểm tra Merkle Root và PoW. Khi hợp lệ 100%, độ cao chuỗi (Chain Height) tăng từ 1 lên 2!",
                render: () => `
                    <div class="space-y-4">
                        <div class="bg-slate-900 border border-emerald-800/60 rounded-xl p-4">
                            <div class="text-xs text-emerald-400 font-bold uppercase mb-3">Kết Quả Kiểm Toán Tự Động Tại Từng Nút:</div>
                            <div class="grid grid-cols-1 md:grid-cols-3 gap-3">
                                <div class="bg-slate-950 border border-slate-800 rounded-lg p-3 text-center">
                                    <div class="text-xs font-bold text-white mb-1">Node A (Miner)</div>
                                    <div class="text-[10px] text-emerald-400 font-bold">Ledger Height: 2 Blocks</div>
                                    <div class="text-[9px] text-slate-500 mt-1">Mempool: 0 txs (Đã dọn)</div>
                                </div>
                                <div class="bg-slate-950 border border-slate-800 rounded-lg p-3 text-center">
                                    <div class="text-xs font-bold text-white mb-1">Node B (Relay)</div>
                                    <div class="text-[10px] text-emerald-400 font-bold">Ledger Height: 2 Blocks</div>
                                    <div class="text-[9px] text-slate-500 mt-1">Mempool: 0 txs (Đã dọn)</div>
                                </div>
                                <div class="bg-slate-950 border border-slate-800 rounded-lg p-3 text-center">
                                    <div class="text-xs font-bold text-white mb-1">Node C (Wallet)</div>
                                    <div class="text-[10px] text-emerald-400 font-bold">Ledger Height: 2 Blocks</div>
                                    <div class="text-[9px] text-slate-500 mt-1">Trạng thái Tx: CONFIRMED ✅</div>
                                </div>
                            </div>
                        </div>
                        <div class="bg-emerald-950/40 border border-emerald-700/60 rounded-lg p-3 text-xs text-slate-200">
                            🎉 <strong class="text-emerald-300">Hoàn Tất Toàn Vẹn:</strong> Giao dịch của Alice chuyển 15.5 COIN cho Bob đã chính thức được khắc ghi vĩnh viễn vào cuốn sổ cái phân tán của toàn mạng. Không ai có thể đảo ngược hay xóa bỏ!
                        </div>
                    </div>
                `
            }
        ];

        function initSlowMoStepper() {
            renderSlowMoPills();
            renderSlowMoStep(slowMoState.currentStep);
        }

        function renderSlowMoPills() {
            const container = document.getElementById('slowMoStepperContainer');
            if (!container) return;

            container.innerHTML = slowMoDefinitions.map(def => {
                const isActive = def.step === slowMoState.currentStep;
                const isPassed = def.step < slowMoState.currentStep;

                let pillStyle = "bg-slate-950 border-slate-800 text-slate-400 hover:border-slate-700";
                if (isActive) {
                    pillStyle = "bg-cyan-950 border-cyan-400 text-cyan-200 font-bold active-step-glow";
                } else if (isPassed) {
                    pillStyle = "bg-slate-900/80 border-emerald-700 text-emerald-300";
                }

                return `
                    <button onclick="jumpToSlowMoStep(${def.step})" class="border rounded-lg p-2 text-center transition flex flex-col items-center justify-between text-xs cursor-pointer ${pillStyle}">
                        <div class="text-base mb-0.5">${def.icon}</div>
                        <div class="text-[10px] font-semibold truncate w-full">${def.short}</div>
                    </button>
                `;
            }).join('');
        }

        function renderSlowMoStep(stepNum) {
            const def = slowMoDefinitions.find(d => d.step === stepNum);
            if (!def) return;

            const box = document.getElementById('slowMoStageBox');
            if (!box) return;

            box.innerHTML = `
                <div class="space-y-3">
                    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-2 border-b border-slate-800 pb-3">
                        <div class="flex items-center gap-2.5">
                            <span class="text-2xl">${def.icon}</span>
                            <div>
                                <div class="text-sm font-bold text-white">${def.title}</div>
                                <div class="text-xs text-slate-400">${def.desc}</div>
                            </div>
                        </div>
                        <span class="text-[10px] uppercase font-bold px-2.5 py-1 rounded bg-slate-900 border border-cyan-800 text-cyan-300 self-start sm:self-auto code-font">
                            ${def.badge}
                        </span>
                    </div>

                    <div class="pt-2">
                        ${def.render()}
                    </div>
                </div>
            `;

            renderSlowMoPills();

            if (stepNum === 3) {
                triggerPacketAnimation('C', 'B');
            } else if (stepNum === 4) {
                triggerPacketAnimation('B', 'A');
            } else if (stepNum === 8) {
                triggerPacketAnimation('A', 'B');
                setTimeout(() => triggerPacketAnimation('B', 'C'), 250);
            }
        }

        function toggleSlowMoPlay() {
            if (slowMoState.isPlaying) {
                pauseSlowMo();
            } else {
                startSlowMo();
            }
        }

        function startSlowMo() {
            slowMoState.isPlaying = true;
            document.getElementById('slowMoPlayIcon').textContent = '⏸️';
            document.getElementById('slowMoPlayText').textContent = 'Tạm Dừng';

            if (slowMoState.speedMs !== 'manual') {
                scheduleNextSlowMoTimer();
            }
        }

        function pauseSlowMo() {
            slowMoState.isPlaying = false;
            document.getElementById('slowMoPlayIcon').textContent = '▶️';
            document.getElementById('slowMoPlayText').textContent = 'Tiếp Tục';
            if (slowMoState.timer) {
                clearTimeout(slowMoState.timer);
                slowMoState.timer = null;
            }
        }

        function scheduleNextSlowMoTimer() {
            if (slowMoState.timer) clearTimeout(slowMoState.timer);
            if (!slowMoState.isPlaying || slowMoState.speedMs === 'manual') return;

            slowMoState.timer = setTimeout(() => {
                if (!slowMoState.isPlaying) return;
                if (slowMoState.currentStep < 9) {
                    slowMoState.currentStep++;
                    renderSlowMoStep(slowMoState.currentStep);
                    scheduleNextSlowMoTimer();
                } else {
                    pauseSlowMo();
                }
            }, typeof slowMoState.speedMs === 'number' ? slowMoState.speedMs : 3500);
        }

        function nextSlowMoStep() {
            if (slowMoState.currentStep < 9) {
                slowMoState.currentStep++;
                renderSlowMoStep(slowMoState.currentStep);
                if (slowMoState.isPlaying) scheduleNextSlowMoTimer();
            }
        }

        function prevSlowMoStep() {
            if (slowMoState.currentStep > 1) {
                slowMoState.currentStep--;
                renderSlowMoStep(slowMoState.currentStep);
                if (slowMoState.isPlaying) scheduleNextSlowMoTimer();
            }
        }

        function jumpToSlowMoStep(stepNum) {
            slowMoState.currentStep = stepNum;
            renderSlowMoStep(slowMoState.currentStep);
            if (slowMoState.isPlaying) scheduleNextSlowMoTimer();
        }

        function resetSlowMo() {
            pauseSlowMo();
            slowMoState.currentStep = 1;
            renderSlowMoStep(1);
        }

        function changeSlowMoSpeed(val) {
            if (val === 'manual') {
                slowMoState.speedMs = 'manual';
                pauseSlowMo();
            } else {
                slowMoState.speedMs = parseInt(val, 10);
                if (slowMoState.isPlaying) scheduleNextSlowMoTimer();
            }
        }
    </script>
</body>
</html>
""";
    }
}
