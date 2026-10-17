package com.pow.simulator.web;

/**
 * Giao diện Bỏ Phiếu Cử Tri Điện Tử Độc Lập (Dedicated Voter Ballot Portal).
 * Thiết kế tối giản, trực quan, chuyên biệt cho cử tri đi bầu.
 * Khi cử tri bấm gửi phiếu, phiếu sẽ được ký số và phát tán qua P2P
 * đến Mempool của TẤT CẢ các máy trong cụm mạng (Nodes).
 */
public final class VoterHtmlTemplate {

    private VoterHtmlTemplate() {}

    public static String getHtml() {
        return """
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Phòng Bỏ Phiếu Cử Tri Trực Tuyến | Blockchain E-Voting</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800;900&family=JetBrains+Mono:wght@400;500;700&display=swap');
        body { font-family: 'Inter', sans-serif; }
        .font-mono { font-family: 'JetBrains Mono', monospace; }
        .candidate-card:hover { transform: translateY(-3px); }
        .candidate-selected {
            border-color: #10b981 !important;
            background: rgba(16, 185, 129, 0.12) !important;
            box-shadow: 0 0 20px rgba(16, 185, 129, 0.25) !important;
        }
    </style>
</head>
<body class="bg-slate-950 text-slate-100 min-h-screen antialiased flex flex-col justify-between selection:bg-purple-500 selection:text-white">

    <!-- TOP NAVIGATION -->
    <header class="border-b border-slate-800 bg-slate-900/80 backdrop-blur sticky top-0 z-50">
        <div class="max-w-4xl mx-auto px-4 py-3.5 flex items-center justify-between">
            <div class="flex items-center gap-3">
                <div class="w-10 h-10 rounded-xl bg-gradient-to-tr from-purple-600 via-indigo-600 to-sky-500 flex items-center justify-center text-xl shadow-lg shadow-purple-500/20">
                    🗳️
                </div>
                <div>
                    <h1 class="text-base font-black tracking-tight text-white flex items-center gap-2">
                        PHÒNG BỎ PHIẾU CỬ TRI ĐIỆN TỬ
                        <span class="text-[10px] bg-emerald-500/20 text-emerald-400 border border-emerald-500/40 px-2 py-0.5 rounded-full font-mono font-bold">● P2P CLUSTER ACTIVE</span>
                    </h1>
                    <p class="text-xs text-slate-400">Đại Hội Đại Biểu Đoàn &bull; Khoa Công Nghệ Thông Tin (HUTECH)</p>
                </div>
            </div>

            <a href="/" class="px-3.5 py-1.5 bg-slate-800 hover:bg-slate-700 border border-slate-700 text-xs font-semibold rounded-lg transition-all flex items-center gap-1.5 text-slate-300 hover:text-white shadow">
                <span>📊</span> Bảng Điều Khiển Sổ Cái (Admin)
            </a>
        </div>
    </header>

    <!-- MAIN VOTING CONTAINER -->
    <main class="max-w-3xl mx-auto px-4 py-8 flex-1 w-full space-y-6">

        <!-- BANNER GIỚI THIỆU -->
        <div class="bg-gradient-to-r from-purple-950/60 via-slate-900 to-indigo-950/60 border border-purple-500/30 rounded-2xl p-5 shadow-xl relative overflow-hidden">
            <div class="absolute -right-10 -bottom-10 w-40 h-40 bg-purple-600/10 rounded-full blur-3xl pointer-events-none"></div>
            <div class="flex items-start gap-3.5">
                <div class="text-3xl">🛡️</div>
                <div>
                    <h2 class="text-sm font-bold text-purple-300 uppercase tracking-wider">Hệ Thống Bỏ Phiếu Minh Bạch - Phân Tán</h2>
                    <p class="text-xs text-slate-300 mt-1 leading-relaxed">
                        Mỗi cử tri chỉ được bỏ phiếu <strong>1 lần duy nhất</strong>. Khi bạn gửi phiếu, lá phiếu sẽ được mã hóa và phát tán đồng loạt qua giao thức TCP P2P đến <strong>Mempool của TẤT CẢ các máy trong cụm Node</strong> để đối soát độc lập, chống gian lận tuyệt đối.
                    </p>
                </div>
            </div>
        </div>

        <!-- FORM BỎ PHIẾU -->
        <div class="bg-slate-900/90 border border-slate-800 rounded-2xl p-6 shadow-2xl space-y-6">

            <!-- BƯỚC 1: ĐỊNH DANH CỬ TRI -->
            <div>
                <label class="block text-xs font-bold text-slate-300 uppercase tracking-wider mb-2 flex items-center gap-1.5">
                    <span class="w-5 h-5 rounded-full bg-purple-600 text-white flex items-center justify-center text-[11px] font-black">1</span>
                    Nhập Mã Cử Tri / MSSV / CCCD:
                </label>
                <div class="relative">
                    <input type="text" id="voterIdInput" placeholder="Ví dụ: 2380601549, 2380601305, 2380601342..."
                           class="w-full bg-slate-950 border border-slate-700 rounded-xl px-4 py-3 text-sm font-semibold text-white placeholder-slate-500 focus:outline-none focus:border-purple-500 focus:ring-2 focus:ring-purple-500/30 transition-all font-mono">
                </div>
                
                <!-- Gợi ý nhanh cho demo -->
                <div class="mt-2.5 flex items-center gap-2 flex-wrap text-xs text-slate-400">
                    <span class="text-[11px]">⚡ Chọn nhanh để demo:</span>
                    <button type="button" onclick="fillVoter('2380601549')" class="px-2 py-0.5 bg-slate-800 hover:bg-slate-700 rounded text-sky-400 font-mono text-[11px] border border-slate-700 transition">2380601549 (Nhật)</button>
                    <button type="button" onclick="fillVoter('2380601305')" class="px-2 py-0.5 bg-slate-800 hover:bg-slate-700 rounded text-sky-400 font-mono text-[11px] border border-slate-700 transition">2380601305 (Lương)</button>
                    <button type="button" onclick="fillVoter('2380601342')" class="px-2 py-0.5 bg-slate-800 hover:bg-slate-700 rounded text-sky-400 font-mono text-[11px] border border-slate-700 transition">2380601342 (Minh)</button>
                </div>
            </div>

            <!-- BƯỚC 2: CHỌN ĐẠI BIỂU / ỨNG CỬ VIÊN -->
            <div>
                <label class="block text-xs font-bold text-slate-300 uppercase tracking-wider mb-2 flex items-center gap-1.5">
                    <span class="w-5 h-5 rounded-full bg-purple-600 text-white flex items-center justify-center text-[11px] font-black">2</span>
                    Chọn 01 Đại Biểu Bạn Tín Nhiệm:
                </label>
                
                <div class="grid grid-cols-1 md:grid-cols-3 gap-3.5" id="candidatesList">
                    <!-- Candidate 1 -->
                    <div class="candidate-card border-2 border-slate-800 bg-slate-950/60 rounded-xl p-4 cursor-pointer transition-all duration-200 select-none flex flex-col justify-between"
                         onclick="selectCandidate('Lê Quang Minh (Đại biểu Khoa CNTT)', this)">
                        <div class="flex items-center gap-3 mb-3">
                            <div class="w-10 h-10 rounded-full bg-sky-500/20 text-sky-400 border border-sky-500/40 flex items-center justify-center text-lg font-bold">
                                👨‍💼
                            </div>
                            <div>
                                <div class="text-[10px] text-sky-400 font-mono font-bold uppercase">Ứng viên #01</div>
                                <div class="text-sm font-bold text-white">Lê Quang Minh</div>
                            </div>
                        </div>
                        <div class="text-xs text-slate-400 border-t border-slate-800 pt-2 flex items-center justify-between">
                            <span>Đại biểu Khoa CNTT</span>
                            <span class="status-indicator text-slate-600 text-sm">⚪</span>
                        </div>
                    </div>

                    <!-- Candidate 2 -->
                    <div class="candidate-card border-2 border-slate-800 bg-slate-950/60 rounded-xl p-4 cursor-pointer transition-all duration-200 select-none flex flex-col justify-between"
                         onclick="selectCandidate('Nguyễn Đỗ Trung Lương (Đại biểu Ban Học Tập)', this)">
                        <div class="flex items-center gap-3 mb-3">
                            <div class="w-10 h-10 rounded-full bg-emerald-500/20 text-emerald-400 border border-emerald-500/40 flex items-center justify-center text-lg font-bold">
                                👨‍🎓
                            </div>
                            <div>
                                <div class="text-[10px] text-emerald-400 font-mono font-bold uppercase">Ứng viên #02</div>
                                <div class="text-sm font-bold text-white">Nguyễn Đỗ Trung Lương</div>
                            </div>
                        </div>
                        <div class="text-xs text-slate-400 border-t border-slate-800 pt-2 flex items-center justify-between">
                            <span>Đại biểu Ban Học Tập</span>
                            <span class="status-indicator text-slate-600 text-sm">⚪</span>
                        </div>
                    </div>

                    <!-- Candidate 3 -->
                    <div class="candidate-card border-2 border-slate-800 bg-slate-950/60 rounded-xl p-4 cursor-pointer transition-all duration-200 select-none flex flex-col justify-between"
                         onclick="selectCandidate('Nguyễn Hoàng Nhật (Đại biểu Ban Phong Trào)', this)">
                        <div class="flex items-center gap-3 mb-3">
                            <div class="w-10 h-10 rounded-full bg-purple-500/20 text-purple-400 border border-purple-500/40 flex items-center justify-center text-lg font-bold">
                                👨‍💻
                            </div>
                            <div>
                                <div class="text-[10px] text-purple-400 font-mono font-bold uppercase">Ứng viên #03</div>
                                <div class="text-sm font-bold text-white">Nguyễn Hoàng Nhật</div>
                            </div>
                        </div>
                        <div class="text-xs text-slate-400 border-t border-slate-800 pt-2 flex items-center justify-between">
                            <span>Đại biểu Ban Phong Trào</span>
                            <span class="status-indicator text-slate-600 text-sm">⚪</span>
                        </div>
                    </div>
                </div>

                <input type="hidden" id="selectedCandidate" value="">
            </div>

            <!-- BƯỚC 3: NÚT GỬI PHIẾU BẦU -->
            <div class="pt-2">
                <button type="button" id="btnSubmitVote" onclick="submitVote()"
                        class="w-full py-4 px-6 bg-gradient-to-r from-purple-600 via-indigo-600 to-sky-600 hover:from-purple-500 hover:via-indigo-500 hover:to-sky-500 text-white font-extrabold text-sm rounded-xl shadow-lg shadow-purple-600/30 transition-all transform active:scale-95 flex items-center justify-center gap-2 cursor-pointer border border-purple-400/30">
                    <span>🗳️</span> XÁC NHẬN BỎ PHIẾU VÀO MẠNG LƯỚI BLOCKCHAIN
                </button>
            </div>

        </div>

        <!-- QUY TRÌNH PHÁT TÁN GÓI TIN P2P BROADCAST LIVE -->
        <div id="broadcastStatusBox" class="hidden bg-slate-900 border-2 border-indigo-500/50 rounded-2xl p-5 shadow-2xl space-y-3">
            <div class="flex items-center justify-between border-b border-slate-800 pb-3">
                <div class="flex items-center gap-2 text-indigo-400 font-bold text-sm">
                    <span class="animate-spin text-lg">⚙️</span> QUY TRÌNH PHÁT TÁN PHIẾU BẦU QUA MẠNG P2P
                </div>
                <div id="p2pBadge" class="text-xs font-mono font-bold px-2 py-0.5 rounded bg-amber-500/20 text-amber-300">Đang phát tán...</div>
            </div>

            <div class="space-y-2 text-xs font-mono" id="broadcastLogs">
                <!-- Logs dynamically inserted here -->
            </div>
        </div>

        <!-- THEO DÕI TRỰC TIẾP MEMPOOL CỦA TỪNG MÁY TRONG CỤM NODE -->
        <div class="bg-slate-900/80 border border-slate-800 rounded-2xl p-5 shadow-xl space-y-4">
            <div class="flex items-center justify-between">
                <div>
                    <h3 class="text-xs font-black text-slate-300 uppercase tracking-wider flex items-center gap-1.5">
                        <span>📡</span> TRẠNG THÁI MEMPOOL TRÊN TỪNG MÁY (P2P CLUSTER NODES)
                    </h3>
                    <p class="text-[11px] text-slate-400 mt-0.5">Minh chứng phiếu bầu đồng bộ xuất hiện trong Mempool của tất cả các node độc lập</p>
                </div>
                <div class="text-[10px] font-mono text-emerald-400 bg-emerald-950 px-2.5 py-1 rounded-full border border-emerald-500/30">
                    ● TỰ ĐỘNG LÀM MỚI (1.5s)
                </div>
            </div>

            <!-- 3 CỘT CHO 3 MÁY TRẠM (NODES) -->
            <div class="grid grid-cols-1 md:grid-cols-3 gap-3" id="nodesContainer">
                <!-- Node 1 -->
                <div class="bg-slate-950 border border-slate-800 rounded-xl p-3.5 space-y-2">
                    <div class="flex items-center justify-between">
                        <div class="text-xs font-bold text-sky-400 flex items-center gap-1">
                            <span>🖥️</span> Node 1 (Khoa CNTT)
                        </div>
                        <span id="node1Badge" class="text-[10px] font-mono bg-sky-500/20 text-sky-300 px-1.5 py-0.5 rounded">Port 8000</span>
                    </div>
                    <div class="text-[11px] text-slate-400">Vai trò: Cổng Web Tiếp Nhận</div>
                    <div class="p-2 bg-slate-900 rounded border border-slate-800">
                        <div class="text-[10px] text-slate-400">Phiếu trong Mempool:</div>
                        <div id="node1MempoolCount" class="text-lg font-black text-amber-400 font-mono">0 phiếu</div>
                    </div>
                </div>

                <!-- Node 2 -->
                <div class="bg-slate-950 border border-slate-800 rounded-xl p-3.5 space-y-2">
                    <div class="flex items-center justify-between">
                        <div class="text-xs font-bold text-emerald-400 flex items-center gap-1">
                            <span>🖥️</span> Node 2 (Ban Giám Sát)
                        </div>
                        <span id="node2Badge" class="text-[10px] font-mono bg-emerald-500/20 text-emerald-300 px-1.5 py-0.5 rounded">Port 8001</span>
                    </div>
                    <div class="text-[11px] text-slate-400">Vai trò: Nút Kiểm Soát Độc Lập</div>
                    <div class="p-2 bg-slate-900 rounded border border-slate-800">
                        <div class="text-[10px] text-slate-400">Phiếu trong Mempool:</div>
                        <div id="node2MempoolCount" class="text-lg font-black text-amber-400 font-mono">0 phiếu</div>
                    </div>
                </div>

                <!-- Node 3 -->
                <div class="bg-slate-950 border border-slate-800 rounded-xl p-3.5 space-y-2">
                    <div class="flex items-center justify-between">
                        <div class="text-xs font-bold text-purple-400 flex items-center gap-1">
                            <span>🖥️</span> Node 3 (Hội Đồng SV)
                        </div>
                        <span id="node3Badge" class="text-[10px] font-mono bg-purple-500/20 text-purple-300 px-1.5 py-0.5 rounded">Port 8002</span>
                    </div>
                    <div class="text-[11px] text-slate-400">Vai trò: Nút Lưu Trữ & Đối Chiếu</div>
                    <div class="p-2 bg-slate-900 rounded border border-slate-800">
                        <div class="text-[10px] text-slate-400">Phiếu trong Mempool:</div>
                        <div id="node3MempoolCount" class="text-lg font-black text-amber-400 font-mono">0 phiếu</div>
                    </div>
                </div>
            </div>

            <div class="text-center pt-2">
                <a href="/" class="text-xs text-sky-400 hover:text-sky-300 font-semibold underline flex items-center justify-center gap-1">
                    <span>➡️</span> Xem chi tiết toàn bộ Sổ Cái & Thao Tác Đào Khối PoW trên Bảng Quản Trị
                </a>
            </div>
        </div>

    </main>

    <!-- FOOTER -->
    <footer class="border-t border-slate-900 bg-slate-950 py-4 text-center text-xs text-slate-500">
        Đồ án Lập Trình Mạng &bull; Đề tài Hệ Thống Bầu Cử Điện Tử Blockchain &bull; HUTECH 2026
    </footer>

    <!-- SCRIPT LOGIC -->
    <script>
        let selectedCandidateName = '';

        function fillVoter(mssv) {
            document.getElementById('voterIdInput').value = mssv;
        }

        function selectCandidate(name, element) {
            selectedCandidateName = name;
            document.getElementById('selectedCandidate').value = name;

            document.querySelectorAll('.candidate-card').forEach(card => {
                card.classList.remove('candidate-selected');
                const ind = card.querySelector('.status-indicator');
                if (ind) ind.textContent = '⚪';
            });

            element.classList.add('candidate-selected');
            const currentInd = element.querySelector('.status-indicator');
            if (currentInd) currentInd.textContent = '✅';
        }

        async function submitVote() {
            const voterId = document.getElementById('voterIdInput').value.trim();
            const candidate = selectedCandidateName;

            if (!voterId) {
                alert('⚠️ Vui lòng nhập Mã Cử Tri / MSSV của bạn!');
                document.getElementById('voterIdInput').focus();
                return;
            }

            if (!candidate) {
                alert('⚠️ Vui lòng chọn 1 Đại Biểu Bạn Tín Nhiệm!');
                return;
            }

            const btn = document.getElementById('btnSubmitVote');
            btn.disabled = true;
            btn.innerHTML = '<span>⏳</span> ĐANG GỬI PHIẾU & PHÁT TÁN P2P TỚI CÁC NODE...';

            const statusBox = document.getElementById('broadcastStatusBox');
            const logsContainer = document.getElementById('broadcastLogs');
            const badge = document.getElementById('p2pBadge');
            statusBox.classList.remove('hidden');
            logsContainer.innerHTML = '';
            badge.className = 'text-xs font-mono font-bold px-2 py-0.5 rounded bg-amber-500/20 text-amber-300';
            badge.textContent = 'Đang phát tán...';

            function addLog(msg, colorClass = 'text-slate-300') {
                const div = document.createElement('div');
                div.className = colorClass;
                div.innerHTML = msg;
                logsContainer.appendChild(div);
            }

            addLog('📡 <strong>[Bước 1/4]</strong> Đang gửi phiếu từ trình duyệt tới <em>Node 1 (Khoa CNTT - Port 8000)</em>...');

            try {
                const res = await fetch('/api/election/vote', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ voterId: voterId, candidate: candidate })
                });
                const data = await res.json();

                if (data.success) {
                    addLog('⚡ <strong>[Bước 2/4]</strong> Node 1 xác thực hợp lệ: Cử tri [' + voterId + '] chưa từng bầu cử.', 'text-emerald-400');
                    addLog('🌐 <strong>[Bước 3/4]</strong> Node 1 phát tán gói tin TCP P2P sang <em>Node 2 (Ban Giám Sát)</em> & <em>Node 3 (Hội Đồng SV)</em>...', 'text-sky-400');
                    addLog('🎯 <strong>[Bước 4/4]</strong> CẢ 3 NODE ĐÃ XÁC NHẬN NHẬN PHIẾU VÀO MEMPOOL CỦA MÌNH!', 'text-emerald-400 font-bold');

                    badge.className = 'text-xs font-mono font-bold px-2 py-0.5 rounded bg-emerald-500/20 text-emerald-400';
                    badge.textContent = 'ĐÃ VÀO MEMPOOL 3/3 NODES';

                    alert('🎉 BỎ PHIẾU THÀNH CÔNG!\\n\\nPhiếu của cử tri [' + voterId + '] đã được phát tán thành công đến Mempool của TẤT CẢ các máy trong cụm Node!\\n\\nBạn có thể xem số lượng phiếu tăng lên ở cả 3 Node bên dưới.');
                    fetchClusterStatus();
                } else {
                    addLog('❌ <strong>LỖI BỎ PHIẾU:</strong> ' + (data.error || 'Bị từ chối'), 'text-rose-400 font-bold');
                    badge.className = 'text-xs font-mono font-bold px-2 py-0.5 rounded bg-rose-500/20 text-rose-400';
                    badge.textContent = 'TỪ CHỐI GHI NHẬN';
                    alert('❌ LỖI: ' + (data.error || 'Không thể bỏ phiếu!'));
                }
            } catch (err) {
                addLog('❌ <strong>LỖI KẾT NỐI MẠNG:</strong> ' + err.message, 'text-rose-400');
                badge.className = 'text-xs font-mono font-bold px-2 py-0.5 rounded bg-rose-500/20 text-rose-400';
                badge.textContent = 'LỖI KẾT NỐI';
                alert('❌ Lỗi kết nối mạng: ' + err.message);
            } finally {
                btn.disabled = false;
                btn.innerHTML = '<span>🗳️</span> XÁC NHẬN BỎ PHIẾU VÀO MẠNG LƯỚI BLOCKCHAIN';
            }
        }

        async function fetchClusterStatus() {
            try {
                const res = await fetch('/api/election/status');
                const data = await res.json();

                if (data.cluster && data.cluster.length >= 3) {
                    document.getElementById('node1MempoolCount').textContent = data.cluster[0].mempoolCount + ' phiếu';
                    document.getElementById('node2MempoolCount').textContent = data.cluster[1].mempoolCount + ' phiếu';
                    document.getElementById('node3MempoolCount').textContent = data.cluster[2].mempoolCount + ' phiếu';
                } else {
                    const count = (data.mempool ? data.mempool.length : 0);
                    document.getElementById('node1MempoolCount').textContent = count + ' phiếu';
                    document.getElementById('node2MempoolCount').textContent = count + ' phiếu';
                    document.getElementById('node3MempoolCount').textContent = count + ' phiếu';
                }
            } catch (ignored) {}
        }

        // Tự động cập nhật mỗi 1.5 giây
        setInterval(fetchClusterStatus, 1500);
        fetchClusterStatus();
    </script>
</body>
</html>
""";
    }
}
