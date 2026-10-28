package com.pow.simulator.web;

/**
 * Giao diện Giám Sát Cụm 10 Máy P2P Proof-of-Work (Enterprise Decentralized Mesh).
 * Phong cách Light Theme cao cấp:
 * 1. CỘT TRÁI: Sơ đồ mạng P2P Full-Mesh 10 đỉnh (Decagon Topology) hiển thị số Nonce chạy thực tế và hoạt ảnh truyền tin.
 * 2. CỘT PHẢI: Sơ đồ trình tự giao tiếp mạng 5 bước (Interactive Protocol Stepper) ĐỒNG BỘ 100% thời gian thực.
 * 3. Chế độ "Mô Phỏng Chậm Từng Bước (Slow-Mo)" giúp quan sát cặn kẽ từng gói tin TCP và cơ chế Nakamoto Consensus.
 * 4. Bảng 10 thẻ máy trạm (2 hàng x 5 cột) và Console Terminal thời gian thực.
 */
public final class NodeClusterHtmlTemplate {

    private NodeClusterHtmlTemplate() {}

    private static final String PART_1 = """
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cụm 10 Máy P2P Proof-of-Work | Mạng Lưới Đồng Thuận Phân Tán</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&family=JetBrains+Mono:wght@400;500;600;700&display=swap');
        
        body {
            font-family: 'Plus Jakarta Sans', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
            background-color: #f8fafc;
        }
        .font-mono {
            font-family: 'JetBrains Mono', monospace;
        }
        
        ::-webkit-scrollbar { width: 6px; height: 6px; }
        ::-webkit-scrollbar-track { background: #0f172a; }
        ::-webkit-scrollbar-thumb { background: #334155; border-radius: 9999px; }
        ::-webkit-scrollbar-thumb:hover { background: #475569; }

        .node-card {
            transition: all 0.2s cubic-bezier(0.16, 1, 0.3, 1);
        }
        .node-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.06);
        }
        .node-valid-hash {
            border-color: #2563eb !important;
            background-color: #f8fafc !important;
            box-shadow: 0 0 0 2px #2563eb, 0 10px 25px -5px rgba(37, 99, 235, 0.15) !important;
        }
        .computing-pulse {
            animation: computing-active 0.8s infinite alternate;
        }
        @keyframes computing-active {
            from { opacity: 0.7; }
            to { opacity: 1; border-color: #f59e0b; }
        }

        /* Hoạt ảnh tia sáng Gossip truyền dọc 45 đường nối */
        @keyframes dash-move {
            from { stroke-dashoffset: 24; }
            to { stroke-dashoffset: 0; }
        }
        .gossip-signal-line {
            stroke-dasharray: 6, 6;
            animation: dash-move 0.5s linear infinite;
        }

        /* Vòng sóng xung kích bung tỏa khi phát hiện và nhận khối */
        @keyframes ripple-wave {
            0% { r: 16; opacity: 1; stroke-width: 3.5; }
            100% { r: 42; opacity: 0; stroke-width: 0.8; }
        }
        .node-ripple {
            animation: ripple-wave 1.4s cubic-bezier(0, 0.2, 0.8, 1) forwards;
        }

        /* Thẻ bước quy trình bên cột phải */
        .step-card {
            transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1);
        }
        .step-card.active-step {
            border-color: #2563eb !important;
            background-color: #eff6ff !important;
            box-shadow: 0 4px 14px -2px rgba(37, 99, 235, 0.15);
            transform: translateX(4px);
        }
        .step-card.active-step-amber {
            border-color: #d97706 !important;
            background-color: #fffbeb !important;
            box-shadow: 0 4px 14px -2px rgba(217, 119, 6, 0.15);
            transform: translateX(4px);
        }
        .step-card.active-step-green {
            border-color: #059669 !important;
            background-color: #ecfdf5 !important;
            box-shadow: 0 4px 14px -2px rgba(5, 150, 105, 0.15);
            transform: translateX(4px);
        }
    </style>
</head>
<body class="min-h-screen text-slate-800 antialiased flex flex-col justify-between selection:bg-slate-900 selection:text-white">

    <!-- TOP NAVIGATION BAR -->
    <header class="border-b border-slate-200 bg-white sticky top-0 z-40 shadow-xs">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 py-3 flex flex-col md:flex-row items-center justify-between gap-3">
            <div class="flex items-center gap-3">
                <div class="w-9 h-9 rounded-xl bg-slate-900 text-white flex items-center justify-center font-bold text-xs tracking-wider shadow-xs">
                    P2P
                </div>
                <div>
                    <div class="flex items-center gap-2">
                        <h1 class="text-sm font-extrabold text-slate-900 tracking-tight uppercase">
                            HỆ THỐNG GIÁM SÁT CỤM 10 NÚT MẠNG P2P PROOF-OF-WORK
                        </h1>
                        <span class="text-[10px] bg-emerald-50 text-emerald-700 border border-emerald-200 px-2 py-0.5 rounded-full font-mono font-bold">10 NODES MESH ONLINE</span>
                    </div>
                    <p class="text-xs text-slate-500 font-mono">Giao thức TCP Mesh 45 Kết Nối &bull; Đa luồng Java 21 Virtual Threads</p>
                </div>
            </div>

            <!-- ĐIỀU HƯỚNG LIÊN KẾT HỆ THỐNG -->
            <div class="flex items-center gap-2.5">
                <a href="/vote" class="px-3.5 py-1.5 bg-blue-600 hover:bg-blue-700 text-white font-semibold text-xs rounded-xl transition shadow-xs flex items-center gap-1.5 cursor-pointer">
                    Cổng Bỏ Phiếu Cử Tri
                </a>
                <a href="/" class="px-3.5 py-1.5 bg-white hover:bg-slate-50 border border-slate-300 text-slate-700 text-xs font-semibold rounded-xl transition shadow-xs flex items-center gap-1.5 cursor-pointer">
                    Sổ Cái Explorer
                </a>
            </div>
        </div>
    </header>

    <!-- KHU VỰC ĐIỀU KHIỂN & THEO DÕI CHÍNH -->
    <main class="max-w-7xl mx-auto px-4 sm:px-6 py-5 flex-1 w-full space-y-5">

        <!-- 1. BẢNG ĐIỀU KHIỂN TRUNG TÂM (MASTER CONTROLS) -->
        <div class="bg-white border border-slate-200 rounded-2xl p-4 shadow-xs">
            <div class="flex flex-col lg:flex-row items-center justify-between gap-4">
                
                <!-- CÁC NÚT ĐIỀU HÀNH CUỘC TRANH TÀI POW -->
                <div class="flex items-center gap-2.5 w-full lg:w-auto flex-wrap">
                    <!-- BẮT ĐẦU CHẠY TỰ ĐỘNG -->
                    <button id="btnContStart" onclick="startContinuousMining()" 
                            class="px-4 py-2 bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-xs tracking-wider rounded-xl transition shadow-xs flex items-center gap-2 cursor-pointer">
                        <span class="w-2 h-2 rounded-full bg-white animate-pulse"></span>
                        <span>BẮT ĐẦU CHẠY TỰ ĐỘNG</span>
                    </button>

                    <!-- DỪNG CHẠY TỰ ĐỘNG -->
                    <button id="btnContStop" onclick="stopContinuousMining()" disabled
                            class="px-3.5 py-2 bg-rose-50 text-rose-400 border border-rose-200 font-bold text-xs tracking-wider rounded-xl transition flex items-center gap-1.5 cursor-pointer opacity-50">
                        <span>DỪNG</span>
                    </button>

                    <!-- ĐÓNG GÓI 1 KHỐI (TRANH TÀI 10 MÁY) -->
                    <button id="btnRace" onclick="startPowRace()" 
                            class="px-3.5 py-2 bg-slate-900 hover:bg-slate-800 text-white font-semibold text-xs rounded-xl transition shadow-xs flex items-center gap-2 cursor-pointer">
                        <span>Đóng Gói 1 Khối</span>
                    </button>

                    <!-- MÔ PHỎNG CHẬM TỪNG BƯỚC (SLOW-MO) -->
                    <button id="btnSlowMo" onclick="runSlowMotionSequence()" 
                            class="px-3.5 py-2 bg-blue-50 hover:bg-blue-100 border border-blue-200 text-blue-700 font-bold text-xs rounded-xl transition shadow-xs flex items-center gap-1.5 cursor-pointer">
                        <span>Mô Phỏng Chậm 5 Bước (Slow-Mo)</span>
                    </button>
                </div>

                <!-- CÁC CHỈ SỐ MẠNG & THÔNG SỐ KỸ THUẬT -->
                <div class="flex items-center gap-2.5 flex-wrap justify-center lg:justify-end w-full lg:w-auto font-mono text-xs">
                    <!-- Trạng thái tự động liên tục -->
                    <div class="bg-slate-50 border border-slate-200 rounded-xl px-3 py-1.5 flex items-center gap-2">
                        <span class="w-2 h-2 rounded-full bg-slate-400" id="contDot"></span>
                        <span id="contText" class="text-slate-600 font-bold">Chế độ liên tục: ĐÃ TẮT</span>
                    </div>

                    <!-- Mempool -->
                    <div class="bg-slate-50 border border-slate-200 rounded-xl px-3 py-1.5 flex items-center gap-1.5">
                        <span class="text-slate-500 font-sans">Hòm phiếu:</span>
                        <span id="mempoolBadge" class="font-bold text-blue-700 bg-blue-50 border border-blue-200 px-1.5 py-0.5 rounded">0 phiếu</span>
                    </div>
                </div>

            </div>
        </div>

        <!-- 2. KHU VỰC SONG SONG: SƠ ĐỒ MẠNG P2P MESH & SƠ ĐỒ TRÌNH TỰ ĐỒNG BỘ 100% -->
        <div class="grid grid-cols-1 xl:grid-cols-12 gap-5 items-stretch">
            
            <!-- CỘT TRÁI (7 COLS): SƠ ĐỒ MẠNG P2P FULL-MESH 10 ĐỈNH -->
            <div class="xl:col-span-7 bg-white border border-slate-200 rounded-2xl p-4 sm:p-5 shadow-xs flex flex-col justify-between">
                <div class="flex items-center justify-between border-b border-slate-100 pb-2.5 mb-2">
                    <div>
                        <h2 class="text-xs font-bold text-slate-800 uppercase tracking-wider flex items-center gap-2">
                            SƠ ĐỒ MẠNG P2P FULL-MESH (10 ĐỈNH &bull; 45 LIÊN KẾT TCP)
                        </h2>
                        <p class="text-[11px] text-slate-500 mt-0.5">Số Nonce chạy trực tiếp tại từng máy; hạt sáng phát tán Gossip lan truyền khi tìm được Hash.</p>
                    </div>
                    <div class="flex items-center gap-2">
                        <button onclick="testAll45TcpLines()" class="px-2.5 py-1 bg-cyan-50 hover:bg-cyan-100 border border-cyan-200 text-cyan-800 text-[10.5px] font-bold rounded-lg transition shadow-2xs flex items-center gap-1 cursor-pointer" title="Phát tín hiệu kiểm tra toàn bộ 45 đường truyền TCP đồng thời">
                            <span class="w-1.5 h-1.5 rounded-full bg-cyan-600 animate-pulse"></span>
                            <span>Test Toàn Bộ 45 Dây TCP</span>
                        </button>
                        <span class="text-[10px] font-mono px-2 py-0.5 rounded bg-emerald-50 text-emerald-700 border border-emerald-200 font-bold shrink-0">
                            45/45 TCP Active
                        </span>
                    </div>
                </div>

                <!-- SVG CANVAS 10 ĐỈNH THẬP GIÁC ĐỐI XỨNG (620 x 540) -->
                <div class="relative w-full h-[510px] bg-slate-50/50 border border-slate-200/80 rounded-xl flex items-center justify-center overflow-hidden">
                    <svg class="w-full h-full" viewBox="0 0 620 540" id="p2pSvgCanvas">
                        <defs>
                            <filter id="glow" x="-40%" y="-40%" width="180%" height="180%">
                                <feGaussianBlur stdDeviation="3" result="blur" />
                                <feMerge>
                                    <feMergeNode in="blur" />
                                    <feMergeNode in="SourceGraphic" />
                                </feMerge>
                            </filter>
                            <filter id="superGlow" x="-60%" y="-60%" width="220%" height="220%">
                                <feGaussianBlur stdDeviation="6" result="blur1" />
                                <feGaussianBlur stdDeviation="2" result="blur2" />
                                <feMerge>
                                    <feMergeNode in="blur1" />
                                    <feMergeNode in="blur2" />
                                    <feMergeNode in="SourceGraphic" />
                                </feMerge>
                            </filter>
                        </defs>

                        <!-- LỚP 1: 45 ĐƯỜNG KẾT NỐI TĨNH FULL-MESH -->
                        <g stroke="#cbd5e1" stroke-width="1.2" stroke-dasharray="3,3" id="meshBaseLines"></g>

                        <!-- LỚP 2: TIA SÁNG GOSSIP ĐỘNG -->
                        <g id="dynamicGossipLayer"></g>

                        <!-- LỚP 3: VÒNG SÓNG RIPPLE XUNG KÍCH -->
                        <g id="rippleLayer"></g>



                        <!-- LỚP 5: 10 ĐỈNH MÁY TRẠM & KHUNG NONCE THỜI GIAN THỰC -->
                        <g id="nodesSvgLayer"></g>
                    </svg>
                </div>
            </div>

            <!-- CỘT PHẢI (5 COLS): SƠ ĐỒ TRÌNH TỰ GIAO TIẾP TỪNG BƯỚC ĐỒNG BỘ 100% -->
            <div class="xl:col-span-5 bg-white border border-slate-200 rounded-2xl p-4 sm:p-5 shadow-xs flex flex-col justify-between space-y-3">
                <div class="border-b border-slate-100 pb-2.5 flex items-center justify-between">
                    <div>
                        <h2 class="text-xs font-bold text-slate-800 uppercase tracking-wider flex items-center gap-1.5">
                            QUY TRÌNH GIAO TIẾP P2P (5 BƯỚC ĐỒNG THUẬN)
                        </h2>
                        <p class="text-[11px] text-slate-500 mt-0.5">Trình tự luân chuyển dữ liệu và đồng thuận Nakamoto đồng bộ với sơ đồ mạng.</p>
                    </div>
                    <div class="flex items-center gap-1">
                        <button onclick="goToStep(1)" class="w-6 h-6 rounded-md bg-slate-100 hover:bg-slate-200 text-[10px] font-bold text-slate-700 flex items-center justify-center font-mono">1</button>
                        <button onclick="goToStep(2)" class="w-6 h-6 rounded-md bg-slate-100 hover:bg-slate-200 text-[10px] font-bold text-slate-700 flex items-center justify-center font-mono">2</button>
                        <button onclick="goToStep(3)" class="w-6 h-6 rounded-md bg-slate-100 hover:bg-slate-200 text-[10px] font-bold text-slate-700 flex items-center justify-center font-mono">3</button>
                        <button onclick="goToStep(4)" class="w-6 h-6 rounded-md bg-slate-100 hover:bg-slate-200 text-[10px] font-bold text-slate-700 flex items-center justify-center font-mono">4</button>
                        <button onclick="goToStep(5)" class="w-6 h-6 rounded-md bg-slate-100 hover:bg-slate-200 text-[10px] font-bold text-slate-700 flex items-center justify-center font-mono">5</button>
                    </div>
                </div>

                <!-- 5 THẺ TRÌNH TỰ GIAO TIẾP (INTERACTIVE PROTOCOL STEP CARDS) -->
                <div class="space-y-2 flex-1 flex flex-col justify-between" id="stepsContainer">
                    
                    <!-- BƯỚC 1 -->
                    <div id="step-card-1" class="step-card border border-slate-200 rounded-xl p-2.5 cursor-pointer bg-white" onclick="goToStep(1)">
                        <div class="flex items-start justify-between">
                            <div class="flex items-center gap-2">
                                <span class="w-5 h-5 rounded-full bg-slate-100 text-slate-700 text-[10px] font-bold flex items-center justify-center font-mono shrink-0" id="step-badge-1">1</span>
                                <span class="text-xs font-bold text-slate-900">Phát Tán Phiếu Bầu (TX Gossip)</span>
                            </div>
                            <span class="text-[9px] font-mono font-bold text-slate-400 bg-slate-100 px-1.5 py-0.5 rounded" id="step-tag-1">CHỜ PHIẾU</span>
                        </div>
                        <p class="text-[10.5px] text-slate-500 mt-1 leading-relaxed pl-7">
                            Cử tri nộp phiếu tại Cổng Web &rarr; Node 1 nhận và phát tán gói tin TCP Gossip sang 9 máy còn lại. Hòm phiếu Mempool cả 10 máy tăng lên.
                        </p>
                        <div class="mt-1.5 pl-7 flex items-center gap-2 text-[9px] font-mono text-slate-400">
                            <span>Khung TCP: [4B Length][0x02 Type: TX_GOSSIP][Payload JSON]</span>
                        </div>
                    </div>

                    <!-- BƯỚC 2 -->
                    <div id="step-card-2" class="step-card border border-slate-200 rounded-xl p-2.5 cursor-pointer bg-white" onclick="goToStep(2)">
                        <div class="flex items-start justify-between">
                            <div class="flex items-center gap-2">
                                <span class="w-5 h-5 rounded-full bg-slate-100 text-slate-700 text-[10px] font-bold flex items-center justify-center font-mono shrink-0" id="step-badge-2">2</span>
                                <span class="text-xs font-bold text-slate-900">Tranh Tài Băm Song Song (Parallel PoW)</span>
                            </div>
                            <span class="text-[9px] font-mono font-bold text-slate-400 bg-slate-100 px-1.5 py-0.5 rounded" id="step-tag-2">SẴN SÀNG</span>
                        </div>
                        <p class="text-[10.5px] text-slate-500 mt-1 leading-relaxed pl-7">
                            10 máy khởi tạo 10 Java 21 Virtual Threads cùng giải SHA-256 tìm số Nonce thỏa mãn chuỗi băm có 6 số 0 dẫn đầu (<code class="bg-slate-100 px-1 rounded text-amber-700">000000...</code>).
                        </p>
                        <div class="mt-1.5 pl-7 flex items-center gap-2 text-[9px] font-mono text-slate-400">
                            <span>Công thức: SHA-256(PrevHash + Time + Data + Nonce) &lt; Target</span>
                        </div>
                    </div>

                    <!-- BƯỚC 3 -->
                    <div id="step-card-3" class="step-card border border-slate-200 rounded-xl p-2.5 cursor-pointer bg-white" onclick="goToStep(3)">
                        <div class="flex items-start justify-between">
                            <div class="flex items-center gap-2">
                                <span class="w-5 h-5 rounded-full bg-slate-100 text-slate-700 text-[10px] font-bold flex items-center justify-center font-mono shrink-0" id="step-badge-3">3</span>
                                <span class="text-xs font-bold text-slate-900">Phát Hiện Hash Hợp Lệ (Proof Discovery)</span>
                            </div>
                            <span class="text-[9px] font-mono font-bold text-slate-400 bg-slate-100 px-1.5 py-0.5 rounded" id="step-tag-3">CHỜ KHỐI</span>
                        </div>
                        <p class="text-[10.5px] text-slate-500 mt-1 leading-relaxed pl-7" id="step-desc-3">
                            Một máy tìm ra Nonce hợp lệ trước tiên &rarr; Khóa lời giải và đóng gói Khối mới chứa các phiếu bầu trong Mempool.
                        </p>
                        <div class="mt-1.5 pl-7 flex items-center gap-2 text-[9px] font-mono text-slate-400">
                            <span>Đóng gói: Index, Nonce, Hash, Timestamp, Merkle Root</span>
                        </div>
                    </div>

                    <!-- BƯỚC 4 -->
                    <div id="step-card-4" class="step-card border border-slate-200 rounded-xl p-2.5 cursor-pointer bg-white" onclick="goToStep(4)">
                        <div class="flex items-start justify-between">
                            <div class="flex items-center gap-2">
                                <span class="w-5 h-5 rounded-full bg-slate-100 text-slate-700 text-[10px] font-bold flex items-center justify-center font-mono shrink-0" id="step-badge-4">4</span>
                                <span class="text-xs font-bold text-slate-900">Phát Tán Khối Qua 9 Kênh TCP (Block Gossip)</span>
                            </div>
                            <span class="text-[9px] font-mono font-bold text-slate-400 bg-slate-100 px-1.5 py-0.5 rounded" id="step-tag-4">GOSSIP MESH</span>
                        </div>
                        <p class="text-[10.5px] text-slate-500 mt-1 leading-relaxed pl-7">
                            Máy tìm được Hash phát tán gói tin qua 9 socket TCP song song. Các hạt tín hiệu bay dọc theo đường truyền tới 9 máy còn lại.
                        </p>
                        <div class="mt-1.5 pl-7 flex items-center gap-2 text-[9px] font-mono text-slate-400">
                            <span>Khung TCP: [4B Length][0x03 Type: SYNC_BLOCK][Block JSON]</span>
                        </div>
                    </div>

                    <!-- BƯỚC 5 -->
                    <div id="step-card-5" class="step-card border border-slate-200 rounded-xl p-2.5 cursor-pointer bg-white" onclick="goToStep(5)">
                        <div class="flex items-start justify-between">
                            <div class="flex items-center gap-2">
                                <span class="w-5 h-5 rounded-full bg-slate-100 text-slate-700 text-[10px] font-bold flex items-center justify-center font-mono shrink-0" id="step-badge-5">5</span>
                                <span class="text-xs font-bold text-slate-900">Đồng Thuận Nakamoto (Consensus Agreement)</span>
                            </div>
                            <span class="text-[9px] font-mono font-bold text-slate-400 bg-slate-100 px-1.5 py-0.5 rounded" id="step-tag-5">ĐỒNG BỘ</span>
                        </div>
                        <p class="text-[10.5px] text-slate-500 mt-1 leading-relaxed pl-7">
                            9 máy đối chiếu tính toàn vẹn, hủy luồng tính toán tại chỗ, xóa phiếu đã đóng gói khỏi Mempool và đồng loạt cập nhật Sổ Cái lên Khối mới.
                        </p>
                        <div class="mt-1.5 pl-7 flex items-center gap-2 text-[9px] font-mono text-emerald-700 font-bold">
                            <span>Quy tắc Nakamoto: Chuỗi dài nhất và hợp lệ nhất được chấp thuận 100%</span>
                        </div>
                    </div>

                </div>
            </div>

        </div>

        <!-- 2.5 BANNER KHỐI MỚI NHẤT (REAL-TIME STATUS) -->
        <div id="latestBlockBanner" class="bg-white border border-slate-200 rounded-2xl p-4 sm:p-5 flex flex-col md:flex-row items-start md:items-center justify-between gap-4 font-mono shadow-xs transition-all duration-300">
            <div class="flex items-center gap-3.5 min-w-0">
                <div class="w-10 h-10 rounded-xl bg-slate-100 border border-slate-200 flex items-center justify-center text-slate-700 font-black text-xs shrink-0 font-sans" id="latestBlockIcon">
                    BLK
                </div>
                <div class="min-w-0">
                    <div class="flex items-center gap-2">
                        <span class="text-xs font-bold text-slate-900" id="latestBlockTitle">Khối #--</span>
                        <span class="text-[10px] font-bold text-emerald-700 bg-emerald-50 border border-emerald-200 px-2 py-0.5 rounded-full" id="latestBlockStatus">Đã Khóa Vào Sổ Cái</span>
                    </div>
                    <div class="text-[11px] text-slate-600 truncate max-w-[650px] mt-0.5 font-sans" id="latestBlockContent">
                        Đang đồng bộ dữ liệu Sổ Cái VoteChain...
                    </div>
                </div>
            </div>
            <div class="flex items-center gap-5 text-xs text-slate-500 shrink-0">
                <div>
                    <span class="text-[9px] text-slate-400 uppercase block font-bold font-sans">Mã Hash (6 Zeros):</span>
                    <span class="text-emerald-700 font-bold font-mono text-[11px]" id="latestBlockHash">000000...</span>
                </div>
                <div>
                    <span class="text-[9px] text-slate-400 uppercase block font-bold font-sans">Nonce Hợp Lệ:</span>
                    <span class="text-slate-900 font-bold font-mono text-[11px]" id="latestBlockNonce">--</span>
                </div>
            </div>
        </div>

        <!-- 3. DANH SÁCH 10 MÁY (NODES) TRONG CỤM MẠNG P2P (GRID 2 HÀNG X 5 CỘT) -->
        <div>
            <div class="flex items-center justify-between mb-3">
                <h2 class="text-xs font-bold text-slate-500 uppercase tracking-wider flex items-center gap-2">
                    BẢNG THÔNG SỐ CHI TIẾT 10 MÁY TRẠM ĐỘC LẬP
                </h2>
                <span class="text-xs text-slate-400 font-mono">2 Hàng &times; 5 Cột</span>
            </div>

            <div class="flex items-center justify-between mb-3 flex-wrap gap-2">
                <div class="flex items-center gap-2">
                    <span id="nodeTotalBadge" class="text-[10px] font-bold text-blue-700 bg-blue-50 border border-blue-200 px-2 py-0.5 rounded-full font-mono">10 Nodes Active</span>
                </div>
                <button onclick="openJoinNodeModal()" class="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 text-white font-bold text-xs shadow-xs hover:shadow-md transition cursor-pointer">
                    <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"/></svg>
                    <span>➕ Đăng Ký Tham Gia Node Mới</span>
                </button>
            </div>
            <div class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-5 gap-3" id="nodesGrid">
                <!-- 10 Thẻ máy sẽ được sinh ra chuẩn mực tại đây -->
            </div>
        </div>
    """;

