package com.pow.simulator.web;

/**
 * Giao diện Sổ Cái Chuỗi Khối Blockchain (Blockchain Ledger Explorer).
 * Chuẩn Etherscan & Solscan - 100% Kích thước các khối đồng bộ tuyệt đối.
 */
public final class DashboardHtmlTemplate {

    private DashboardHtmlTemplate() {}

    public static String getHtml() {
        return """
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sổ Cái Blockchain (Blockchain Ledger Explorer) | E-Voting</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&family=JetBrains+Mono:wght@400;500;600;700&display=swap');
        body { font-family: 'Plus Jakarta Sans', -apple-system, BlinkMacSystemFont, sans-serif; }
        .font-mono { font-family: 'JetBrains Mono', monospace; }
        ::-webkit-scrollbar { width: 5px; height: 5px; }
        ::-webkit-scrollbar-track { background: #f8fafc; }
        ::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 9999px; }
        ::-webkit-scrollbar-thumb:hover { background: #94a3b8; }
        .block-card {
            transition: all 0.2s cubic-bezier(0.16, 1, 0.3, 1);
        }
        .block-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.08);
        }
        .highlight-search {
            outline: 2px solid #6366f1;
            background-color: rgba(99, 102, 241, 0.05);
        }
    </style>
</head>
<body class="bg-[#f8fafc] text-slate-800 antialiased min-h-screen p-4 md:p-8 flex flex-col justify-between selection:bg-slate-900 selection:text-white">

    <div class="max-w-7xl mx-auto w-full space-y-4">

        <!-- 1. HEADER SỔ CÁI CHÍNH HIỆU CHUẨN ETHERSCAN -->
        <header class="bg-white border border-slate-200/90 rounded-2xl px-6 py-4 flex flex-col sm:flex-row items-center justify-between gap-4 shadow-2xs">
            <div class="flex items-center gap-3.5">
                <div class="w-10 h-10 rounded-xl bg-slate-900 text-white flex items-center justify-center text-lg shrink-0 font-bold shadow-xs">
                    ⛓️
                </div>
                <div>
                    <div class="flex items-center gap-2.5">
                        <h1 class="text-base font-black text-slate-900 tracking-tight uppercase">
                            SỔ CÁI CHUỖI KHỐI &bull; BLOCKCHAIN LEDGER
                        </h1>
                        <span class="text-[10px] font-mono bg-emerald-50 text-emerald-700 border border-emerald-200 px-2 py-0.5 rounded-full font-bold flex items-center gap-1">
                            <span class="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse"></span> TRỰC TUYẾN
                        </span>
                    </div>
                    <p class="text-[11px] text-slate-400 font-mono">Hệ thống sổ cái phân tán &bull; Bất biến &bull; Đồng thuận PoW SHA-256</p>
                </div>
            </div>

            <div class="flex items-center gap-4 text-xs font-mono text-slate-500">
                <div class="text-right hidden sm:block">
                    <div class="text-[10px] text-slate-400 uppercase font-semibold">Giao thức đồng thuận</div>
                    <div class="font-bold text-slate-800 text-[11px]">Proof-of-Work (7 Zeros)</div>
                </div>
                <div class="h-6 w-px bg-slate-200 hidden sm:block"></div>
                <div class="text-right">
                    <div class="text-[10px] text-slate-400 uppercase font-semibold">Tính toàn vẹn</div>
                    <div class="font-bold text-emerald-600 text-[11px]">100% Xác thực</div>
                </div>
            </div>
        </header>

        <!-- 2. BĂNG CHỈ SỐ VĨ MÔ (EXECUTIVE METRICS CARDS - 100% ĐỒNG BỘ CHIỀU CAO) -->
        <div class="grid grid-cols-2 lg:grid-cols-4 gap-3">
            <div class="bg-white border border-slate-200/90 rounded-xl p-3.5 shadow-2xs h-[88px] flex flex-col justify-between">
                <div class="text-[10px] font-mono uppercase font-bold text-slate-400 tracking-wider">Chiều Cao Khối (Height)</div>
                <div class="text-xl font-mono font-black text-slate-900 flex items-baseline gap-1">
                    <span id="statBlocks">0</span>
                    <span class="text-xs font-normal text-slate-400">blocks</span>
                </div>
            </div>

            <div class="bg-white border border-slate-200/90 rounded-xl p-3.5 shadow-2xs h-[88px] flex flex-col justify-between">
                <div class="text-[10px] font-mono uppercase font-bold text-slate-400 tracking-wider">Tổng Phiếu / Bản Ghi</div>
                <div class="text-xl font-mono font-black text-slate-900 flex items-baseline gap-1">
                    <span id="statTotalVotes">0</span>
                    <span class="text-xs font-normal text-slate-400">records</span>
                </div>
            </div>

            <div class="bg-white border border-slate-200/90 rounded-xl p-3.5 shadow-2xs h-[88px] flex flex-col justify-between">
                <div class="text-[10px] font-mono uppercase font-bold text-slate-400 tracking-wider">Mã Băm Khối Mới Nhất</div>
                <div id="statLatestHash" class="text-xs font-mono font-bold text-slate-700 truncate cursor-pointer hover:text-indigo-600 transition" onclick="copyLatestHash()">
                    0000000...
                </div>
            </div>

            <div class="bg-white border border-slate-200/90 rounded-xl p-3.5 shadow-2xs h-[88px] flex flex-col justify-between">
                <div class="text-[10px] font-mono uppercase font-bold text-slate-400 tracking-wider">Trạng Thái Sổ Cái</div>
                <div>
                    <span class="text-[11px] font-mono font-bold text-emerald-700 bg-emerald-50 border border-emerald-200 px-2.5 py-1 rounded-md inline-block">
                        BẢO MẬT BẤT BIẾN
                    </span>
                </div>
            </div>
        </div>

        <!-- 3. THANH TÌM KIẾM CHUẨN ETHERSCAN & SOLSCAN -->
        <div class="relative">
            <div class="absolute inset-y-0 left-0 pl-3.5 flex items-center pointer-events-none text-slate-400 text-xs">
                🔍
            </div>
            <input type="text" id="searchInput" oninput="handleSearch(this.value)"
                   placeholder="Tìm kiếm theo số hiệu Block (#0, #1...), mã băm Hash, Tx Hash (0x...), hoặc MSSV cử tri..."
                   class="w-full pl-9 pr-4 py-2.5 bg-white border border-slate-200/90 rounded-xl text-xs text-slate-800 placeholder-slate-400 focus:outline-none focus:border-slate-900 focus:ring-1 focus:ring-slate-900 shadow-2xs font-mono transition">
        </div>
 
        <!-- 4. SỔ CÁI CHUỖI KHỐI (100% CÁC KHỐI ĐỒNG BỘ KÍCH THƯỚC) -->
        <main class="bg-white border border-slate-200/90 rounded-2xl p-5 md:p-6 shadow-2xs space-y-4">
            <div class="flex items-center justify-between border-b border-slate-100 pb-3">
                <div class="flex items-center gap-2">
                    <span class="text-base text-slate-500 font-bold">🔗</span>
                    <h2 class="text-xs font-black text-slate-900 uppercase tracking-wider">
                        CHUỖI KHỐI SỔ CÁI (<span id="statBlocksStream">0</span> KHỐI ĐÃ KHÓA)
                    </h2>
                </div>
                <div class="flex items-center gap-2">
                    <span class="text-[10px] font-mono text-slate-400 hidden sm:inline">Nhấp vào khối để soi chi tiết</span>
                    <span class="text-[10px] font-mono text-emerald-700 bg-emerald-50 border border-emerald-200 px-2 py-0.5 rounded-full font-bold">
                        Đồng Bộ 100%
                    </span>
                </div>
            </div>

            <!-- CHUỖI KHỐI NỐI XÍCH (HORIZONTAL STREAM CÁC KHỐI KÍCH THƯỚC ĐỒNG NHẤT 240px x 316px) -->
            <div id="blockchainContainer" class="flex items-center gap-0 overflow-x-auto pb-4 pt-2">
                <!-- Render các khối bằng Javascript -->
            </div>
        </main>

        <!-- 5. DANH SÁCH LATEST BLOCKS & LATEST TRANSACTIONS (CHUẨN ETHERSCAN) -->
        <section class="grid grid-cols-1 lg:grid-cols-2 gap-5">
            <!-- CỘT 1: LATEST BLOCKS -->
            <div class="bg-white border border-slate-200/90 rounded-2xl shadow-2xs overflow-hidden flex flex-col justify-between">
                <div>
                    <div class="px-5 py-3.5 border-b border-slate-100 flex items-center justify-between">
                        <div class="flex items-center gap-2.5">
                            <span class="w-2 h-2 rounded-full bg-slate-900"></span>
                            <h3 class="text-xs font-black text-slate-900 uppercase tracking-wider">
                                Latest Blocks
                            </h3>
                        </div>
                        <span id="latestBlocksHeaderCount" class="text-[11px] font-mono text-slate-400 font-bold">0 blocks</span>
                    </div>
                    <div id="latestBlocksList" class="divide-y divide-slate-100 min-h-[180px]">
                        <!-- Render list of latest blocks -->
                    </div>
                </div>
                <div class="p-2.5 bg-slate-50 border-t border-slate-100 text-center">
                    <span class="text-[10px] font-mono text-slate-500 font-medium">PoW SHA-256 Engine &bull; Khối nối xích bất biến</span>
                </div>
            </div>

            <!-- CỘT 2: LATEST TRANSACTIONS -->
            <div class="bg-white border border-slate-200/90 rounded-2xl shadow-2xs overflow-hidden flex flex-col justify-between">
                <div>
                    <div class="px-5 py-3.5 border-b border-slate-100 flex items-center justify-between">
                        <div class="flex items-center gap-2.5">
                            <span class="w-2 h-2 rounded-full bg-emerald-600"></span>
                            <h3 class="text-xs font-black text-slate-900 uppercase tracking-wider">
                                Latest Transactions
                            </h3>
                        </div>
                        <span id="latestTxsHeaderCount" class="text-[11px] font-mono text-slate-400 font-bold">0 txns</span>
                    </div>
                    <div id="latestTxsList" class="divide-y divide-slate-100 min-h-[180px]">
                        <!-- Render list of latest transactions -->
                    </div>
                </div>
                <div class="p-2.5 bg-slate-50 border-t border-slate-100 text-center">
                    <span class="text-[10px] font-mono text-slate-500 font-medium">Bầu cử P2P phi tập trung &bull; Mỗi cử tri 1 phiếu</span>
                </div>
            </div>
        </section>

    </div>

    <!-- FOOTER ĐƠN GIẢN -->
    <footer class="max-w-7xl mx-auto w-full pt-6 pb-2 text-xs text-slate-400 font-medium flex flex-col sm:flex-row items-center justify-between gap-2 border-t border-slate-200/60 mt-4">
        <span>Sổ Cái Chuỗi Khối Bất Biến &bull; Đồ án Lập Trình Mạng HUTECH</span>
        <a href="/vote" target="_blank" class="text-slate-400 hover:text-slate-900 transition font-mono text-[11px] flex items-center gap-1">
            <span>🗳️</span> Cổng Bỏ Phiếu Cử Tri (/vote) ↗
        </a>
    </footer>

    <!-- MODAL CHI TIẾT BLOCK & TRANSACTION (CHUẨN ETHERSCAN / SOLSCAN) -->
    <div id="detailModal" class="fixed inset-0 bg-slate-900/40 backdrop-blur-xs z-50 hidden flex items-center justify-center p-4">
        <div class="bg-white border border-slate-200 rounded-2xl max-w-xl w-full p-6 shadow-xl space-y-4 max-h-[90vh] overflow-y-auto">
            <div class="flex items-center justify-between border-b border-slate-100 pb-3">
                <div class="flex items-center gap-2.5">
                    <span id="modalTypeBadge" class="text-[10px] font-mono font-bold px-2 py-0.5 rounded bg-slate-900 text-white">BLOCK</span>
                    <h3 id="modalTitle" class="text-sm font-extrabold text-slate-900">Chi Tiết</h3>
                </div>
                <button onclick="closeModal()" class="w-8 h-8 rounded-lg text-slate-400 hover:text-slate-700 hover:bg-slate-100 flex items-center justify-center text-lg font-bold cursor-pointer transition">&times;</button>
            </div>
            <div id="modalBody" class="space-y-3 text-xs font-mono">
                <!-- Nội dung chi tiết modal -->
            </div>
            <div class="pt-3 border-t border-slate-100 flex items-center justify-between">
                <span class="text-[10px] font-mono text-emerald-700 bg-emerald-50 px-2 py-0.5 rounded border border-emerald-200 font-bold">100% Cryptographically Verified</span>
                <button onclick="closeModal()" class="px-4 py-1.5 bg-slate-900 hover:bg-slate-800 text-white font-bold text-xs rounded-xl cursor-pointer transition">Đóng</button>
            </div>
        </div>
    </div>

    <!-- TOAST THÔNG BÁO COPIED -->
    <div id="copyToast" class="fixed bottom-6 right-6 bg-slate-900 text-white text-xs font-mono font-semibold px-4 py-2 rounded-xl shadow-lg transition-all duration-300 opacity-0 pointer-events-none z-50">
        Đã sao chép vào bộ nhớ tạm!
    </div>

    <!-- SCRIPT JAVASCRIPT -->
    <script>
        let currentChainData = null;
        let searchQuery = '';

        async function fetchStatus() {
            try {
                const res = await fetch('/api/election/status');
                const data = await res.json();
                currentChainData = data;
                renderDashboard(data);
            } catch (e) {
                console.error("Lỗi cập nhật:", e);
            }
        }

        function renderDashboard(data) {
            if (!data) return;

            // Stats
            document.querySelectorAll('#statBlocks, #statBlocksStream').forEach(el => el.textContent = data.blockCount);

            const totalVotesEl = document.getElementById('statTotalVotes');
            if (totalVotesEl) totalVotesEl.textContent = data.totalVotes;

            const latestHashEl = document.getElementById('statLatestHash');
            if (latestHashEl && data.blocks && data.blocks.length > 0) {
                const last = data.blocks[data.blocks.length - 1];
                const shortLast = last.hash.length > 18 ? (last.hash.substring(0, 10) + '...' + last.hash.substring(last.hash.length - 6)) : last.hash;
                latestHashEl.textContent = shortLast;
                latestHashEl.title = 'Nhấp để sao chép: ' + last.hash;
            }

            // RENDER CÁC BLOCK ĐỒNG BỘ 100% KÍCH THƯỚC (240px x 316px)
            const chainCont = document.getElementById('blockchainContainer');
            const blocks = data.blocks || [];
            
            chainCont.innerHTML = blocks.map((b, i) => {
                const isGenesis = (i === 0);
                const isLatest = (i === blocks.length - 1);
                const isTamperedBlock = data.isTampered && data.tamperedBlockIndex === i;
                
                const zeroMatches = b.hash.match(/^0+/) || [''];
                const zeroCount = zeroMatches[0].length;
                const leading = b.hash.substring(0, zeroCount);
                const rest = b.hash.substring(zeroCount);

                const txCount = isGenesis ? 0 : (b.noiDung && b.noiDung.indexOf(' | ') !== -1 ? b.noiDung.split(' | ').length : (b.noiDung ? 1 : 0));
                const shortTime = b.time.indexOf(' ') !== -1 ? b.time.split(' ')[1] : b.time;

                const isMatch = checkBlockMatch(b, i, searchQuery);

                return `
                    <!-- KHỐI CHUẨN KÍCH THƯỚC: 240px x 316px -->
                    <div onclick="showBlockDetail(${i})"
                         class="w-[240px] h-[316px] shrink-0 bg-white border ${isMatch ? 'ring-2 ring-indigo-500 border-indigo-400' : (isLatest ? 'border-slate-900 ring-1 ring-slate-900/10' : (isGenesis ? 'border-slate-300' : 'border-slate-200/90'))} rounded-2xl p-3.5 shadow-2xs block-card flex flex-col justify-between cursor-pointer select-none">
                        
                        <!-- 1. BLOCK HEADER (h-7) -->
                        <div class="h-7 flex items-center justify-between border-b border-slate-100 pb-1.5 shrink-0">
                            <span class="text-xs font-black font-mono ${isGenesis ? 'text-indigo-600' : 'text-slate-900'} uppercase">
                                ${isGenesis ? 'GENESIS #0' : 'BLOCK #' + i}
                            </span>
                            <span class="text-[9px] px-2 py-0.5 bg-slate-100 text-slate-700 rounded-md font-mono font-bold">
                                ${txCount} TXS
                            </span>
                        </div>

                        <!-- 2. HASH (h-[48px]) -->
                        <div class="h-[48px] shrink-0 flex flex-col justify-center">
                            <div class="h-4 text-[9px] font-mono font-bold text-slate-400 flex items-center justify-between">
                                <span>HASH (SHA-256)</span>
                                <span class="text-amber-700 font-extrabold">${zeroCount} zeros</span>
                            </div>
                            <div class="h-[28px] px-2 bg-slate-50 rounded-lg border border-slate-200/80 font-mono text-[9px] text-slate-700 truncate flex items-center" title="${escapeHtml(b.hash)}">
                                <span class="text-amber-600 font-black">${escapeHtml(leading)}</span><span class="text-slate-600">${escapeHtml(rest.substring(0, 10))}...</span>
                            </div>
                        </div>

                        <!-- 3. PREV HASH (h-[48px]) -->
                        <div class="h-[48px] shrink-0 flex flex-col justify-center">
                            <div class="h-4 text-[9px] font-mono font-bold text-slate-400 flex items-center justify-between">
                                <span>PREV HASH</span>
                            </div>
                            <div class="h-[28px] px-2 bg-slate-50 rounded-lg border border-slate-200/80 font-mono text-[9px] text-slate-400 truncate flex items-center" title="${escapeHtml(b.preHash)}">
                                ${escapeHtml(b.preHash.substring(0, 16))}...
                            </div>
                        </div>

                        <!-- 4. NỘI DUNG CONTENT (h-[82px]) -->
                        <div class="h-[82px] shrink-0 flex flex-col justify-between">
                            <div class="h-4 text-[9px] font-mono font-bold text-slate-400 flex items-center justify-between">
                                <span>DATA PAYLOAD</span>
                                <span class="text-[9px] text-slate-400 font-medium">${isGenesis ? 'Khởi tạo' : txCount + ' phiếu'}</span>
                            </div>
                            <div class="h-[62px] p-2 bg-slate-50 rounded-lg border border-slate-200/80 text-[10px] text-slate-800 overflow-y-auto leading-relaxed font-mono">
                                ${formatContent(b.noiDung, isGenesis)}
                            </div>
                        </div>

                        <!-- 5. TIME & NONCE (h-7) -->
                        <div class="h-7 pt-1.5 border-t border-slate-100 flex items-center justify-between text-[9px] font-mono shrink-0">
                            <span class="text-slate-400 truncate max-w-[110px]" title="${escapeHtml(b.time)}">${escapeHtml(shortTime)}</span>
                            <span class="font-bold text-slate-800 bg-slate-100 px-1.5 py-0.5 rounded">N: ${b.nonce}</span>
                        </div>
                    </div>

                    <!-- MŨI TÊN NỐI XÍCH CÂN XỨNG GIỮA CÁC KHỐI -->
                    ${i < blocks.length - 1 ? `
                    <div class="w-8 shrink-0 flex items-center justify-center text-slate-300 font-mono text-sm select-none">
                        ➔
                    </div>` : ''}
                `;
            }).join('');

            // Render 2 danh sách phong cách Etherscan
            renderLatestBlocks(data.blocks);
            renderLatestTransactions(data);
        }

        function formatContent(noiDung, isGenesis) {
            if (isGenesis) {
                return `<div class="text-slate-500 font-mono text-[10px] font-medium leading-relaxed">${escapeHtml(noiDung)}</div>`;
            }
            const items = (noiDung || '').split(' | ');
            return items.map(item => {
                return `<div class="py-0.5 font-mono text-[10px] text-slate-800 border-b border-slate-100 last:border-b-0 truncate">
                    ${escapeHtml(item)}
                </div>`;
            }).join('');
        }

        function renderLatestBlocks(blocks) {
            const listCont = document.getElementById('latestBlocksList');
            const countLabel = document.getElementById('latestBlocksHeaderCount');
            if (countLabel) countLabel.textContent = (blocks ? blocks.length : 0) + ' blocks';

            const latest = (blocks || []).slice().reverse().slice(0, 6);
            if (latest.length === 0) {
                listCont.innerHTML = `<div class="p-8 text-center text-xs text-slate-400 font-mono">Chưa có khối nào</div>`;
                return;
            }

            listCont.innerHTML = latest.map(b => {
                const isGenesis = (b.index === 0);
                const txCount = isGenesis ? 0 : (b.noiDung && b.noiDung.indexOf(' | ') !== -1 ? b.noiDung.split(' | ').length : (b.noiDung ? 1 : 0));
                const shortHash = b.hash.length > 14 ? (b.hash.substring(0, 8) + '...' + b.hash.substring(b.hash.length - 4)) : b.hash;

                return `
                    <div onclick="showBlockDetail(${b.index})" class="p-3.5 flex items-center justify-between gap-3 text-xs hover:bg-slate-50/80 transition cursor-pointer">
                        <div class="flex items-center gap-3 min-w-0">
                            <div class="w-9 h-9 rounded-xl bg-slate-100 border border-slate-200/90 flex items-center justify-center font-mono font-bold text-slate-700 text-[11px] shrink-0">
                                Bk
                            </div>
                            <div class="min-w-0">
                                <div class="flex items-center gap-2">
                                    <span class="font-mono font-bold text-slate-900">#${b.index}</span>
                                    <span class="text-[10px] text-slate-400 font-medium font-mono">${escapeHtml(b.time)}</span>
                                </div>
                                <div class="text-[11px] text-slate-500 font-mono truncate mt-0.5">
                                    Hash: <span class="text-slate-700 font-medium">${shortHash}</span> &bull; Nonce: <span class="text-slate-700">${b.nonce}</span>
                                </div>
                            </div>
                        </div>

                        <div class="text-right shrink-0">
                            <span class="px-2 py-0.5 rounded-md bg-slate-100 border border-slate-200 text-slate-700 font-mono text-[10px] font-bold">
                                ${txCount} txns
                            </span>
                            <div class="text-[9px] font-mono text-emerald-700 font-bold mt-1">
                                ${isGenesis ? 'Genesis' : 'PoW Verified'}
                            </div>
                        </div>
                    </div>
                `;
            }).join('');
        }

        function renderLatestTransactions(data) {
            const listCont = document.getElementById('latestTxsList');
            const countLabel = document.getElementById('latestTxsHeaderCount');
            const txs = data.transactions || [];

            let allTxs = [...txs];
            if (allTxs.length === 0) {
                (data.mempool || []).forEach(v => {
                    allTxs.push({
                        txHash: '0x' + (v.voteId || 'pending'),
                        blockIndex: -1,
                        status: 'Pending',
                        voterId: v.voterId,
                        candidate: v.candidate,
                        time: v.time
                    });
                });
                (data.blocks || []).slice(1).reverse().forEach(b => {
                    const items = (b.noiDung || '').split(' | ');
                    items.forEach((item, idx) => {
                        let v = '', c = '';
                        if (item.indexOf('Cử tri [') !== -1) {
                            const vs = item.indexOf('Cử tri [') + 8;
                            const ve = item.indexOf(']', vs);
                            if (ve !== -1) v = item.substring(vs, ve);
                        }
                        if (item.indexOf('bầu cho [') !== -1) {
                            const cs = item.indexOf('bầu cho [') + 9;
                            const ce = item.indexOf(']', cs);
                            if (ce !== -1) c = item.substring(cs, ce);
                        }
                        if (v || c) {
                            allTxs.push({
                                txHash: '0x' + b.hash.substring(0, 8),
                                blockIndex: b.index,
                                status: 'Confirmed',
                                voterId: v || 'Cử tri',
                                candidate: c || 'Ứng viên',
                                time: b.time
                            });
                        }
                    });
                });
            }

            if (countLabel) countLabel.textContent = allTxs.length + ' txns';

            const latest = allTxs.slice(0, 6);
            if (latest.length === 0) {
                listCont.innerHTML = `<div class="p-8 text-center text-xs text-slate-400 font-mono">Chưa có giao dịch / phiếu bầu nào</div>`;
                return;
            }

            listCont.innerHTML = latest.map(tx => {
                const isPending = (tx.status === 'Pending' || tx.blockIndex === -1);
                return `
                    <div onclick="showTxDetail('${escapeHtml(tx.txHash)}')" class="p-3.5 flex items-center justify-between gap-3 text-xs hover:bg-slate-50/80 transition cursor-pointer">
                        <div class="flex items-center gap-3 min-w-0">
                            <div class="w-9 h-9 rounded-xl ${isPending ? 'bg-amber-50 border-amber-200 text-amber-700' : 'bg-slate-100 border-slate-200/90 text-slate-600'} border flex items-center justify-center font-mono font-bold text-[11px] shrink-0">
                                Tx
                            </div>
                            <div class="min-w-0">
                                <div class="flex items-center gap-2">
                                    <span class="font-mono font-bold text-slate-900 truncate max-w-[110px]">${escapeHtml(tx.txHash)}</span>
                                    <span class="text-[10px] text-slate-400 font-medium font-mono">${escapeHtml(tx.time)}</span>
                                </div>
                                <div class="text-[11px] text-slate-500 font-mono truncate mt-0.5 flex items-center gap-1.5">
                                    <span>From: <span class="text-slate-800 font-semibold">[${escapeHtml(tx.voterId)}]</span></span>
                                    <span class="text-slate-300">➔</span>
                                    <span>To: <span class="text-slate-800 font-semibold">[${escapeHtml(tx.candidate)}]</span></span>
                                </div>
                            </div>
                        </div>

                        <div class="text-right shrink-0">
                            <span class="px-2 py-0.5 rounded-md ${isPending ? 'bg-amber-50 border-amber-200 text-amber-700' : 'bg-emerald-50 border-emerald-200 text-emerald-700'} font-mono text-[10px] font-bold">
                                1 Phiếu
                            </span>
                            <div class="text-[9px] font-mono mt-1 ${isPending ? 'text-amber-600 font-bold' : 'text-slate-400'}">
                                ${isPending ? 'In Mempool' : 'Block #' + tx.blockIndex}
                            </div>
                        </div>
                    </div>
                `;
            }).join('');
        }

        // =========================================================================
        //  MODAL CHI TIẾT (BLOCK & TRANSACTION INSPECTOR)
        // =========================================================================

        function showBlockDetail(index) {
            if (!currentChainData || !currentChainData.blocks || !currentChainData.blocks[index]) return;
            const b = currentChainData.blocks[index];
            const isGenesis = (index === 0);

            document.getElementById('modalTypeBadge').textContent = isGenesis ? 'GENESIS BLOCK' : 'BLOCK #' + index;
            document.getElementById('modalTitle').textContent = 'Chi Tiết Khối #' + index;

            const html = `
                <div class="space-y-2.5">
                    <div>
                        <div class="text-[10px] text-slate-400 uppercase font-bold">Block Hash (SHA-256)</div>
                        <div class="flex items-center gap-2 mt-1">
                            <div class="p-2 bg-slate-50 border border-slate-200 rounded-lg text-slate-800 text-[11px] font-bold break-all flex-1">
                                ${escapeHtml(b.hash)}
                            </div>
                            <button onclick="copyText('${escapeHtml(b.hash)}')" class="px-2.5 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-lg text-xs font-bold shrink-0 transition">Sao chép</button>
                        </div>
                    </div>

                    <div>
                        <div class="text-[10px] text-slate-400 uppercase font-bold">Previous Hash</div>
                        <div class="flex items-center gap-2 mt-1">
                            <div class="p-2 bg-slate-50 border border-slate-200 rounded-lg text-slate-500 text-[11px] break-all flex-1">
                                ${escapeHtml(b.preHash)}
                            </div>
                            <button onclick="copyText('${escapeHtml(b.preHash)}')" class="px-2.5 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-lg text-xs font-bold shrink-0 transition">Sao chép</button>
                        </div>
                    </div>

                    <div class="grid grid-cols-2 gap-3 pt-1">
                        <div>
                            <div class="text-[10px] text-slate-400 uppercase font-bold">Nonce PoW</div>
                            <div class="p-2 bg-slate-50 border border-slate-200 rounded-lg text-slate-800 font-bold text-xs mt-1">
                                ${b.nonce}
                            </div>
                        </div>
                        <div>
                            <div class="text-[10px] text-slate-400 uppercase font-bold">Thời gian tạo</div>
                            <div class="p-2 bg-slate-50 border border-slate-200 rounded-lg text-slate-800 text-xs mt-1">
                                ${escapeHtml(b.time)}
                            </div>
                        </div>
                    </div>

                    <div>
                        <div class="text-[10px] text-slate-400 uppercase font-bold">Dữ liệu ghi vào Sổ Cái (Payload)</div>
                        <div class="p-2.5 bg-slate-50 border border-slate-200 rounded-lg text-slate-800 text-xs mt-1 leading-relaxed whitespace-pre-wrap">
                            ${escapeHtml(b.noiDung)}
                        </div>
                    </div>
                </div>
            `;

            document.getElementById('modalBody').innerHTML = html;
            document.getElementById('detailModal').classList.remove('hidden');
        }

        function showTxDetail(txHash) {
            if (!currentChainData) return;
            const txs = currentChainData.transactions || [];
            const tx = txs.find(t => t.txHash === txHash);

            document.getElementById('modalTypeBadge').textContent = 'TRANSACTION';
            document.getElementById('modalTitle').textContent = 'Giao Dịch ' + txHash;

            const html = `
                <div class="space-y-3">
                    <div>
                        <div class="text-[10px] text-slate-400 uppercase font-bold">Transaction Hash</div>
                        <div class="flex items-center gap-2 mt-1">
                            <div class="p-2 bg-slate-50 border border-slate-200 rounded-lg text-slate-800 text-xs font-bold flex-1">
                                ${escapeHtml(txHash)}
                            </div>
                            <button onclick="copyText('${escapeHtml(txHash)}')" class="px-2.5 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-lg text-xs font-bold shrink-0 transition">Sao chép</button>
                        </div>
                    </div>

                    <div class="grid grid-cols-2 gap-3">
                        <div>
                            <div class="text-[10px] text-slate-400 uppercase font-bold">Trạng Thái</div>
                            <div class="p-2 bg-emerald-50 border border-emerald-200 text-emerald-800 font-bold text-xs rounded-lg mt-1">
                                ${tx ? escapeHtml(tx.status) : 'Confirmed'} (Khối #${tx ? tx.blockIndex : ''})
                            </div>
                        </div>
                        <div>
                            <div class="text-[10px] text-slate-400 uppercase font-bold">Thời Gian</div>
                            <div class="p-2 bg-slate-50 border border-slate-200 text-slate-800 text-xs rounded-lg mt-1">
                                ${tx ? escapeHtml(tx.time) : ''}
                            </div>
                        </div>
                    </div>

                    ${tx ? `
                    <div>
                        <div class="text-[10px] text-slate-400 uppercase font-bold">Người Gửi (From Voter) ➔ Người Nhận (To Candidate)</div>
                        <div class="p-2.5 bg-slate-50 border border-slate-200 rounded-lg text-xs text-slate-800 mt-1 flex items-center justify-between">
                            <span class="font-bold">Cử tri MSSV: ${escapeHtml(tx.voterId)}</span>
                            <span class="text-slate-400">➔</span>
                            <span class="font-bold text-indigo-700">Ứng viên: ${escapeHtml(tx.candidate)}</span>
                        </div>
                    </div>
                    ` : ''}
                </div>
            `;

            document.getElementById('modalBody').innerHTML = html;
            document.getElementById('detailModal').classList.remove('hidden');
        }

        function closeModal() {
            document.getElementById('detailModal').classList.add('hidden');
        }

        // =========================================================================
        //  TÌM KIẾM & TIỆN ÍCH
        // =========================================================================

        function handleSearch(q) {
            searchQuery = (q || '').trim().toLowerCase();
            if (currentChainData) {
                renderDashboard(currentChainData);
            }
        }

        function checkBlockMatch(b, index, q) {
            if (!q) return false;
            if (('#' + index) === q || String(index) === q) return true;
            if (b.hash.toLowerCase().indexOf(q) !== -1) return true;
            if (b.preHash.toLowerCase().indexOf(q) !== -1) return true;
            if (b.noiDung && b.noiDung.toLowerCase().indexOf(q) !== -1) return true;
            return false;
        }

        function copyLatestHash() {
            if (currentChainData && currentChainData.blocks && currentChainData.blocks.length > 0) {
                const last = currentChainData.blocks[currentChainData.blocks.length - 1];
                copyText(last.hash);
            }
        }

        function copyText(txt) {
            navigator.clipboard.writeText(txt).then(() => {
                const toast = document.getElementById('copyToast');
                toast.classList.remove('opacity-0');
                setTimeout(() => toast.classList.add('opacity-0'), 2000);
            }).catch(e => console.error(e));
        }

        function escapeHtml(text) {
            if (!text) return '';
            return String(text)
                .replace(/&/g, "&amp;")
                .replace(/</g, "&lt;")
                .replace(/>/g, "&gt;")
                .replace(/"/g, "&quot;")
                .replace(/'/g, "&#039;");
        }

        // Đóng modal khi nhấn Escape
        window.addEventListener('keydown', e => {
            if (e.key === 'Escape') closeModal();
        });

        // Tự động tải và cập nhật mỗi 1.5 giây
        fetchStatus();
        setInterval(fetchStatus, 1500);
    </script>
</body>
</html>
""";
    }
}
