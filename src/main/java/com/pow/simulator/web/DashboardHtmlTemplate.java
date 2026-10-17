package com.pow.simulator.web;

/**
 * Giao diện Sổ Cái Chuỗi Khối Blockchain (Blockchain Ledger).
 * Thiết kế Tối Giản Cốt Lõi (Basic & Ultra-Minimalist White Theme).
 * Tập trung trọn vẹn vào:
 * 1. Mempool & Nút Đào Khối (PoW)
 * 2. Sổ Cái Chuỗi Khối: Các Block nối xích với nhau qua PreHash và Nonce 5 thuộc tính.
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
    <title>Sổ Cái Blockchain (Blockchain Ledger) | E-Voting</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&family=JetBrains+Mono:wght@400;500;600;700&display=swap');
        body { font-family: 'Plus Jakarta Sans', -apple-system, BlinkMacSystemFont, sans-serif; }
        .font-mono { font-family: 'JetBrains Mono', monospace; }
        ::-webkit-scrollbar { width: 6px; height: 6px; }
        ::-webkit-scrollbar-track { background: #f8fafc; }
        ::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 9999px; }
        ::-webkit-scrollbar-thumb:hover { background: #94a3b8; }
        .block-card {
            transition: all 0.2s cubic-bezier(0.16, 1, 0.3, 1);
        }
        .block-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 20px -4px rgba(0, 0, 0, 0.06);
        }
    </style>
</head>
<body class="bg-[#f8fafc] text-slate-800 antialiased min-h-screen p-4 md:p-8 flex flex-col justify-between selection:bg-indigo-600 selection:text-white">

    <div class="max-w-7xl mx-auto w-full space-y-5">

        <!-- 1. HEADER TINH GỌN NHẤT -->
        <header class="bg-white border border-slate-200/90 rounded-2xl px-5 py-4 flex flex-col sm:flex-row items-center justify-between gap-4 shadow-2xs">
            <div class="flex items-center gap-3">
                <div class="w-10 h-10 rounded-xl bg-slate-900 text-white flex items-center justify-center text-xl shrink-0 font-bold">
                    ⛓️
                </div>
                <div>
                    <h1 class="text-lg font-extrabold text-slate-900 tracking-tight flex items-center gap-2">
                        SỔ CÁI CHUỖI KHỐI (BLOCKCHAIN LEDGER)
                        <span class="text-[10px] font-mono bg-emerald-50 text-emerald-700 border border-emerald-200 px-2 py-0.5 rounded-full font-bold">● ONLINE</span>
                    </h1>
                    <p class="text-xs text-slate-500 font-medium">Hệ Thống Bầu Cử Điện Tử Proof-of-Work &bull; Lập Trình Mạng HUTECH</p>
                </div>
            </div>

            <!-- ĐỘ KHÓ & NÚT PHÒNG BỎ PHIẾU -->
            <div class="flex items-center gap-3">
                <div class="flex items-center gap-1.5 bg-slate-50 border border-slate-200 px-3 py-1.5 rounded-xl text-xs">
                    <span class="text-slate-500 font-medium">Độ Khó:</span>
                    <select id="difficultySelect" onchange="changeDifficulty(this.value)"
                            class="bg-transparent font-mono font-bold text-slate-900 focus:outline-none cursor-pointer">
                        <option value="1">1 số 0 (0...)</option>
                        <option value="2">2 số 0 (00...)</option>
                        <option value="3">3 số 0 (000...)</option>
                        <option value="4">4 số 0 (0000...)</option>
                        <option value="5" selected>5 số 0 (00000...)</option>
                        <option value="6">6 số 0 (000000...)</option>
                    </select>
                </div>

                <a href="/vote" target="_blank"
                   class="px-4 py-2 bg-indigo-600 hover:bg-indigo-700 text-white text-xs font-bold rounded-xl shadow-xs flex items-center gap-1.5 transition">
                    <span>🗳️</span> Phòng Bỏ Phiếu Cử Tri ↗
                </a>
            </div>
        </header>

        <!-- 2. THANH ĐIỀU KHIỂN MEMPOOL & NÚT KHAI THÁC POW -->
        <div class="bg-white border border-slate-200/90 rounded-2xl p-4 md:p-5 flex flex-col md:flex-row items-center justify-between gap-4 shadow-2xs">
            <!-- MEMPOOL STATUS -->
            <div class="flex items-center gap-4 flex-wrap">
                <div class="flex items-center gap-2">
                    <span class="text-lg">⏳</span>
                    <div>
                        <div class="text-xs font-bold text-slate-900">
                            Hòm Phiếu Chờ (Mempool): <span id="mempoolBadgeCount" class="text-amber-600 font-mono font-black text-sm">0 phiếu</span>
                        </div>
                        <div class="text-[11px] text-slate-500 font-medium">
                            Đồng bộ P2P: <span class="text-emerald-700 font-bold">Node 1 (Port 8000)</span> &bull; <span class="text-emerald-700 font-bold">Node 2</span> &bull; <span class="text-emerald-700 font-bold">Node 3</span>
                        </div>
                    </div>
                </div>

                <!-- TÓM TẮT PHIẾU CHỜ TRONG MEMPOOL -->
                <div id="mempoolQuickPreview" class="hidden text-xs text-slate-600 bg-slate-50 px-3 py-1.5 rounded-lg border border-slate-200 font-mono">
                    <!-- Danh sách ngắn gọn -->
                </div>
            </div>

            <!-- NÚT KHAI THÁC POW (MINE BLOCK) -->
            <button onclick="mineBlock()" id="btnMine"
                    class="w-full md:w-auto px-6 py-2.5 bg-slate-900 hover:bg-slate-800 text-white font-extrabold text-xs rounded-xl shadow-xs transition flex items-center justify-center gap-2 cursor-pointer">
                <span>⛏️</span> ĐÀO KHỐI (MINE BLOCK POW)
            </button>
        </div>

        <!-- CẢNH BÁO NẾU PHÁT HIỆN GIAN LẬN SỬA SỔ CÁI -->
        <div id="tamperAlertBanner" class="hidden bg-rose-50 border border-rose-300 text-rose-800 p-4 rounded-xl flex items-center justify-between shadow-xs">
            <div class="flex items-center gap-3">
                <span class="text-xl">🚨</span>
                <div>
                    <h3 class="font-bold text-xs text-rose-900 uppercase">Cảnh Báo: Phát Hiện Sửa Đổi Khối Trong Sổ Cái!</h3>
                    <p id="tamperReasonText" class="text-xs text-rose-700 font-mono mt-0.5"></p>
                </div>
            </div>
            <button onclick="resetElection()" class="px-3 py-1 bg-rose-600 hover:bg-rose-700 text-white font-bold rounded-lg text-xs transition">
                Phục Hồi
            </button>
        </div>

        <!-- 3. SỔ CÁI CHUỖI KHỐI (CÁC BLOCK NỐI XÍCH VỚI NHAU) -->
        <main class="bg-white border border-slate-200/90 rounded-2xl p-5 md:p-6 shadow-2xs space-y-4">
            <div class="flex items-center justify-between border-b border-slate-100 pb-3">
                <div class="flex items-center gap-2">
                    <span class="text-base text-slate-500 font-bold">🔗</span>
                    <h2 class="text-xs font-black text-slate-900 uppercase tracking-wider">
                        CHUỖI KHỐI SỔ CÁI (<span id="statBlocks">0</span> KHỐI ĐÃ KHÓA)
                    </h2>
                </div>

                <div class="flex items-center gap-2">
                    <button onclick="tamperTest()" class="text-xs text-rose-600 hover:text-rose-800 font-medium px-2.5 py-1 rounded bg-rose-50 hover:bg-rose-100 border border-rose-200 transition cursor-pointer">
                        ⚠️ Thử Gian Lận
                    </button>
                    <button onclick="resetElection()" class="text-xs text-slate-600 hover:text-slate-800 font-medium px-2.5 py-1 rounded bg-slate-100 hover:bg-slate-200 border border-slate-200 transition cursor-pointer">
                        🔄 Reset Sổ Cái
                    </button>
                </div>
            </div>

            <!-- CHUỖI KHỐI NỐI XÍCH (HORIZONTAL STREAM) -->
            <div id="blockchainContainer" class="flex items-center gap-3 overflow-x-auto pb-4 pt-2">
                <!-- Render các khối bằng Javascript -->
            </div>
        </main>

        <!-- 4. DANH SÁCH LATEST BLOCKS & LATEST TRANSACTIONS (CHUẨN ETHERSCAN) -->
        <section class="grid grid-cols-1 lg:grid-cols-2 gap-5">
            <!-- CỘT 1: LATEST BLOCKS -->
            <div class="bg-white border border-slate-200/90 rounded-2xl shadow-2xs overflow-hidden flex flex-col justify-between">
                <div>
                    <div class="px-5 py-3.5 border-b border-slate-100 flex items-center justify-between">
                        <div class="flex items-center gap-2.5">
                            <span class="w-2 h-2 rounded-full bg-indigo-600"></span>
                            <h3 class="text-xs font-black text-slate-900 uppercase tracking-wider">
                                Latest Blocks
                            </h3>
                        </div>
                        <span id="latestBlocksHeaderCount" class="text-[11px] font-mono text-slate-400 font-bold">0 blocks</span>
                    </div>
                    <div id="latestBlocksList" class="divide-y divide-slate-100 min-h-[160px]">
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
                    <div id="latestTxsList" class="divide-y divide-slate-100 min-h-[160px]">
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
    <footer class="max-w-7xl mx-auto w-full pt-6 text-center text-xs text-slate-400 font-medium">
        Đồ án Lập Trình Mạng &bull; Sổ Cái Bầu Cử Phân Tán PoW (Java 21) &bull; HUTECH 2026
    </footer>

    <!-- SCRIPT JAVASCRIPT -->
    <script>
        async function fetchStatus() {
            try {
                const res = await fetch('/api/election/status');
                const data = await res.json();
                renderDashboard(data);
            } catch (e) {
                console.error("Lỗi cập nhật:", e);
            }
        }

        async function changeDifficulty(val) {
            try {
                const res = await fetch('/api/election/difficulty', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ difficulty: parseInt(val) })
                });
                const data = await res.json();
                if (data.success) {
                    fetchStatus();
                }
            } catch (err) {
                console.error("Lỗi đổi độ khó:", err);
            }
        }

        function renderDashboard(data) {
            // Sync difficulty select
            const diffSel = document.getElementById('difficultySelect');
            if (diffSel && data.difficulty && document.activeElement !== diffSel) {
                diffSel.value = data.difficulty;
            }

            // Stats
            document.querySelectorAll('#statBlocks').forEach(el => el.textContent = data.blockCount);
            document.getElementById('mempoolBadgeCount').textContent = data.mempool.length + ' phiếu';

            // Preview mempool
            const memPreview = document.getElementById('mempoolQuickPreview');
            if (data.mempool && data.mempool.length > 0) {
                memPreview.classList.remove('hidden');
                memPreview.innerHTML = 'Phiếu chờ: ' + data.mempool.map(v => escapeHtml(v.voterId)).join(', ');
            } else {
                memPreview.classList.add('hidden');
            }

            // Tamper status
            const alertBanner = document.getElementById('tamperAlertBanner');
            if (data.isTampered || !data.isChainValid) {
                alertBanner.classList.remove('hidden');
                document.getElementById('tamperReasonText').textContent = data.tamperReason || 'Mã hash không còn khớp với nội dung!';
            } else {
                alertBanner.classList.add('hidden');
            }

            // RENDER CÁC BLOCK ĐƯỢC NỐI VỚI NHAU (COMPACT, BASIC & BEAUTIFUL, ZERO ICONS)
            const chainCont = document.getElementById('blockchainContainer');
            chainCont.innerHTML = data.blocks.map((b, i) => {
                const isGenesis = (i === 0);
                const isTamperedBlock = data.isTampered && data.tamperedBlockIndex === i;
                const zeroMatches = b.hash.match(/^0+/) || [''];
                const zeroCount = zeroMatches[0].length;
                const leading = b.hash.substring(0, zeroCount);
                const rest = b.hash.substring(zeroCount);

                return `
                    <div class="w-[220px] shrink-0 bg-white border ${isTamperedBlock ? 'border-rose-400 bg-rose-50/20' : (isGenesis ? 'border-indigo-400' : 'border-slate-200')} rounded-xl p-3 shadow-2xs block-card space-y-2">
                        
                        <!-- BLOCK HEADER -->
                        <div class="flex items-center justify-between border-b border-slate-100 pb-1.5">
                            <span class="text-xs font-bold font-mono ${isGenesis ? 'text-indigo-600' : 'text-slate-900'} uppercase">
                                ${isGenesis ? 'GENESIS #0' : 'BLOCK #' + i}
                            </span>
                            <span class="text-[9px] px-1.5 py-0.5 bg-slate-100 text-slate-600 rounded font-mono font-bold">
                                NONCE: ${b.nonce}
                            </span>
                        </div>

                        <!-- 5 THUỘC TÍNH CỐT LÕI (BASIC, KHÔNG CẦN ICON) -->
                        <div class="space-y-1.5 text-xs">
                            
                            <!-- 1. HASH -->
                            <div>
                                <div class="text-[9px] font-mono font-semibold text-slate-400 flex items-center justify-between">
                                    <span>HASH</span>
                                    <span class="text-amber-700">${zeroCount} zeros</span>
                                </div>
                                <div class="p-1 bg-slate-50 rounded border border-slate-200/80 font-mono text-[9px] ${isTamperedBlock ? 'text-rose-600 font-bold' : 'text-slate-700'} truncate">
                                    <span class="text-amber-600 font-black">${escapeHtml(leading)}</span>${escapeHtml(rest.substring(0, 10))}...
                                </div>
                            </div>

                            <!-- 2. TIME -->
                            <div class="flex items-center justify-between text-[9px] font-mono">
                                <span class="font-semibold text-slate-400">TIME</span>
                                <span class="text-slate-600 truncate max-w-[140px]">${escapeHtml(b.time)}</span>
                            </div>

                            <!-- 3. PRE HASH -->
                            <div>
                                <span class="text-[9px] font-mono font-semibold text-slate-400 block">PREV HASH</span>
                                <div class="p-1 bg-slate-50 rounded border border-slate-200/80 font-mono text-[9px] text-slate-400 truncate">
                                    ${escapeHtml(b.preHash.substring(0, 16))}...
                                </div>
                            </div>

                            <!-- 4. NỘI DUNG (CONTENT) -->
                            <div>
                                <span class="text-[9px] font-mono font-semibold text-slate-400 block">CONTENT</span>
                                <div class="p-1.5 bg-slate-50 rounded border border-slate-200/80 text-[10px] text-slate-800 break-words max-h-16 overflow-y-auto leading-relaxed">
                                    ${formatContent(b.noiDung, isGenesis)}
                                </div>
                            </div>

                        </div>
                    </div>

                    ${i < data.blocks.length - 1 ? `
                    <div class="shrink-0 flex items-center text-slate-300 font-mono text-xs select-none px-0.5">
                        ➔
                    </div>` : ''}
                `;
            }).join('');

            // Render 2 danh sách phong cách Etherscan
            renderLatestBlocks(data.blocks);
            renderLatestTransactions(data);
        }

        async function mineBlock() {
            const btn = document.getElementById('btnMine');
            btn.disabled = true;
            btn.innerHTML = '<span>⏳</span> ĐANG ĐÀO BLOCK...';

            try {
                const res = await fetch('/api/election/mine', { method: 'POST' });
                const data = await res.json();
                if (data.success) {
                    alert('🎉 ĐÃ ĐÀO KHỐI THÀNH CÔNG!\\n\\nBlock mới:\\nHash: ' + data.hash + '\\nNonce: ' + data.nonce);
                    fetchStatus();
                } else {
                    alert('❌ LỖI KHAI THÁC: ' + (data.error || 'Lỗi đóng khối!'));
                }
            } catch (err) {
                alert('Lỗi: ' + err.message);
            } finally {
                btn.disabled = false;
                btn.innerHTML = '<span>⛏️</span> ĐÀO KHỐI (MINE BLOCK POW)';
            }
        }

        async function tamperTest() {
            if (!confirm('Thử nghiệm sửa đổi nội dung Khối #1 trong Sổ Cái để kiểm tra mã băm SHA-256?')) return;
            try {
                const res = await fetch('/api/election/tamper', { method: 'POST' });
                const data = await res.json();
                if (data.success) fetchStatus();
            } catch (err) {
                alert('Lỗi: ' + err.message);
            }
        }

        async function resetElection() {
            if (!confirm('Làm mới lại toàn bộ Sổ Cái về Genesis Block #0?')) return;
            try {
                const res = await fetch('/api/election/reset', { method: 'POST' });
                const data = await res.json();
                if (data.success) fetchStatus();
            } catch (err) {
                alert('Lỗi: ' + err.message);
            }
        }

        function formatContent(noiDung, isGenesis) {
            if (isGenesis) {
                return `<div class="text-slate-500 font-mono text-[10px] font-medium">${escapeHtml(noiDung)}</div>`;
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
                listCont.innerHTML = `<div class="p-6 text-center text-xs text-slate-400 font-mono">Chưa có khối nào</div>`;
                return;
            }

            listCont.innerHTML = latest.map(b => {
                const isGenesis = (b.index === 0);
                const txCount = isGenesis ? 0 : (b.noiDung && b.noiDung.includes(' | ') ? b.noiDung.split(' | ').length : (b.noiDung ? 1 : 0));
                const shortHash = b.hash.length > 14 ? (b.hash.substring(0, 8) + '...' + b.hash.substring(b.hash.length - 4)) : b.hash;

                return `
                    <div class="p-3.5 flex items-center justify-between gap-3 text-xs hover:bg-slate-50/70 transition">
                        <div class="flex items-center gap-3 min-w-0">
                            <div class="w-9 h-9 rounded-xl bg-slate-100 border border-slate-200/90 flex items-center justify-center font-mono font-bold text-slate-600 text-[11px] shrink-0">
                                Bk
                            </div>
                            <div class="min-w-0">
                                <div class="flex items-center gap-2">
                                    <span class="font-mono font-bold text-indigo-600">#${b.index}</span>
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
                listCont.innerHTML = `<div class="p-6 text-center text-xs text-slate-400 font-mono">Chưa có giao dịch / phiếu bầu nào</div>`;
                return;
            }

            listCont.innerHTML = latest.map(tx => {
                const isPending = (tx.status === 'Pending' || tx.blockIndex === -1);
                return `
                    <div class="p-3.5 flex items-center justify-between gap-3 text-xs hover:bg-slate-50/70 transition">
                        <div class="flex items-center gap-3 min-w-0">
                            <div class="w-9 h-9 rounded-xl ${isPending ? 'bg-amber-50 border-amber-200 text-amber-700' : 'bg-slate-100 border-slate-200/90 text-slate-600'} border flex items-center justify-center font-mono font-bold text-[11px] shrink-0">
                                Tx
                            </div>
                            <div class="min-w-0">
                                <div class="flex items-center gap-2">
                                    <span class="font-mono font-bold text-indigo-600 truncate max-w-[110px]">${escapeHtml(tx.txHash)}</span>
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

        function escapeHtml(text) {
            if (!text) return '';
            return String(text)
                .replace(/&/g, "&amp;")
                .replace(/</g, "&lt;")
                .replace(/>/g, "&gt;")
                .replace(/"/g, "&quot;")
                .replace(/'/g, "&#039;");
        }

        // Tự động tải và cập nhật mỗi 1.5 giây
        fetchStatus();
        setInterval(fetchStatus, 1500);
    </script>
</body>
</html>
""";
    }
}