    private static final String PART_2 = """
        <!-- 4. CONSOLE TERMINAL GIAO THỨC P2P & NAKAMOTO CONSENSUS -->
        <div class="bg-slate-900 border border-slate-800 rounded-2xl p-4 sm:p-5 flex flex-col justify-between font-mono shadow-xs">
            <div>
                <div class="flex items-center justify-between border-b border-slate-800 pb-3 mb-3">
                    <div class="flex items-center gap-2">
                        <span class="w-2.5 h-2.5 rounded-full bg-rose-500"></span>
                        <span class="w-2.5 h-2.5 rounded-full bg-amber-500"></span>
                        <span class="w-2.5 h-2.5 rounded-full bg-emerald-500"></span>
                        <span class="text-xs font-bold text-slate-300 ml-2">NHẬT KÝ GIAO THỨC P2P & ĐỒNG THUẬN NAKAMOTO (10 NODES)</span>
                    </div>
                    <button onclick="clearTerminal()" class="text-[11px] text-slate-400 hover:text-white px-2.5 py-1 rounded-md bg-slate-800 hover:bg-slate-700 transition cursor-pointer">
                        Xóa nhật ký
                    </button>
                </div>

                <div id="terminalBody" class="h-[190px] overflow-y-auto space-y-1.5 text-xs text-slate-300 pr-1">
                    <!-- Nhật ký P2P động -->
                </div>
            </div>

            <div class="pt-3 border-t border-slate-800 flex items-center justify-between text-[11px] text-slate-400">
                <span>Cụm máy: <strong class="text-emerald-400">10 Nodes P2P Cluster Running</strong></span>
                <span id="termCounter">0 sự kiện</span>
            </div>
        </div>

        <!-- MODAL DANG KY THAM GIA NODE MOI -->
        <div id="joinNodeModal" class="hidden fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4">
            <div class="bg-white rounded-2xl max-w-md w-full p-6 shadow-2xl border border-slate-200 font-sans">
                <div class="flex items-center justify-between pb-3 border-b border-slate-100">
                    <div class="flex items-center gap-2">
                        <span class="text-xl">🚀</span>
                        <h3 class="text-base font-bold text-slate-900">Đăng Ký Nút Mạng (Node) Mới</h3>
                    </div>
                    <button onclick="closeJoinNodeModal()" class="text-slate-400 hover:text-slate-600 text-lg font-bold cursor-pointer">&times;</button>
                </div>
                
                <div class="mt-4 space-y-4 text-left">
                    <div>
                        <label class="block text-xs font-bold text-slate-700 mb-1">Tên Máy / Người Tham Gia:</label>
                        <input type="text" id="newNodeName" placeholder="Ví dụ: Nguyễn Hoàng Nhật (Miner #11)" class="w-full text-sm border border-slate-300 rounded-xl px-3.5 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500 font-sans">
                    </div>
                    <div>
                        <label class="block text-xs font-bold text-slate-700 mb-1">Vai Trò Nút Mạng:</label>
                        <select id="newNodeRole" class="w-full text-sm border border-slate-300 rounded-xl px-3.5 py-2.5 focus:outline-none focus:ring-2 focus:ring-blue-500 font-sans">
                            <option value="Nút Thợ Đào PoW Cá Nhân">⚡ Nút Thợ Đào PoW Cá Nhân (Miner Node)</option>
                            <option value="Nút Kiểm Toán & Sổ Cái Độc Lập">🛡️ Nút Kiểm Toán & Sổ Cái Độc Lập (Auditor Node)</option>
                            <option value="Trạm Bỏ Phiếu Cử Tri Mở Rộng">🗳️ Trạm Bỏ Phiếu Cử Tri Mở Rộng (Voter Station)</option>
                        </select>
                    </div>
                    <div class="bg-blue-50 border border-blue-200 rounded-xl p-3 text-xs text-blue-800">
                        💡 Sau khi tham gia, máy của bạn sẽ được cấp 1 Port TCP ảo, đồng bộ chuỗi khối và cùng tham gia giải bài toán PoW với cụm mạng!
                    </div>
                </div>

                <div class="mt-6 flex justify-end gap-2.5">
                    <button onclick="closeJoinNodeModal()" class="px-4 py-2 text-xs font-bold text-slate-600 hover:bg-slate-100 rounded-xl transition cursor-pointer">Hủy</button>
                    <button onclick="submitJoinNode()" id="btnSubmitJoinNode" class="px-5 py-2 text-xs font-bold text-white bg-blue-600 hover:bg-blue-700 rounded-xl shadow-xs transition cursor-pointer">Xác Nhận Tham Gia</button>
                </div>
            </div>
        </div>
    </main>

    <!-- FOOTER HỆ THỐNG -->
    <footer class="border-t border-slate-200 bg-white py-3.5 text-center text-xs text-slate-500 font-mono">
        Hệ Thống Bầu Cử Điện Tử Blockchain &bull; Mô Phỏng Cụm 10 Máy PoW &bull; HUTECH 2026
    </footer>

    <!-- SCRIPT ĐIỀU KHIỂN & HOẠT ẢNH LAN TRUYỀN P2P MESH -->
    <script>
        let isMiningActive = false;
        let isContinuousActive = false;
        let isSlowMoRunning = false;
        let lastRenderedBlockIndex = -1;
        let seenLogs = new Set();
        let nonceIntervalId = null;
        let slowMoTimeoutId = null;
        let currentActiveStep = 0;
        let lastKnownMempoolCount = 0;

        // CẤU HÌNH 10 MÁY VỚI TỌA ĐỘ VÒNG TRÒN DECAGON TRÊN CANVAS 620 x 540 (Tâm 310, 270 - Bán kính 190)
        const nodePositions = {
            'Node-1':  { x: 310, y: 80,  port: 8001, name: 'Máy 1: Khoa CNTT', role: 'Gateway & Điều Phối', short: 'Khoa CNTT', color: '#2563eb' },
            'Node-2':  { x: 422, y: 116, port: 8002, name: 'Máy 2: Ban Thanh Tra', role: 'Giám Sát & Đối Soát', short: 'Ban Thanh Tra', color: '#059669' },
            'Node-3':  { x: 491, y: 211, port: 8003, name: 'Máy 3: Hội Đồng SV', role: 'Lưu Trữ & Xác Thực', short: 'Hội Đồng SV', color: '#7c3aed' },
            'Node-4':  { x: 491, y: 329, port: 8004, name: 'Máy 4: TT Dữ Liệu', role: 'Tính Toán Phân Tán', short: 'TT Dữ Liệu', color: '#e11d48' },
            'Node-5':  { x: 422, y: 424, port: 8005, name: 'Máy 5: Trạm Dự Phòng', role: 'Trạm Ung Văn Khiêm', short: 'Trạm Dự Phòng', color: '#d97706' },
            'Node-6':  { x: 310, y: 460, port: 8006, name: 'Máy 6: Khoa Điện-ĐT', role: 'Nút Xác Thực Phụ', short: 'Khoa Điện-ĐT', color: '#0891b2' },
            'Node-7':  { x: 198, y: 424, port: 8007, name: 'Máy 7: Viện Nghiên Cứu', role: 'Phân Tích Dữ Liệu', short: 'Viện NC', color: '#4f46e5' },
            'Node-8':  { x: 129, y: 329, port: 8008, name: 'Máy 8: Ban Khảo Thí SV', role: 'Tra Cứu Cử Tri', short: 'Khảo Thí SV', color: '#0d9488' },
            'Node-9':  { x: 129, y: 211, port: 8009, name: 'Máy 9: Đoàn Trường', role: 'Giám Sát Bầu Cử', short: 'Đoàn Trường', color: '#be123c' },
            'Node-10': { x: 198, y: 116, port: 8010, name: 'Máy 10: TT Khảo Thí', role: 'Khóa Sổ Cái Dự Phòng', short: 'TT Khảo Thí', color: '#b45309' }
        };

        const nodeIds = Object.keys(nodePositions);
        const all45Links = [];

        // VẼ 45 ĐƯỜNG NỐI TĨNH VÀ KHỞI TẠO 10 ĐỈNH TRÊN SVG CANVAS
        function initSvgTopology() {
            const baseLines = document.getElementById('meshBaseLines');
            const nodesLayer = document.getElementById('nodesSvgLayer');
            if (!baseLines || !nodesLayer) return;

            // 1. Lưu danh sách và vẽ 45 đường nối giữa tất cả các cặp đỉnh (10 * 9 / 2 = 45)
            all45Links.length = 0;
            baseLines.innerHTML = '';
            for (let i = 0; i < nodeIds.length; i++) {
                for (let j = i + 1; j < nodeIds.length; j++) {
                    const p1 = nodePositions[nodeIds[i]];
                    const p2 = nodePositions[nodeIds[j]];
                    all45Links.push({ idA: nodeIds[i], idB: nodeIds[j], pA: p1, pB: p2 });

                    const line = document.createElementNS('http://www.w3.org/2000/svg', 'line');
                    line.setAttribute('x1', p1.x);
                    line.setAttribute('y1', p1.y);
                    line.setAttribute('x2', p2.x);
                    line.setAttribute('y2', p2.y);
                    line.setAttribute('id', 'mesh-line-' + nodeIds[i] + '-' + nodeIds[j]);
                    baseLines.appendChild(line);
                }
            }

            // 2. Vẽ 10 đỉnh nút máy kèm Khung Nonce chạy thực tế
            nodesLayer.innerHTML = '';
            nodeIds.forEach((id, idx) => {
                const node = nodePositions[id];
                const g = document.createElementNS('http://www.w3.org/2000/svg', 'g');
                g.setAttribute('id', 'svg-node-group-' + id);
                g.setAttribute('class', 'cursor-pointer hover:opacity-90');
                g.setAttribute('title', 'Nhấp để chỉ định ' + node.name + ' (:' + node.port + ') độc lập giải PoW');
                g.onclick = () => mineSingleNode(id);

                // Vòng tròn node máy
                const circle = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
                circle.setAttribute('id', 'svg-circle-' + id);
                circle.setAttribute('cx', node.x);
                circle.setAttribute('cy', node.y);
                circle.setAttribute('r', '15.5');
                circle.setAttribute('fill', '#ffffff');
                circle.setAttribute('stroke', node.color);
                circle.setAttribute('stroke-width', '2.5');

                // Nhãn N1 -> N10
                const textLabel = document.createElementNS('http://www.w3.org/2000/svg', 'text');
                textLabel.setAttribute('x', node.x);
                textLabel.setAttribute('y', node.y + 3.5);
                textLabel.setAttribute('text-anchor', 'middle');
                textLabel.setAttribute('fill', '#0f172a');
                textLabel.setAttribute('font-size', '9');
                textLabel.setAttribute('font-weight', 'bold');
                textLabel.setAttribute('font-family', 'JetBrains Mono');
                textLabel.textContent = 'N' + (idx + 1);

                // Port & Tên máy rút gọn
                const textInfo = document.createElementNS('http://www.w3.org/2000/svg', 'text');
                textInfo.setAttribute('text-anchor', 'middle');
                textInfo.setAttribute('font-family', 'Plus Jakarta Sans');
                textInfo.setAttribute('font-size', '8.5');
                textInfo.setAttribute('font-weight', 'bold');
                textInfo.setAttribute('fill', '#334155');

                let nameY = node.y < 270 ? node.y - 19 : node.y + 24;
                textInfo.setAttribute('x', node.x);
                textInfo.setAttribute('y', nameY);
                textInfo.textContent = node.short + ' (:' + node.port + ')';

                // KHUNG NONCE THỜI GIAN THỰC (LIVE NONCE TICKER BADGE RÕ NÉT)
                const nonceBadgeG = document.createElementNS('http://www.w3.org/2000/svg', 'g');
                nonceBadgeG.setAttribute('id', 'svg-nonce-g-' + id);

                let badgeY = node.y < 270 ? node.y - 39 : node.y + 33;
                let badgeX = node.x - 48;

                const badgeRect = document.createElementNS('http://www.w3.org/2000/svg', 'rect');
                badgeRect.setAttribute('x', badgeX);
                badgeRect.setAttribute('y', badgeY);
                badgeRect.setAttribute('width', '96');
                badgeRect.setAttribute('height', '16.5');
                badgeRect.setAttribute('rx', '4.5');
                badgeRect.setAttribute('fill', '#f8fafc');
                badgeRect.setAttribute('stroke', '#cbd5e1');
                badgeRect.setAttribute('stroke-width', '1');
                badgeRect.setAttribute('id', 'svg-nonce-rect-' + id);

                const badgeText = document.createElementNS('http://www.w3.org/2000/svg', 'text');
                badgeText.setAttribute('x', node.x);
                badgeText.setAttribute('y', badgeY + 11.5);
                badgeText.setAttribute('text-anchor', 'middle');
                badgeText.setAttribute('font-family', 'JetBrains Mono');
                badgeText.setAttribute('font-size', '9');
                badgeText.setAttribute('font-weight', 'bold');
                badgeText.setAttribute('fill', '#475569');
                badgeText.setAttribute('id', 'svg-nonce-text-' + id);
                badgeText.textContent = 'Nonce: 0';

                nonceBadgeG.appendChild(badgeRect);
                nonceBadgeG.appendChild(badgeText);

                g.appendChild(circle);
                g.appendChild(textLabel);
                g.appendChild(textInfo);
                g.appendChild(nonceBadgeG);
                nodesLayer.appendChild(g);
            });
        }

        // SINH 10 THẺ MÁY GIAO DIỆN SÁNG (2 HÀNG X 5 CỘT)
        function initNodeCardsGrid() {
            const grid = document.getElementById('nodesGrid');
            if (!grid) return;
            grid.innerHTML = '';

            nodeIds.forEach((id, idx) => {
                const node = nodePositions[id];
                const card = document.createElement('div');
                card.id = 'card-' + id;
                card.className = 'node-card bg-white border border-slate-200 rounded-2xl p-3 flex flex-col justify-between space-y-2 shadow-xs cursor-pointer hover:border-blue-400 transition';
                card.setAttribute('title', 'Nhấp để ' + node.name + ' (:' + node.port + ') độc lập giải PoW');
                card.onclick = () => mineSingleNode(id);

                card.innerHTML = `
                    <div class="flex items-start justify-between">
                        <div>
                            <span class="text-[9px] font-mono font-bold text-blue-700 bg-blue-50 border border-blue-200 px-1.5 py-0.5 rounded">PORT ${node.port}</span>
                            <h3 class="text-xs font-bold text-slate-900 mt-1">${node.name}</h3>
                            <p class="text-[9px] text-slate-400 font-medium">${node.role}</p>
                        </div>
                        <span id="status-${id}" class="text-[9px] font-mono font-bold text-emerald-700 bg-emerald-50 border border-emerald-200 px-1.5 py-0.5 rounded-full">ONLINE</span>
                    </div>

                    <div class="space-y-1 bg-slate-50 border border-slate-200/70 rounded-xl p-2 text-[10.5px] font-mono">
                        <div class="flex justify-between text-slate-500">
                            <span class="font-sans text-[10px]">Tốc độ:</span>
                            <span class="text-slate-800 font-bold">${220 + (idx * 15)} KH/s</span>
                        </div>
                        <div class="flex justify-between text-slate-500">
                            <span class="font-sans text-[10px]">Khối đã tìm:</span>
                            <span id="mined-${id}" class="text-slate-900 font-bold">0 blocks</span>
                        </div>
                        <div class="flex justify-between text-slate-500">
                            <span class="font-sans text-[10px]">Mempool:</span>
                            <span id="mem-${id}" class="text-blue-700 font-bold">0 phiếu</span>
                        </div>
                        <div class="flex justify-between text-slate-500">
                            <span class="font-sans text-[10px]">Chiều cao:</span>
                            <span id="height-${id}" class="text-slate-800 font-bold">#0</span>
                        </div>
                    </div>

                    <div id="winner-tag-${id}" class="hidden text-center py-1 bg-blue-50 border border-blue-200 rounded-lg text-blue-700 text-[10px] font-mono font-bold">
                        ĐÃ TÌM ĐƯỢC HASH
                    </div>

                    <div class="text-[9px] text-blue-600 font-medium text-center pt-1 border-t border-slate-100 flex items-center justify-center gap-1 font-sans">
                        <span>⚡</span> <span>Bấm để khai thác độc lập</span>
                    </div>
                `;
                grid.appendChild(card);
            });
        }

        function addTermLog(msg, color = 'text-slate-300') {
            const body = document.getElementById('terminalBody');
            const d = document.createElement('div');
            d.className = color;
            d.innerHTML = msg;
            body.appendChild(d);
            body.scrollTop = body.scrollHeight;

            const countEl = document.getElementById('termCounter');
            if (countEl) countEl.textContent = body.children.length + ' sự kiện';
        }

        function clearTerminal() {
            document.getElementById('terminalBody').innerHTML = '';
            document.getElementById('termCounter').textContent = '0 sự kiện';
            seenLogs.clear();
        }

        function setMiningUiState(isMining) {
            isMiningActive = isMining;
            const btnRace = document.getElementById('btnRace');
            if (btnRace) {
                btnRace.disabled = isMining || isContinuousActive;
                btnRace.style.opacity = (isMining || isContinuousActive) ? '0.6' : '1';
                btnRace.style.pointerEvents = (isMining || isContinuousActive) ? 'none' : 'auto';
            }

            if (isMining) {
                startSvgNonceTicker();
            } else if (!isContinuousActive) {
                stopSvgNonceTicker();
            }
        }

        // =========================================================================
        // ĐIỀU KHIỂN ĐỒNG BỘ 5 BƯỚC QUY TRÌNH (SYNCHRONIZED STEPPER)
        // =========================================================================
        function highlightStep(stepNum, extraData = {}) {
            currentActiveStep = stepNum;

            // 1. Cập nhật giao diện 5 Thẻ bước bên cột phải
            for (let i = 1; i <= 5; i++) {
                const card = document.getElementById('step-card-' + i);
                const tag = document.getElementById('step-tag-' + i);
                const badge = document.getElementById('step-badge-' + i);
                if (!card) continue;

                card.classList.remove('active-step', 'active-step-amber', 'active-step-green');
                if (tag) tag.className = 'text-[9px] font-mono font-bold text-slate-400 bg-slate-100 px-1.5 py-0.5 rounded';
                if (badge) badge.className = 'w-5 h-5 rounded-full bg-slate-100 text-slate-700 text-[10px] font-bold flex items-center justify-center font-mono shrink-0';

                if (i < stepNum) {
                    if (tag) {
                        tag.className = 'text-[9px] font-mono font-bold text-emerald-700 bg-emerald-50 border border-emerald-200 px-1.5 py-0.5 rounded';
                        tag.textContent = 'HOÀN TẤT';
                    }
                    if (badge) badge.className = 'w-5 h-5 rounded-full bg-emerald-100 text-emerald-700 text-[10px] font-bold flex items-center justify-center font-mono shrink-0';
                }
            }

            const activeCard = document.getElementById('step-card-' + stepNum);
            const activeTag = document.getElementById('step-tag-' + stepNum);
            const activeBadge = document.getElementById('step-badge-' + stepNum);

            if (stepNum === 1) {
                if (activeCard) activeCard.classList.add('active-step');
                if (activeTag) {
                    activeTag.className = 'text-[9px] font-mono font-bold text-blue-700 bg-blue-50 border border-blue-200 px-1.5 py-0.5 rounded animate-pulse';
                    activeTag.textContent = 'ĐANG PHÁT TÁN';
                }
                if (activeBadge) activeBadge.className = 'w-5 h-5 rounded-full bg-blue-600 text-white text-[10px] font-bold flex items-center justify-center font-mono shrink-0';
            } else if (stepNum === 2) {
                if (activeCard) activeCard.classList.add('active-step-amber');
                if (activeTag) {
                    activeTag.className = 'text-[9px] font-mono font-bold text-amber-700 bg-amber-50 border border-amber-200 px-1.5 py-0.5 rounded animate-pulse';
                    activeTag.textContent = 'ĐANG BĂM HASH';
                }
                if (activeBadge) activeBadge.className = 'w-5 h-5 rounded-full bg-amber-600 text-white text-[10px] font-bold flex items-center justify-center font-mono shrink-0';
            } else if (stepNum === 3) {
                if (activeCard) activeCard.classList.add('active-step');
                if (activeTag) {
                    activeTag.className = 'text-[9px] font-mono font-bold text-blue-700 bg-blue-50 border border-blue-200 px-1.5 py-0.5 rounded';
                    activeTag.textContent = 'TÌM THẤY HASH';
                }
                if (activeBadge) activeBadge.className = 'w-5 h-5 rounded-full bg-blue-600 text-white text-[10px] font-bold flex items-center justify-center font-mono shrink-0';
                if (extraData.winnerName) {
                    const desc3 = document.getElementById('step-desc-3');
                    if (desc3) desc3.innerHTML = '<strong>' + extraData.winnerName + '</strong> (' + (extraData.winnerId || '') + ') đã tìm thấy Nonce: <span class="font-mono text-blue-700 font-bold">' + (extraData.nonce || '') + '</span>!';
                }
            } else if (stepNum === 4) {
                if (activeCard) activeCard.classList.add('active-step');
                if (activeTag) {
                    activeTag.className = 'text-[9px] font-mono font-bold text-indigo-700 bg-indigo-50 border border-indigo-200 px-1.5 py-0.5 rounded animate-pulse';
                    activeTag.textContent = 'LAN TRUYỀN 9 TIA';
                }
                if (activeBadge) activeBadge.className = 'w-5 h-5 rounded-full bg-indigo-600 text-white text-[10px] font-bold flex items-center justify-center font-mono shrink-0';
            } else if (stepNum === 5) {
                if (activeCard) activeCard.classList.add('active-step-green');
                if (activeTag) {
                    activeTag.className = 'text-[9px] font-mono font-bold text-emerald-700 bg-emerald-50 border border-emerald-200 px-1.5 py-0.5 rounded';
                    activeTag.textContent = 'ĐỒNG THUẬN 10/10';
                }
                if (activeBadge) activeBadge.className = 'w-5 h-5 rounded-full bg-emerald-600 text-white text-[10px] font-bold flex items-center justify-center font-mono shrink-0';
            }
        }

        // BẤM VÀO SỐ BƯỚC THỦ CÔNG ĐỂ XEM MÔ PHỎNG BƯỚC ĐÓ
        function goToStep(stepNum) {
            if (isSlowMoRunning || isMiningActive) return;

            const curHeight = (lastRenderedBlockIndex >= 0) ? lastRenderedBlockIndex : 1;
            const targetWinner = 'Node-4';
            const targetNonce = 4028190;
            highlightStep(stepNum, { winnerName: 'Máy 4: TT Dữ Liệu', winnerId: targetWinner, nonce: targetNonce });

            if (stepNum === 1) {
                stopSvgNonceTicker();
                clearSvgSignals();
                animateTxGossip('Node-1');
                addTermLog('🗳️ <strong class="text-sky-400">[BƯỚC 1: TX GOSSIP]</strong> Cử tri nộp phiếu &rarr; Gateway (:8001) tiếp nhận và phát tán TCP Gossip lan truyền qua 10 máy trạm!', 'text-sky-300 font-bold');
            } else if (stepNum === 2) {
                clearSvgSignals();
                startSvgNonceTicker();
                nodeIds.forEach(id => {
                    const status = document.getElementById('status-' + id);
                    const card = document.getElementById('card-' + id);
                    if (card) card.classList.add('computing-pulse');
                    if (status) {
                        status.className = 'text-[9px] font-mono font-bold text-amber-700 bg-amber-50 border border-amber-300 px-1.5 py-0.5 rounded-full';
                        status.textContent = 'ĐANG CHẠY';
                    }
                });
                addTermLog('⚙️ <strong class="text-amber-400">[BƯỚC 2: PARALLEL POW]</strong> 10 Luồng Virtual Threads cùng giải SHA-256 tìm 6 số 0 dẫn đầu (000000...). Nonce đang chạy...', 'text-amber-300 font-bold');
            } else if (stepNum === 3) {
                stopSvgNonceTicker();
                clearSvgSignals();
                highlightWinnerNodeOnSvg(targetWinner, targetNonce);
                addTermLog('🏆 <strong class="text-blue-400">[BƯỚC 3: PROOF FOUND]</strong> Máy 4: TT Dữ Liệu đã tìm thấy Hash hợp lệ [Nonce: ' + targetNonce.toLocaleString('vi-VN') + ']!', 'text-blue-300 font-bold');
            } else if (stepNum === 4) {
                stopSvgNonceTicker();
                clearSvgSignals();
                animateBlockGossip(targetWinner, curHeight, targetNonce);
                addTermLog('📡 <strong class="text-indigo-400">[BƯỚC 4: BLOCK GOSSIP]</strong> Máy 4 phát tán gói tin SYNC_BLOCK #' + curHeight + ' qua các kênh TCP tới 9 máy trạm...', 'text-indigo-300 font-bold');
            } else if (stepNum === 5) {
                stopSvgNonceTicker();
                clearSvgSignals();
                nodeIds.forEach(id => {
                    const st = document.getElementById('status-' + id);
                    const card = document.getElementById('card-' + id);
                    if (card) card.classList.remove('computing-pulse');
                    if (st) {
                        st.className = 'text-[9px] font-mono font-bold text-slate-700 bg-slate-100 border border-slate-300 px-1.5 py-0.5 rounded-full';
                        st.textContent = 'ĐÃ ĐỒNG BỘ';
                    }
                    const peerTxt = document.getElementById('svg-nonce-text-' + id);
                    const peerRect = document.getElementById('svg-nonce-rect-' + id);
                    if (peerTxt) {
                        peerTxt.textContent = 'Khối #' + curHeight + ' OK';
                        peerTxt.setAttribute('fill', '#047857');
                    }
                    if (peerRect) {
                        peerRect.setAttribute('fill', '#d1fae5');
                        peerRect.setAttribute('stroke', '#6ee7b7');
                    }
                });
                addTermLog('✅ <strong class="text-emerald-400">[BƯỚC 5: NAKAMOTO CONSENSUS]</strong> Cả 10 máy đối chiếu chuỗi hợp lệ, cập nhật Sổ Cái lên Khối #' + curHeight + '!', 'text-emerald-400 font-bold');
            }
        }

        // =========================================================================
        // CHẾ ĐỘ MÔ PHỎNG CHẬM 5 BƯỚC (CHUẨN 4 GIÂY CHO MỖI BƯỚC QUAN SÁT)
        // =========================================================================
        async function runSlowMotionSequence() {
            if (isMiningActive || isContinuousActive || isSlowMoRunning) return;

            isSlowMoRunning = true;
            setMiningUiState(true);
            const btn = document.getElementById('btnSlowMo');
            btn.innerHTML = '<span class="inline-block animate-spin mr-1">⏳</span> <span>ĐANG CHẠY MÔ PHỎNG (4S/BƯỚC)...</span>';

            try {
                // =================================================================
                // BƯỚC 1: PHÁT TÁN PHIẾU BẦU (TX GOSSIP) (4 GIÂY)
                // =================================================================
                highlightStep(1);
                addTermLog('🗳️ <strong class="text-sky-400">[BƯỚC 1/5 - TX GOSSIP]</strong> Cử tri nộp phiếu &rarr; Nút Gateway (:8001) tiếp nhận và phát tán TCP Gossip qua toàn mạng P2P...', 'text-sky-300 font-bold');
                
                // Thật sự nạp phiếu vào Mempool backend
                fetch('/api/cluster/seed-votes', { method: 'POST' }).catch(() => {});
                const memBadge = document.getElementById('mempoolBadge');
                if (memBadge) memBadge.textContent = '3 phiếu';

                animateTxGossip('Node-1');
                await new Promise(r => setTimeout(r, 4000));

                // =================================================================
                // BƯỚC 2: TRANH TÀI BĂM SONG SONG (PARALLEL POW) (4 GIÂY)
                // =================================================================
                highlightStep(2);
                addTermLog('⚙️ <strong class="text-amber-400">[BƯỚC 2/5 - PARALLEL POW]</strong> 10 Máy trạm kích hoạt 10 Virtual Threads cùng giải SHA-256 tìm 6 số 0 dẫn đầu. Nonce đang chạy...', 'text-amber-300 font-bold');
                
                nodeIds.forEach(id => {
                    const card = document.getElementById('card-' + id);
                    const status = document.getElementById('status-' + id);
                    if (card) card.classList.add('computing-pulse');
                    if (status) {
                        status.className = 'text-[9px] font-mono font-bold text-amber-700 bg-amber-50 border border-amber-300 px-1.5 py-0.5 rounded-full';
                        status.textContent = 'ĐANG CHẠY';
                    }
                });

                startSvgNonceTicker();

                let minePromise = fetch('/api/cluster/mine?race=true', { method: 'POST' })
                    .then(r => r.json())
                    .catch(e => ({ error: e.message }));

                // ĐẢM BẢO CHẠY ĐỦ 4 GIÂY ĐỂ QUAN SÁT TẤT CẢ CÁC MÁY BĂM NONCE
                await new Promise(r => setTimeout(r, 4000));

                const data = await minePromise;
                const winnerId = (data && data.success && data.winnerId) ? data.winnerId : 'Node-4';
                const winnerName = (data && data.success && data.winnerName) ? data.winnerName : 'Máy 4: TT Dữ Liệu';
                const winnerNonce = (data && data.success && data.nonce) ? data.nonce : 4028190;
                const blockIdx = (data && data.success && data.blockIndex !== undefined) ? data.blockIndex : (lastRenderedBlockIndex > 0 ? lastRenderedBlockIndex + 1 : 1);

                // =================================================================
                // BƯỚC 3: PHÁT HIỆN HASH HỢP LỆ (PROOF DISCOVERY) (4 GIÂY)
                // =================================================================
                stopSvgNonceTicker();
                clearSvgSignals();
                highlightStep(3, { winnerName: winnerName, winnerId: winnerId, nonce: winnerNonce });
                addTermLog('🏆 <strong class="text-blue-400">[BƯỚC 3/5 - PROOF FOUND]</strong> ⚡ ' + winnerName + ' (' + winnerId + ') ĐÃ TÌM THẤY HASH HỢP LỆ! [Nonce: ' + Number(winnerNonce).toLocaleString('vi-VN') + ']. Nổi bật máy trên sơ đồ 4s...', 'text-blue-300 font-bold');
                highlightWinnerNodeOnSvg(winnerId, winnerNonce);
                await new Promise(r => setTimeout(r, 4000));

                // =================================================================
                // BƯỚC 4: PHÁT TÁN KHỐI QUA MẠNG TCP (BLOCK GOSSIP) (4 GIÂY)
                // =================================================================
                highlightStep(4);
                addTermLog('📡 <strong class="text-indigo-400">[BƯỚC 4/5 - BLOCK GOSSIP]</strong> ' + winnerId + ' phát tán gói tin SYNC_BLOCK #' + blockIdx + ' qua các kênh TCP tới 9 máy trạm...', 'text-indigo-300 font-bold');
                animateBlockGossip(winnerId, blockIdx, winnerNonce);
                await new Promise(r => setTimeout(r, 4000));

                // =================================================================
                // BƯỚC 5: ĐỒNG THUẬN NAKAMOTO 10/10 MÁY (4 GIÂY)
                // =================================================================
                highlightStep(5);
                addTermLog('✅ <strong class="text-emerald-400">[BƯỚC 5/5 - NAKAMOTO CONSENSUS]</strong> Cả 10 máy đã xác thực hợp lệ, chấp thuận quy tắc Chuỗi Dài Nhất, khóa Khối #' + blockIdx + ' vào Sổ Cái và dọn sạch Mempool!', 'text-emerald-400 font-bold');
                
                nodeIds.forEach(id => {
                    const status = document.getElementById('status-' + id);
                    const card = document.getElementById('card-' + id);
                    if (card) card.classList.remove('computing-pulse');
                    if (status) {
                        status.className = 'text-[9px] font-mono font-bold text-slate-700 bg-slate-100 border border-slate-300 px-1.5 py-0.5 rounded-full';
                        status.textContent = 'ĐÃ ĐỒNG BỘ';
                    }
                    const memEl = document.getElementById('mem-' + id);
                    if (memEl) memEl.textContent = '0 phiếu';
                });

                if (memBadge) memBadge.textContent = '0 phiếu';
                lastRenderedBlockIndex = blockIdx;

                await new Promise(r => setTimeout(r, 4000));
            } catch (err) {
                addTermLog('❌ Lỗi: ' + err.message, 'text-rose-400');
            } finally {
                isSlowMoRunning = false;
                setMiningUiState(false);
                btn.innerHTML = '<span>Mô Phỏng Chậm 5 Bước (Slow-Mo)</span>';
                fetchClusterState();
            }
        }
    """;

    private static final String PART_3 = """
        // =========================================================================
        // KHỐI VẬN HÀNH TICKER NONCE & LÀM NỔI BẬT MÁY CHIẾN THẮNG TRÊN SƠ ĐỒ SVG
        // =========================================================================
        let currentNonces = {};

        function startSvgNonceTicker() {
            if (nonceIntervalId) clearInterval(nonceIntervalId);
            clearWinnerHighlights();
            
            nodeIds.forEach((id, idx) => {
                if (!currentNonces[id]) {
                    currentNonces[id] = Math.floor(Math.random() * 200000) + (idx * 45000);
                }
            });

            nonceIntervalId = setInterval(() => {
                nodeIds.forEach(id => {
                    currentNonces[id] += Math.floor(Math.random() * 9500) + 3500;
                    const txt = document.getElementById('svg-nonce-text-' + id);
                    const rect = document.getElementById('svg-nonce-rect-' + id);
                    const circle = document.getElementById('svg-circle-' + id);

                    if (txt) {
                        txt.textContent = 'N: ' + currentNonces[id].toLocaleString('vi-VN');
                        txt.setAttribute('fill', '#92400e');
                        txt.setAttribute('font-weight', 'bold');
                    }
                    if (rect) {
                        rect.setAttribute('fill', '#fef3c7');
                        rect.setAttribute('stroke', '#f59e0b');
                        rect.setAttribute('stroke-width', '1.5');
                    }
                    if (circle) {
                        circle.setAttribute('stroke', '#d97706');
                        circle.setAttribute('stroke-width', '3.5');
                    }
                });
            }, 40);
        }

        function stopSvgNonceTicker() {
            if (nonceIntervalId) {
                clearInterval(nonceIntervalId);
                nonceIntervalId = null;
            }
        }

        function clearWinnerHighlights() {
            const banner = document.getElementById('svg-winner-trophy-banner');
            if (banner) banner.remove();

            nodeIds.forEach(id => {
                const node = nodePositions[id];
                const circle = document.getElementById('svg-circle-' + id);
                if (circle && node) {
                    circle.setAttribute('r', '15.5');
                    circle.setAttribute('stroke', node.color);
                    circle.setAttribute('stroke-width', '2.5');
                    circle.setAttribute('fill', '#ffffff');
                }
                const rect = document.getElementById('svg-nonce-rect-' + id);
                const text = document.getElementById('svg-nonce-text-' + id);
                if (rect) {
                    rect.setAttribute('fill', '#f8fafc');
                    rect.setAttribute('stroke', '#cbd5e1');
                    rect.setAttribute('stroke-width', '1');
                }
                if (text) {
                    text.setAttribute('fill', '#475569');
                }
            });
        }

        // LÀM NỔI BẬT MÁY TÌM ĐƯỢC HASH TRÊN SƠ ĐỒ SVG (DỰA TRÊN 5 GIÂY BƯỚC 3)
        function highlightWinnerNodeOnSvg(winnerId, winnerNonce) {
            stopSvgNonceTicker();
            clearWinnerHighlights();

            const pos = nodePositions[winnerId];
            if (!pos) return;

            // 1. Phóng to và tạo viền phát sáng xanh đậm cho vòng tròn máy
            const circle = document.getElementById('svg-circle-' + winnerId);
            if (circle) {
                circle.setAttribute('r', '23');
                circle.setAttribute('stroke', '#2563eb');
                circle.setAttribute('stroke-width', '5');
                circle.setAttribute('fill', '#eff6ff');
            }

            // 2. Cố định số Nonce tìm được lên huy hiệu
            const winTxt = document.getElementById('svg-nonce-text-' + winnerId);
            const winRect = document.getElementById('svg-nonce-rect-' + winnerId);
            if (winTxt) {
                winTxt.textContent = '★ ' + Number(winnerNonce || 0).toLocaleString('vi-VN');
                winTxt.setAttribute('fill', '#1e40af');
                winTxt.setAttribute('font-weight', 'bold');
            }
            if (winRect) {
                winRect.setAttribute('fill', '#dbeafe');
                winRect.setAttribute('stroke', '#2563eb');
                winRect.setAttribute('stroke-width', '2');
            }

            // 3. Hiển thị Banner Nổi Bật: 🏆 ĐÃ TÌM ĐƯỢC HASH!
            const nodesLayer = document.getElementById('nodesSvgLayer');
            if (nodesLayer) {
                const bannerG = document.createElementNS('http://www.w3.org/2000/svg', 'g');
                bannerG.setAttribute('id', 'svg-winner-trophy-banner');
                
                let bY = pos.y < 270 ? pos.y - 66 : pos.y + 54;
                let bX = pos.x - 78;

                const bRect = document.createElementNS('http://www.w3.org/2000/svg', 'rect');
                bRect.setAttribute('x', bX);
                bRect.setAttribute('y', bY);
                bRect.setAttribute('width', '156');
                bRect.setAttribute('height', '23');
                bRect.setAttribute('rx', '5');
                bRect.setAttribute('fill', '#2563eb');
                bRect.setAttribute('stroke', '#93c5fd');
                bRect.setAttribute('stroke-width', '1.5');
                bRect.setAttribute('filter', 'url(#glow)');

                const bText = document.createElementNS('http://www.w3.org/2000/svg', 'text');
                bText.setAttribute('x', pos.x);
                bText.setAttribute('y', bY + 15.5);
                bText.setAttribute('text-anchor', 'middle');
                bText.setAttribute('fill', '#ffffff');
                bText.setAttribute('font-family', 'Plus Jakarta Sans, sans-serif');
                bText.setAttribute('font-size', '10');
                bText.setAttribute('font-weight', 'bold');
                bText.textContent = '🏆 ĐÃ TÌM ĐƯỢC HASH!';

                bannerG.appendChild(bRect);
                bannerG.appendChild(bText);
                nodesLayer.appendChild(bannerG);
            }

            // 4. Phát 6 đợt sóng xung kích liên tiếp kéo dài 5s
            const rippleLayer = document.getElementById('rippleLayer');
            if (rippleLayer) {
                for (let r = 0; r < 6; r++) {
                    setTimeout(() => {
                        const rip = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
                        rip.setAttribute('cx', pos.x);
                        rip.setAttribute('cy', pos.y);
                        rip.setAttribute('class', 'node-ripple');
                        rip.setAttribute('fill', 'none');
                        rip.setAttribute('stroke', '#2563eb');
                        rip.setAttribute('stroke-width', '3');
                        rippleLayer.appendChild(rip);
                        setTimeout(() => rip.remove(), 1600);
                    }, r * 750);
                }
            }

            // 5. Cập nhật thẻ máy tương ứng trong lưới bên dưới
            nodeIds.forEach(id => {
                const card = document.getElementById('card-' + id);
                const status = document.getElementById('status-' + id);
                const winnerTag = document.getElementById('winner-tag-' + id);
                if (card) card.classList.remove('computing-pulse');

                if (id === winnerId) {
                    if (card) card.classList.add('node-valid-hash');
                    if (status) {
                        status.className = 'text-[9px] font-mono font-bold text-blue-700 bg-blue-50 border border-blue-300 px-1.5 py-0.5 rounded-full';
                        status.textContent = 'ĐÃ TÌM ĐƯỢC HASH';
                    }
                    if (winnerTag) winnerTag.classList.remove('hidden');
                }
            });
        }

        // =========================================================================
        // TRUYỀN DỮ LIỆU & HOẠT ẢNH TRÊN 45 ĐƯỜNG LIÊN KẾT TCP (HẠT PHOTON & VỆT SAO BĂNG)
        // =========================================================================
        function clearSvgSignals() {
            const dynamicLayer = document.getElementById('dynamicGossipLayer');
            if (dynamicLayer) dynamicLayer.innerHTML = '';
            const rippleLayer = document.getElementById('rippleLayer');
            if (rippleLayer) rippleLayer.innerHTML = '';
        }

        // Hiệu ứng chớp sáng viền Node khi nhận hoặc phát tín hiệu
        function flashNodeCircle(nodeId, color = '#38bdf8') {
            const circle = document.getElementById('svg-circle-' + nodeId);
            if (!circle) return;
            const originalStroke = circle.getAttribute('stroke');
            circle.setAttribute('stroke', color);
            circle.setAttribute('stroke-width', '4');
            circle.setAttribute('filter', 'url(#glow)');
            setTimeout(() => {
                circle.setAttribute('stroke', originalStroke || '#cbd5e1');
                circle.setAttribute('stroke-width', '2.5');
                circle.removeAttribute('filter');
            }, 400);
        }

        // Vòng sóng xung kích kép mượt mà (60fps requestAnimationFrame)
        function triggerRipple(x, y, color = '#2563eb', maxR = 42) {
            const rippleLayer = document.getElementById('rippleLayer');
            if (!rippleLayer) return;

            // 1. Sóng ngoài (Outer shockwave ring)
            const rip1 = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
            rip1.setAttribute('cx', x);
            rip1.setAttribute('cy', y);
            rip1.setAttribute('fill', 'none');
            rip1.setAttribute('stroke', color);
            rip1.setAttribute('filter', 'url(#glow)');
            rippleLayer.appendChild(rip1);

            // 2. Hào quang trong (Inner luminous core halo)
            const rip2 = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
            rip2.setAttribute('cx', x);
            rip2.setAttribute('cy', y);
            rip2.setAttribute('fill', color);
            rip2.setAttribute('fill-opacity', '0.22');
            rip2.setAttribute('stroke', color);
            rippleLayer.appendChild(rip2);

            const startTime = performance.now();
            const duration = 700;

            function anim(now) {
                const elapsed = now - startTime;
                const p = Math.min(elapsed / duration, 1);
                // Đường cong Ease-Out Cubic mượt mà
                const ease = 1 - Math.pow(1 - p, 3);

                const r1 = 15.5 + ease * (maxR - 15.5);
                const op1 = (1 - p) * 0.95;
                const sw1 = Math.max(0.6, 3.2 * (1 - p));
                rip1.setAttribute('r', r1);
                rip1.setAttribute('stroke-opacity', op1);
                rip1.setAttribute('stroke-width', sw1);

                const r2 = 15.5 + (ease * 0.55) * (maxR - 15.5);
                const op2 = Math.max(0, (1 - p * 1.4) * 0.28);
                rip2.setAttribute('r', r2);
                rip2.setAttribute('fill-opacity', op2);
                rip2.setAttribute('stroke-opacity', op2 * 2);

                if (p < 1) {
                    requestAnimationFrame(anim);
                } else {
                    rip1.remove();
                    rip2.remove();
                }
            }
            requestAnimationFrame(anim);
        }

        function updateNodeMempoolUi(nodeId, countText) {
            const peerTxt = document.getElementById('svg-nonce-text-' + nodeId);
            const peerRect = document.getElementById('svg-nonce-rect-' + nodeId);
            if (peerTxt) {
                peerTxt.textContent = 'Mempool: ' + countText;
                peerTxt.setAttribute('fill', '#0284c7');
                peerTxt.setAttribute('font-weight', 'bold');
            }
            if (peerRect) {
                peerRect.setAttribute('fill', '#e0f2fe');
                peerRect.setAttribute('stroke', '#38bdf8');
                peerRect.setAttribute('stroke-width', '1.5');
            }
            const memBadge = document.getElementById('mem-' + nodeId);
            if (memBadge) {
                memBadge.textContent = '3 phiếu';
                memBadge.className = 'text-blue-700 font-bold';
            }
        }

        // BẮN 1 GÓI TIN DẠNG VỆT SAO BĂNG (PHOTON COMET) DỌC THEO ĐƯỜNG TRUYỀN
        function animateSingleLink(pA, pB, color = '#0284c7', duration = 650, onComplete = null) {
            const dynamicLayer = document.getElementById('dynamicGossipLayer');
            if (!dynamicLayer || !pA || !pB) return;

            const dx = pB.x - pA.x;
            const dy = pB.y - pA.y;
            const dist = Math.sqrt(dx * dx + dy * dy);
            if (dist < 1) {
                if (onComplete) onComplete();
                return;
            }
            const ux = dx / dist;
            const uy = dy / dist;

            // 1. Tia laser nền chạy dọc theo đường nối (nhạt dần khi hạt đến đích)
            const beamLine = document.createElementNS('http://www.w3.org/2000/svg', 'line');
            beamLine.setAttribute('x1', pA.x);
            beamLine.setAttribute('y1', pA.y);
            beamLine.setAttribute('x2', pB.x);
            beamLine.setAttribute('y2', pB.y);
            beamLine.setAttribute('stroke', color);
            beamLine.setAttribute('stroke-width', '2');
            beamLine.setAttribute('stroke-dasharray', '5, 5');
            beamLine.setAttribute('stroke-opacity', '0.65');
            beamLine.setAttribute('filter', 'url(#glow)');
            dynamicLayer.appendChild(beamLine);

            // 2. Nhóm hạt Photon & Vệt sao băng (Comet)
            const cometG = document.createElementNS('http://www.w3.org/2000/svg', 'g');

            // Vệt đuôi sao băng (Trailing tail)
            const tailLine = document.createElementNS('http://www.w3.org/2000/svg', 'line');
            tailLine.setAttribute('stroke', color);
            tailLine.setAttribute('stroke-width', '3.5');
            tailLine.setAttribute('stroke-linecap', 'round');
            tailLine.setAttribute('stroke-opacity', '0.85');
            tailLine.setAttribute('filter', 'url(#glow)');

            // Hào quang phát sáng (Luminous aura)
            const aura = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
            aura.setAttribute('r', '7.5');
            aura.setAttribute('fill', color);
            aura.setAttribute('fill-opacity', '0.38');
            aura.setAttribute('filter', 'url(#superGlow)');

            // Lõi photon năng lượng (Energy core)
            const core = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
            core.setAttribute('r', '4');
            core.setAttribute('fill', color);

            // Tâm sáng trắng cực đại (Hot center spark)
            const spark = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
            spark.setAttribute('r', '2');
            spark.setAttribute('fill', '#ffffff');

            cometG.appendChild(tailLine);
            cometG.appendChild(aura);
            cometG.appendChild(core);
            cometG.appendChild(spark);
            dynamicLayer.appendChild(cometG);

            const maxTailLen = Math.min(38, dist * 0.4);
            const startTime = performance.now();

            function step(now) {
                const elapsed = now - startTime;
                const p = Math.min(elapsed / duration, 1);
                // Đường cong Ease-Out Quad tự nhiên
                const t = p * (2 - p);

                const hx = pA.x + dx * t;
                const hy = pA.y + dy * t;

                // Chiều dài đuôi co giãn nhịp nhàng: ngắn khi xuất phát -> dài nhất ở giữa -> thu gọn khi chạm đích
                const currentTailLen = maxTailLen * Math.min(p * 2.8, (1 - p) * 2.8, 1);
                const tx = hx - ux * currentTailLen;
                const ty = hy - uy * currentTailLen;

                tailLine.setAttribute('x1', tx);
                tailLine.setAttribute('y1', ty);
                tailLine.setAttribute('x2', hx);
                tailLine.setAttribute('y2', hy);

                aura.setAttribute('cx', hx);
                aura.setAttribute('cy', hy);
                core.setAttribute('cx', hx);
                core.setAttribute('cy', hy);
                spark.setAttribute('cx', hx);
                spark.setAttribute('cy', hy);

                beamLine.setAttribute('stroke-opacity', Math.max(0, (1 - p) * 0.75));

                if (p < 1) {
                    requestAnimationFrame(step);
                } else {
                    cometG.remove();
                    beamLine.remove();
                    if (onComplete) onComplete();
                }
            }
            requestAnimationFrame(step);
        }

        // HOẠT ẢNH BƯỚC 1: CỬ TRI GỬI PHIẾU -> GATEWAY (:8001) -> LAN TRUYỀN GOSSIP THEO VÒNG TRÒN VÀ CHÉO MESH
        function animateTxGossip(gatewayId = 'Node-1', onDone = null) {
            clearSvgSignals();
            const gateway = nodePositions[gatewayId] || nodePositions['Node-1'];
            if (!gateway) return;

            // Chặng 0: Phiếu bầu từ cổng cử tri bay thẳng vào nút Gateway Node 1
            const voterSource = { x: gateway.x, y: Math.max(10, gateway.y - 65) };
            animateSingleLink(voterSource, gateway, '#00b4d8', 500, () => {
                triggerRipple(gateway.x, gateway.y, '#00b4d8', 46);
                flashNodeCircle(gatewayId, '#00b4d8');
                updateNodeMempoolUi(gatewayId, '+3');

                // Lan truyền Gossip đối xứng sang 2 nhánh vòng tròn Decagon
                const leftBranch = ['Node-2', 'Node-3', 'Node-4', 'Node-5', 'Node-6'];
                const rightBranch = ['Node-10', 'Node-9', 'Node-8', 'Node-7', 'Node-6'];

                function gossipHop(hopIdx) {
                    if (hopIdx >= leftBranch.length) {
                        if (onDone) onDone();
                        return;
                    }

                    const prevL = hopIdx === 0 ? gatewayId : leftBranch[hopIdx - 1];
                    const currL = leftBranch[hopIdx];
                    const prevR = hopIdx === 0 ? gatewayId : rightBranch[hopIdx - 1];
                    const currR = rightBranch[hopIdx];

                    const pPrevL = nodePositions[prevL];
                    const pCurrL = nodePositions[currL];
                    const pPrevR = nodePositions[prevR];
                    const pCurrR = nodePositions[currR];

                    if (pPrevL && pCurrL) {
                        animateSingleLink(pPrevL, pCurrL, '#00b4d8', 420, () => {
                            triggerRipple(pCurrL.x, pCurrL.y, '#00b4d8', 38);
                            flashNodeCircle(currL, '#00b4d8');
                            updateNodeMempoolUi(currL, '+3');
                        });
                    }

                    if (currR !== currL && pPrevR && pCurrR) {
                        animateSingleLink(pPrevR, pCurrR, '#00b4d8', 420, () => {
                            triggerRipple(pCurrR.x, pCurrR.y, '#00b4d8', 38);
                            flashNodeCircle(currR, '#00b4d8');
                            updateNodeMempoolUi(currR, '+3');
                        });
                    }

                    // Tại nhịp 2, bắn thêm 2 tia Gossip xuyên tâm tạo hiệu ứng mạng lưới đan xen chân thực
                    if (hopIdx === 1) {
                        const p2 = nodePositions['Node-2'];
                        const p9 = nodePositions['Node-9'];
                        if (p2 && p9) {
                            setTimeout(() => animateSingleLink(p2, p9, '#0284c7', 480), 100);
                        }
                    }

                    setTimeout(() => gossipHop(hopIdx + 1), 340);
                }

                gossipHop(0);
            });
        }

        // HOẠT ẢNH BƯỚC 4: MÁY CHIẾN THẮNG BẮN 9 TIA SÁNG RADIAL SIÊU ĐẸP TỎA RA 9 MÁY TRẠM
        function animateBlockGossip(winnerNodeId, blockIndex, finalNonce, onDone = null) {
            clearSvgSignals();
            stopSvgNonceTicker();
            const source = nodePositions[winnerNodeId] || nodePositions['Node-1'];
            if (!source) return;

            // Sóng bùng nổ chiến thắng màu vàng kim rực rỡ
            triggerRipple(source.x, source.y, '#f59e0b', 54);
            flashNodeCircle(winnerNodeId, '#f59e0b');

            // Cập nhật nhãn Nonce cho máy chiến thắng
            if (finalNonce) {
                const winTxt = document.getElementById('svg-nonce-text-' + winnerNodeId);
                const winRect = document.getElementById('svg-nonce-rect-' + winnerNodeId);
                if (winTxt) {
                    winTxt.textContent = '★ ' + Number(finalNonce).toLocaleString('vi-VN');
                    winTxt.setAttribute('fill', '#b45309');
                    winTxt.setAttribute('font-weight', 'bold');
                }
                if (winRect) {
                    winRect.setAttribute('fill', '#fef3c7');
                    winRect.setAttribute('stroke', '#f59e0b');
                    winRect.setAttribute('stroke-width', '1.5');
                }
            }

            // 9 tia phát tán tỏa ra theo thứ tự khoảng cách từ máy chiến thắng
            const peers = nodeIds.filter(id => id !== winnerNodeId);
            peers.sort((a, b) => {
                const da = Math.hypot(nodePositions[a].x - source.x, nodePositions[a].y - source.y);
                const db = Math.hypot(nodePositions[b].x - source.x, nodePositions[b].y - source.y);
                return da - db;
            });

            peers.forEach((peerId, idx) => {
                const target = nodePositions[peerId];
                setTimeout(() => {
                    animateSingleLink(source, target, '#6366f1', 650, () => {
                        // Sóng đồng thuận màu xanh lục ngọc bảo tại máy nhận khối
                        triggerRipple(target.x, target.y, '#10b981', 40);
                        flashNodeCircle(peerId, '#10b981');

                        const peerTxt = document.getElementById('svg-nonce-text-' + peerId);
                        const peerRect = document.getElementById('svg-nonce-rect-' + peerId);
                        if (peerTxt && blockIndex !== undefined) {
                            peerTxt.textContent = 'Khối #' + blockIndex + ' OK';
                            peerTxt.setAttribute('fill', '#047857');
                            peerTxt.setAttribute('font-weight', 'bold');
                        }
                        if (peerRect && blockIndex !== undefined) {
                            peerRect.setAttribute('fill', '#d1fae5');
                            peerRect.setAttribute('stroke', '#10b981');
                            peerRect.setAttribute('stroke-width', '1.5');
                        }

                        // Nháy viền thẻ máy bên dưới
                        const card = document.getElementById('card-' + peerId);
                        if (card) {
                            card.classList.add('border-emerald-500');
                            setTimeout(() => card.classList.remove('border-emerald-500'), 800);
                        }
                    });
                }, idx * 50);
            });

            if (onDone) {
                setTimeout(onDone, 700 + peers.length * 50);
            }
        }

        const triggerFullMeshBroadcast = animateBlockGossip;
        const triggerGossipBroadcast = animateBlockGossip;

        // KIỂM TRA ĐỒNG THỜI TOÀN BỘ 45 DÂY TCP MESH (HIỆU ỨNG SÓNG QUÉT MẠNG ĐẲNG CẤP)
        function testAll45TcpLines() {
            addTermLog('⚡ <strong class="text-cyan-400">[TEST TOÀN BỘ 45 DÂY TCP]</strong> Kích hoạt kiểm tra xung truyền tín hiệu đồng thời trên 45 liên kết Full-Mesh...', 'text-cyan-300 font-bold');
            
            // Pha 1: 10 Vòng liên kết chu vi ngoài chạy trước tạo khung sóng bao quanh (0ms)
            for (let i = 0; i < nodeIds.length; i++) {
                const next = (i + 1) % nodeIds.length;
                const p1 = nodePositions[nodeIds[i]];
                const p2 = nodePositions[nodeIds[next]];
                setTimeout(() => {
                    animateSingleLink(p1, p2, '#06b6d4', 550);
                }, i * 35);
            }

            // Pha 2: Các đường chéo xuyên tâm Full-Mesh bắn xung so le (200ms)
            const diagonalLinks = all45Links.filter((_, idx) => idx % 2 === 0);
            diagonalLinks.forEach((link, idx) => {
                setTimeout(() => {
                    animateSingleLink(link.pA, link.pB, '#38bdf8', 650);
                }, 200 + (idx % 6) * 50);
            });

            // Pha 3: 10 Đỉnh máy đồng loạt phản hồi sóng xung kích xanh ngọc (650ms)
            nodeIds.forEach((id, idx) => {
                setTimeout(() => {
                    const pos = nodePositions[id];
                    if (pos) {
                        triggerRipple(pos.x, pos.y, '#06b6d4', 42);
                        flashNodeCircle(id, '#06b6d4');
                    }
                }, 600 + idx * 30);
            });

            setTimeout(() => {
                addTermLog('✅ <strong class="text-emerald-400">[KẾT QUẢ TEST TCP]</strong> 45/45 Kênh truyền P2P Mesh phản hồi trễ &lt; 0.3ms. Toàn bộ 45 dây TCP hoạt động hoàn hảo!', 'text-emerald-400 font-bold');
            }, 1100);
        }

        function updateContinuousUI(isRunning) {
            isContinuousActive = isRunning;
            const btnStart = document.getElementById('btnContStart');
            const btnStop = document.getElementById('btnContStop');
            const contDot = document.getElementById('contDot');
            const contText = document.getElementById('contText');
            const btnRace = document.getElementById('btnRace');
            const btnSlowMo = document.getElementById('btnSlowMo');

            if (isRunning) {
                if (btnStart) {
                    btnStart.disabled = true;
                    btnStart.classList.add('opacity-40', 'cursor-not-allowed');
                }
                if (btnStop) {
                    btnStop.disabled = false;
                    btnStop.classList.remove('opacity-50', 'cursor-not-allowed', 'bg-rose-50', 'text-rose-400');
                    btnStop.classList.add('bg-rose-600', 'text-white', 'shadow-xs', 'animate-pulse');
                }
                if (contDot) contDot.className = 'w-2 h-2 rounded-full bg-emerald-500 animate-ping';
                if (contText) contText.innerHTML = '<span class="text-emerald-600 font-bold">ĐANG CHẠY TỰ ĐỘNG</span>';
                if (btnRace) {
                    btnRace.disabled = true;
                    btnRace.style.opacity = '0.5';
                    btnRace.style.pointerEvents = 'none';
                }
                if (btnSlowMo) {
                    btnSlowMo.disabled = true;
                    btnSlowMo.classList.add('opacity-50', 'pointer-events-none');
                }
                startSvgNonceTicker();
                highlightStep(2);
            } else {
                if (btnStart) {
                    btnStart.disabled = false;
                    btnStart.classList.remove('opacity-40', 'cursor-not-allowed');
                }
                if (btnStop) {
                    btnStop.disabled = true;
                    btnStop.classList.add('opacity-50', 'cursor-not-allowed', 'bg-rose-50', 'text-rose-400');
                    btnStop.classList.remove('bg-rose-600', 'text-white', 'shadow-xs', 'animate-pulse');
                }
                if (contDot) contDot.className = 'w-2 h-2 rounded-full bg-slate-400';
                if (contText) contText.textContent = 'Chế độ liên tục: ĐÃ TẮT';
                if (btnRace && !isMiningActive) {
                    btnRace.disabled = false;
                    btnRace.style.opacity = '1';
                    btnRace.style.pointerEvents = 'auto';
                }
                if (btnSlowMo) {
                    btnSlowMo.disabled = false;
                    btnSlowMo.classList.remove('opacity-50', 'pointer-events-none');
                }
                stopSvgNonceTicker();
            }
        }

        // 1. KÍCH HOẠT CHẾ ĐỘ CHẠY TỰ ĐỘNG
        async function startContinuousMining() {
            try {
                const res = await fetch('/api/cluster/continuous/start', { method: 'POST' });
                const data = await res.json();
                if (data.success) {
                    addTermLog('🚀 <strong class="text-emerald-400">[BẮT ĐẦU CHẠY TỰ ĐỘNG]</strong> 10 máy trạm tự động tìm Nonce SHA-256 liên tục!', 'text-emerald-300 font-bold');
                    updateContinuousUI(true);
                    fetchClusterState();
                } else {
                    alert(data.error || 'Không thể bật chạy tự động');
                }
            } catch (err) {
                alert('Lỗi kết nối: ' + err.message);
            }
        }

        // 2. DỪNG CHẾ ĐỘ CHẠY TỰ ĐỘNG
        async function stopContinuousMining() {
            try {
                const res = await fetch('/api/cluster/continuous/stop', { method: 'POST' });
                const data = await res.json();
                if (data.success) {
                    addTermLog('⏹️ <strong class="text-rose-400">[DỪNG CHẠY TỰ ĐỘNG]</strong> Đã phát lệnh dừng tới 10 máy.', 'text-rose-300 font-bold');
                    updateContinuousUI(false);
                    setMiningUiState(false);
                    nodeIds.forEach(id => {
                        const card = document.getElementById('card-' + id);
                        const status = document.getElementById('status-' + id);
                        if (card) card.classList.remove('computing-pulse', 'node-valid-hash');
                        if (status && (status.textContent.includes('ĐANG CHẠY') || status.textContent.includes('MINING'))) {
                            status.className = 'text-[9px] font-mono font-bold text-emerald-700 bg-emerald-50 border border-emerald-200 px-1.5 py-0.5 rounded-full';
                            status.textContent = 'ONLINE';
                        }
                    });
                    fetchClusterState();
                } else {
                    alert(data.error || 'Không thể dừng chạy tự động');
                }
            } catch (err) {
                alert('Lỗi kết nối: ' + err.message);
            }
        }

        // 3. ĐÓNG GÓI 1 KHỐI (CUỘC TRANH TÀI 10 MÁY)
        async function startPowRace() {
            if (isMiningActive || isContinuousActive || isSlowMoRunning) return;

            const btn = document.getElementById('btnRace');
            setMiningUiState(true);
            btn.innerHTML = '<span class="inline-block animate-spin mr-1">⚙️</span> <span>CẢ 10 MÁY ĐANG TRANH TÀI BĂM HASH...</span>';

            // Bước 2: Bắt đầu băm
            highlightStep(2);
            startSvgNonceTicker();

            nodeIds.forEach(id => {
                const card = document.getElementById('card-' + id);
                const status = document.getElementById('status-' + id);
                const winnerTag = document.getElementById('winner-tag-' + id);
                if (card) {
                    card.classList.remove('node-valid-hash');
                    card.classList.add('computing-pulse');
                }
                if (status) {
                    status.className = 'text-[9px] font-mono font-bold text-amber-700 bg-amber-50 border border-amber-300 px-1.5 py-0.5 rounded-full';
                    status.textContent = 'ĐANG CHẠY';
                }
                if (winnerTag) winnerTag.classList.add('hidden');
            });

            addTermLog('🏁 <strong class="text-amber-400">[TRANH TÀI POW 10 MÁY]</strong> 10 Luồng Virtual Threads cùng giải bài toán SHA-256 tìm 6 số 0...', 'text-amber-300 font-bold');

            try {
                const startTime = Date.now();
                const res = await fetch('/api/cluster/mine?race=true', { method: 'POST' });
                const data = await res.json();

                // Đảm bảo chạy tối thiểu 1.5 giây để người dùng quan sát được các số nonce đang băm
                const elapsed = Date.now() - startTime;
                if (elapsed < 1500) {
                    await new Promise(r => setTimeout(r, 1500 - elapsed));
                }

                if (data.success) {
                    const winnerId = data.winnerId || 'Node-1';
                    stopSvgNonceTicker();
                    clearSvgSignals();

                    // Bước 3: Tìm ra hash và làm nổi bật trên SVG (dừng lại 1.5s để quan sát)
                    highlightStep(3, { winnerName: data.winnerName, winnerId: winnerId, nonce: data.nonce });
                    highlightWinnerNodeOnSvg(winnerId, data.nonce);

                    addTermLog('⚡ <strong class="text-blue-400">[TÌM THẤY HASH HỢP LỆ]</strong> ' + data.winnerName + ' (' + winnerId + ' - Port ' + data.winnerPort + ') ĐÃ TÌM ĐƯỢC HASH HỢP LỆ!', 'text-blue-300 font-bold');
                    addTermLog('🔒 [THÔNG SỐ KHỐI] Nonce: <span class="text-white font-bold">' + data.nonce + '</span> | Hash: <span class="text-emerald-400 font-mono">' + data.hash + '</span> | Thời gian: <span class="text-sky-300">' + data.durationMs + ' ms</span>', 'text-slate-300');

                    await new Promise(r => setTimeout(r, 1500));

                    // Bước 4: Lan truyền 9 tia từ máy chiến thắng tới 9 máy còn lại (1.5s)
                    highlightStep(4);
                    addTermLog('📡 [TCP GOSSIP BROADCAST] ' + winnerId + ' phát tán khối #' + data.blockIndex + ' qua các kênh TCP tới 9 máy trạm...', 'text-indigo-300');
                    animateBlockGossip(winnerId, data.blockIndex, data.nonce);

                    await new Promise(r => setTimeout(r, 1500));

                    // Bước 5: Đồng thuận Nakamoto (1.5s)
                    highlightStep(5);
                    addTermLog('✅ [NAKAMOTO CONSENSUS] 10 máy xác thực hợp lệ và cập nhật Sổ Cái lên Khối #' + data.blockIndex + '!', 'text-emerald-400 font-bold');

                    nodeIds.forEach(id => {
                        const status = document.getElementById('status-' + id);
                        const card = document.getElementById('card-' + id);
                        if (card) card.classList.remove('computing-pulse');
                        if (status) {
                            status.className = 'text-[9px] font-mono font-bold text-slate-700 bg-slate-100 border border-slate-300 px-1.5 py-0.5 rounded-full';
                            status.textContent = 'ĐÃ ĐỒNG BỘ';
                        }
                    });

                    lastRenderedBlockIndex = data.blockIndex;
                    await new Promise(r => setTimeout(r, 1200));

                    fetchClusterState();
                } else if (!data.cancelled) {
                    addTermLog('⚠️ [LỖI TÍNH TOÁN POW] ' + (data.error || 'Không thể đóng khối'), 'text-rose-400');
                    alert(data.error || 'Có lỗi xảy ra khi tính toán PoW!');
                }
            } catch (err) {
                addTermLog('❌ [LỖI MẠNG] ' + err.message, 'text-rose-400');
            } finally {
                setMiningUiState(false);
                btn.innerHTML = '<span>Đóng Gói 1 Khối</span>';
            }
        }

        // 4. CHỈ ĐỊNH 1 MÁY ĐỘC LẬP TÍNH TOÁN GIẢI POW
        async function mineSingleNode(nodeId) {
            if (isMiningActive || isContinuousActive || isSlowMoRunning) return;
            const node = nodePositions[nodeId];
            if (!node) return;

            setMiningUiState(true);
            addTermLog('⚡ <strong class="text-amber-400">[CHỈ ĐỊNH KHAI THÁC]</strong> ' + node.name + ' (' + nodeId + ' : ' + node.port + ') được chỉ định độc lập giải thuật toán SHA-256...', 'text-amber-300 font-bold');

            highlightStep(2);
            startSvgNonceTicker();

            const card = document.getElementById('card-' + nodeId);
            const status = document.getElementById('status-' + nodeId);
            if (card) {
                card.classList.remove('node-valid-hash');
                card.classList.add('computing-pulse');
            }
            if (status) {
                status.className = 'text-[9px] font-mono font-bold text-amber-700 bg-amber-50 border border-amber-300 px-1.5 py-0.5 rounded-full';
                status.textContent = 'ĐANG GIẢI POW';
            }

            try {
                const startTime = Date.now();
                const res = await fetch('/api/cluster/mine?nodeId=' + encodeURIComponent(nodeId), { method: 'POST' });
                const data = await res.json();

                const elapsed = Date.now() - startTime;
                if (elapsed < 1200) {
                    await new Promise(r => setTimeout(r, 1200 - elapsed));
                }

                if (data.success) {
                    stopSvgNonceTicker();
                    clearSvgSignals();

                    highlightStep(3, { winnerName: data.winnerName, winnerId: nodeId, nonce: data.nonce });
                    highlightWinnerNodeOnSvg(nodeId, data.nonce);

                    addTermLog('🏆 <strong class="text-blue-400">[HOÀN TẤT BĂM]</strong> ' + node.name + ' đã tìm thấy Hash hợp lệ [Nonce: ' + data.nonce + ']!', 'text-blue-300 font-bold');
                    await new Promise(r => setTimeout(r, 1500));

                    highlightStep(4);
                    addTermLog('📡 [TCP GOSSIP] ' + nodeId + ' phát tán khối #' + data.blockIndex + ' tới 9 máy còn lại qua TCP Sockets...', 'text-indigo-300');
                    animateBlockGossip(nodeId, data.blockIndex, data.nonce);
                    await new Promise(r => setTimeout(r, 1500));

                    highlightStep(5);
                    addTermLog('✅ [ĐỒNG THUẬN] 10 máy đã xác thực thành công Khối #' + data.blockIndex + '!', 'text-emerald-400 font-bold');
                    nodeIds.forEach(id => {
                        const st = document.getElementById('status-' + id);
                        const c = document.getElementById('card-' + id);
                        if (c) c.classList.remove('computing-pulse');
                        if (st) {
                            st.className = 'text-[9px] font-mono font-bold text-slate-700 bg-slate-100 border border-slate-300 px-1.5 py-0.5 rounded-full';
                            st.textContent = 'ĐÃ ĐỒNG BỘ';
                        }
                    });
                    lastRenderedBlockIndex = data.blockIndex;
                    await new Promise(r => setTimeout(r, 1000));
                    fetchClusterState();
                } else if (!data.cancelled) {
                    addTermLog('⚠️ Lỗi: ' + (data.error || 'Không thể đóng khối'), 'text-rose-400');
                }
            } catch (err) {
                addTermLog('❌ Lỗi mạng: ' + err.message, 'text-rose-400');
            } finally {
                setMiningUiState(false);
            }
        }

        // 5. ĐỒNG BỘ TRẠNG THÁI 10 MÁY LIÊN TỤC REAL-TIME (500MS)
        async function fetchClusterState() {
            // Khi đang chạy mô phỏng chậm hoặc đang đóng gói 1 khối, không cho polling can thiệp làm đè giao diện các bước!
            if (isSlowMoRunning) return;

            try {
                const res = await fetch('/api/election/status');
                const data = await res.json();

                const memCount = data.mempool ? data.mempool.length : 0;
                const memBadge = document.getElementById('mempoolBadge');
                if (memBadge) memBadge.textContent = memCount + ' phiếu';

                // Tự động kích hoạt hiệu ứng lan truyền P2P khi có phiếu bầu mới từ Cổng Cử Tri (/vote)
                if (memCount > lastKnownMempoolCount && lastKnownMempoolCount > 0 && !isMiningActive && !isSlowMoRunning) {
                    highlightStep(1);
                    animateTxGossip('Node-1');
                    addTermLog('🗳️ <strong class="text-sky-400">[P2P TX GOSSIP]</strong> Nhận ' + (memCount - lastKnownMempoolCount) + ' phiếu mới từ Cổng Cử Tri -> Đang phát tán qua 10 Nodes!', 'text-sky-300 font-bold');
                }
                lastKnownMempoolCount = memCount;

                if (typeof data.isContinuousMining !== 'undefined') {
                    updateContinuousUI(data.isContinuousMining);
                }

                // Cập nhật Banner Khối Mới Nhất
                if (data.blocks && data.blocks.length > 0) {
                    const latestBlock = data.blocks[data.blocks.length - 1];
                    const bTitle = document.getElementById('latestBlockTitle');
                    const bContent = document.getElementById('latestBlockContent');
                    const bHash = document.getElementById('latestBlockHash');
                    const bNonce = document.getElementById('latestBlockNonce');
                    const bBanner = document.getElementById('latestBlockBanner');

                    if (bTitle) bTitle.textContent = 'Khối #' + latestBlock.index;
                    if (bContent) bContent.textContent = latestBlock.content || 'Khối rỗng (0 phiếu bầu)';
                    if (bHash) bHash.textContent = latestBlock.hash;
                    if (bNonce) bNonce.textContent = latestBlock.nonce;

                    // Nếu có khối mới xuất hiện từ chế độ chạy tự động (liên tục)
                    if (lastRenderedBlockIndex !== -1 && latestBlock.index > lastRenderedBlockIndex && !isMiningActive && !isSlowMoRunning) {
                        if (bBanner) {
                            bBanner.classList.add('border-blue-500', 'bg-blue-50/50', 'scale-[1.01]');
                            setTimeout(() => {
                                bBanner.classList.remove('border-blue-500', 'bg-blue-50/50', 'scale-[1.01]');
                            }, 1800);
                        }
                        
                        let detectedWinner = data.latestWinnerId || 'Node-1';
                        highlightStep(4);
                        animateBlockGossip(detectedWinner, latestBlock.index, latestBlock.nonce);
                        setTimeout(() => highlightStep(5), 1000);
                    }
                    lastRenderedBlockIndex = latestBlock.index;
                }

                if (data.cluster && data.cluster.length > 0) {
                    const badge = document.getElementById('nodeTotalBadge');
                    if (badge) badge.textContent = data.cluster.length + ' Nodes Active';

                    const grid = document.getElementById('nodesGrid');
                    if (grid && data.cluster.length > grid.children.length) {
                        for (let i = grid.children.length; i < data.cluster.length; i++) {
                            const node = data.cluster[i];
                            const card = document.createElement('div');
                            card.id = 'card-' + node.id;
                            card.className = 'node-card bg-white border border-blue-200 rounded-2xl p-3 flex flex-col justify-between space-y-2 shadow-xs cursor-pointer hover:border-blue-400 transition';
                            card.setAttribute('title', 'Nhấp để ' + node.name + ' (:' + node.port + ') độc lập giải PoW');
                            card.onclick = () => mineSingleNode(node.id);
                            card.innerHTML = `
                                <div class="flex items-start justify-between">
                                    <div>
                                        <span class="text-[9px] font-mono font-bold text-blue-700 bg-blue-50 border border-blue-200 px-1.5 py-0.5 rounded">PORT ${node.port}</span>
                                        <h3 class="text-xs font-bold text-slate-900 mt-1">${node.name}</h3>
                                        <p class="text-[9px] text-slate-400 font-medium">${node.role}</p>
                                    </div>
                                    <span id="status-${node.id}" class="text-[9px] font-mono font-bold text-emerald-700 bg-emerald-50 border border-emerald-200 px-1.5 py-0.5 rounded-full">${node.status || 'ONLINE'}</span>
                                </div>
                                <div class="space-y-1 bg-slate-50 border border-slate-200/70 rounded-xl p-2 text-[10.5px] font-mono">
                                    <div class="flex justify-between text-slate-500">
                                        <span class="font-sans text-[10px]">Tốc độ:</span>
                                        <span class="text-slate-800 font-bold">${node.hashRate || '280 KH/s'}</span>
                                    </div>
                                    <div class="flex justify-between text-slate-500">
                                        <span class="font-sans text-[10px]">Khối đã tìm:</span>
                                        <span id="mined-${node.id}" class="text-slate-900 font-bold">${node.blocksMined || 0} blocks</span>
                                    </div>
                                    <div class="flex justify-between text-slate-500">
                                        <span class="font-sans text-[10px]">Mempool:</span>
                                        <span id="mem-${node.id}" class="text-blue-700 font-bold">${node.mempoolCount || 0} phiếu</span>
                                    </div>
                                    <div class="flex justify-between text-slate-500">
                                        <span class="font-sans text-[10px]">Chiều cao:</span>
                                        <span id="height-${node.id}" class="text-slate-800 font-bold">#${node.blockCount > 0 ? (node.blockCount - 1) : 0}</span>
                                    </div>
                                </div>
                                <div id="winner-tag-${node.id}" class="hidden text-center py-1 bg-blue-50 border border-blue-200 rounded-lg text-blue-700 text-[10px] font-mono font-bold">
                                    ĐÃ TÌM ĐƯỢC HASH
                                </div>
                                <div class="text-[9px] text-blue-600 font-medium text-center pt-1 border-t border-slate-100 flex items-center justify-center gap-1 font-sans">
                                    <span>⚡</span> <span>Bấm để khai thác độc lập</span>
                                </div>
                            `;
                            grid.appendChild(card);
                        }
                    }

                    data.cluster.forEach(node => {
                        const mEl = document.getElementById('mined-' + node.id);
                        const memEl = document.getElementById('mem-' + node.id);
                        const hEl = document.getElementById('height-' + node.id);
                        const card = document.getElementById('card-' + node.id);
                        const status = document.getElementById('status-' + node.id);

                        if (mEl) mEl.textContent = (node.blocksMined || 0) + ' blocks';
                        if (memEl) memEl.textContent = (node.mempoolCount || 0) + ' phiếu';
                        if (hEl) hEl.textContent = '#' + (node.blockCount > 0 ? (node.blockCount - 1) : 0);

                        if (data.isContinuousMining && (node.status === 'MINING' || node.status === 'COMPUTING')) {
                            if (card) card.classList.add('computing-pulse');
                            if (status) {
                                status.className = 'text-[9px] font-mono font-bold text-amber-700 bg-amber-50 border border-amber-300 px-1.5 py-0.5 rounded-full';
                                status.textContent = 'ĐANG CHẠY';
                            }
                        } else if (!data.isContinuousMining && !isMiningActive) {
                            if (card && !card.classList.contains('node-valid-hash')) {
                                card.classList.remove('computing-pulse');
                            }
                            if (status && (status.textContent.includes('ĐANG CHẠY') || status.textContent.includes('MINING'))) {
                                status.className = 'text-[9px] font-mono font-bold text-emerald-700 bg-emerald-50 border border-emerald-200 px-1.5 py-0.5 rounded-full';
                                status.textContent = 'ONLINE';
                            }
                        }
                    });
                }

                // Cập nhật log từ server nếu có sự kiện mới
                if (data.logs && data.logs.length > 0) {
                    data.logs.forEach(l => {
                        if (!seenLogs.has(l)) {
                            seenLogs.add(l);
                            let col = 'text-slate-300';
                            if (l.includes('WINNER') || l.includes('KẾT QUẢ') || l.includes('CHIẾN THẮNG') || l.includes('HASH')) col = 'text-blue-300 font-bold';
                            else if (l.includes('CHẠY TỰ ĐỘNG') || l.includes('CUỘC ĐUA') || l.includes('TRANH TÀI') || l.includes('SLOW-MO')) col = 'text-amber-300 font-bold';
                            else if (l.includes('TCP GOSSIP') || l.includes('P2P')) col = 'text-indigo-300';
                            else if (l.includes('ĐÃ ĐÓNG') || l.includes('đồng bộ')) col = 'text-emerald-400 font-bold';
                            else if (l.includes('LỖI') || l.includes('DỪNG')) col = 'text-rose-400 font-bold';
                            addTermLog(l, col);
                        }
                    });
                }
            } catch (ignored) {}
        }

        function openJoinNodeModal() {
            const m = document.getElementById('joinNodeModal');
            if (m) {
                m.classList.remove('hidden');
                document.getElementById('newNodeName').focus();
            }
        }

        function closeJoinNodeModal() {
            const m = document.getElementById('joinNodeModal');
            if (m) m.classList.add('hidden');
        }

        async function submitJoinNode() {
            const nameInput = document.getElementById('newNodeName');
            const roleInput = document.getElementById('newNodeRole');
            const name = nameInput.value.trim();
            const role = roleInput.value;
            const btn = document.getElementById('btnSubmitJoinNode');
            if (!name) {
                alert('Vui lòng nhập tên máy hoặc tên của bạn!');
                return;
            }
            btn.disabled = true;
            btn.textContent = 'Đang gia nhập mạng...';
            try {
                const res = await fetch('/api/cluster/join', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ name: name, role: role })
                });
                const data = await res.json();
                if (data.success) {
                    alert('🎉 ' + data.message + ' (Port ' + data.port + ', Hashrate ' + data.hashRate + ')');
                    closeJoinNodeModal();
                    nameInput.value = '';
                    fetchClusterState();
                } else {
                    alert('⚠️ Lỗi: ' + (data.error || 'Không thể gia nhập'));
                }
            } catch (err) {
                alert('❌ Lỗi kết nối: ' + err.message);
            } finally {
                btn.disabled = false;
                btn.textContent = 'Xác Nhận Tham Gia';
            }
        }

        // KHỞI TẠO HỆ THỐNG
        initSvgTopology();
        initNodeCardsGrid();
        highlightStep(1);

        addTermLog('⚡ [INIT] Cụm 10 Máy P2P PoW Engine đã sẵn sàng trên nền tảng Java 21 Virtual Threads.', 'text-sky-400 font-bold');
        addTermLog('📡 [TCP MESH] 10 Node Sockets liên kết Full-Mesh 45 liên kết: Port 8001 -> 8010.', 'text-slate-400');
        addTermLog('💡 Bấm [Mô Phỏng Chậm 5 Bước (Slow-Mo)] hoặc [Đóng Gói 1 Khối] để quan sát 2 sơ đồ đồng bộ thời gian thực!', 'text-emerald-300 font-bold');

        setInterval(fetchClusterState, 500);
        fetchClusterState();
    </script>
</body>
</html>
    """;

    public static String getHtml() {
        return new StringBuilder(PART_1).append(PART_2).append(PART_3).toString();
    }
}
