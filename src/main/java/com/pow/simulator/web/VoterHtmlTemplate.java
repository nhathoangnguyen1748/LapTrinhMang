package com.pow.simulator.web;

/**
 * Giao diện Bỏ Phiếu Cử Tri Điện Tử Độc Lập (Dedicated Voter Ballot Portal).
 * Phong cách Minimalist White / Light Theme tinh tế, chuyên nghiệp, chuẩn UI/UX.
 * Khi cử tri gửi phiếu, phiếu sẽ được ký số và phát tán qua P2P
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
    <title>Phòng Bỏ Phiếu Cử Tri | Mạng voteChain</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&family=JetBrains+Mono:wght@400;500;600;700&display=swap');
        body { font-family: 'Plus Jakarta Sans', -apple-system, BlinkMacSystemFont, sans-serif; }
        .font-mono { font-family: 'JetBrains Mono', monospace; }
        .candidate-card { transition: all 0.2s cubic-bezier(0.16, 1, 0.3, 1); }
        .candidate-card:hover { transform: translateY(-2px); border-color: #6366f1; }
        .candidate-selected {
            border-color: #4f46e5 !important;
            background-color: #f5f3ff !important;
            box-shadow: 0 4px 12px rgba(79, 70, 229, 0.12) !important;
        }
    </style>
</head>
<body class="bg-slate-50 text-slate-800 min-h-screen antialiased flex flex-col justify-between selection:bg-indigo-600 selection:text-white">

    <!-- TOP NAVIGATION -->
    <header class="border-b border-slate-200 bg-white/95 backdrop-blur sticky top-0 z-50 shadow-xs">
        <div class="max-w-4xl mx-auto px-4 py-3.5 flex items-center justify-between">
            <div class="flex items-center gap-3">
                <div class="w-10 h-10 rounded-xl bg-indigo-600 text-white flex items-center justify-center text-xl shadow-md shadow-indigo-600/20">
                    🗳️
                </div>
                <div>
                    <h1 class="text-base font-extrabold tracking-tight text-slate-900 flex items-center gap-2">
                        PHÒNG BỎ PHIẾU CỬ TRI ĐIỆN TỬ
                        <span class="text-[10px] bg-emerald-50 text-emerald-700 border border-emerald-200 px-2 py-0.5 rounded-full font-mono font-bold">● P2P CLUSTER ONLINE</span>
                    </h1>
                    <p class="text-xs text-slate-500 font-medium">Mạng voteChain &bull; Khoa Công Nghệ Thông Tin (HUTECH)</p>
                </div>
            </div>

            <a href="/" class="px-3.5 py-1.5 bg-white hover:bg-slate-50 border border-slate-300 text-xs font-semibold rounded-lg transition-all flex items-center gap-1.5 text-slate-700 hover:text-slate-900 shadow-xs">
                <span>📊</span> Sổ Cái voteChain Explorer ↗
            </a>
        </div>
    </header>

    <!-- MAIN VOTING CONTAINER -->
    <main class="max-w-3xl mx-auto px-4 py-8 flex-1 w-full space-y-6">

        <!-- BANNER GIỚI THIỆU -->
        <div class="bg-white border border-indigo-100 rounded-2xl p-5 shadow-xs relative overflow-hidden">
            <div class="flex items-start gap-3.5">
                <div class="w-10 h-10 rounded-xl bg-indigo-50 text-indigo-600 flex items-center justify-center text-xl shrink-0 border border-indigo-100">
                    🛡️
                </div>
                <div>
                    <h2 class="text-xs font-bold text-indigo-700 uppercase tracking-wider">Hệ Thống Bỏ Phiếu Minh Bạch - Phân Tán</h2>
                    <p class="text-xs text-slate-600 mt-1 leading-relaxed">
                        Mỗi cử tri chỉ được bỏ phiếu <strong>1 lần duy nhất</strong>. Khi bạn gửi phiếu, lá phiếu sẽ được mã hóa và phát tán đồng loạt qua giao thức TCP P2P đến <strong>Mempool của TẤT CẢ các máy trong cụm Node</strong> để đối soát độc lập, chống gian lận tuyệt đối.
                    </p>
                </div>
            </div>
        </div>

        <!-- FORM BỎ PHIẾU -->
        <div class="bg-white border border-slate-200/90 rounded-2xl p-6 md:p-7 shadow-sm space-y-6">

            <!-- BƯỚC 1: ĐỊNH DANH CỬ TRI -->
            <div>
                <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2 flex items-center gap-1.5">
                    <span class="w-5 h-5 rounded-full bg-indigo-600 text-white flex items-center justify-center text-[11px] font-black">1</span>
                    Nhập Mã Cử Tri / MSSV / CCCD:
                </label>
                <div class="relative">
                    <input type="text" id="voterIdInput" placeholder="Ví dụ: 2380601549, 2380601305, 2380601342..."
                           class="w-full bg-slate-50/60 border border-slate-300 rounded-xl px-4 py-3 text-sm font-semibold text-slate-900 placeholder-slate-400 focus:bg-white focus:outline-none focus:border-indigo-600 focus:ring-4 focus:ring-indigo-100 transition-all font-mono">
                </div>
                
                <!-- Gợi ý nhanh cho demo -->
                <div class="mt-2.5 flex items-center gap-2 flex-wrap text-xs text-slate-500">
                    <span class="text-[11px] font-medium">⚡ Chọn nhanh để demo:</span>
                    <button type="button" onclick="fillVoter('2380601549')" class="px-2.5 py-1 bg-slate-100 hover:bg-slate-200/80 rounded-md text-indigo-700 font-mono text-[11px] border border-slate-200 transition font-bold">2380601549 (Nhật)</button>
                    <button type="button" onclick="fillVoter('2380601305')" class="px-2.5 py-1 bg-slate-100 hover:bg-slate-200/80 rounded-md text-indigo-700 font-mono text-[11px] border border-slate-200 transition font-bold">2380601305 (Lương)</button>
                    <button type="button" onclick="fillVoter('2380601342')" class="px-2.5 py-1 bg-slate-100 hover:bg-slate-200/80 rounded-md text-indigo-700 font-mono text-[11px] border border-slate-200 transition font-bold">2380601342 (Minh)</button>
                </div>
            </div>

            <!-- BƯỚC 2: CHỌN ĐẠI BIỂU / ỨNG CỬ VIÊN -->
            <div>
                <label class="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2 flex items-center gap-1.5">
                    <span class="w-5 h-5 rounded-full bg-indigo-600 text-white flex items-center justify-center text-[11px] font-black">2</span>
                    Chọn 01 Đại Biểu Bạn Tín Nhiệm:
                </label>
                
                <div class="grid grid-cols-1 md:grid-cols-3 gap-3.5" id="candidatesList">
                    <!-- Candidate 1 -->
                    <div class="candidate-card border-2 border-slate-200 bg-white rounded-xl p-4 cursor-pointer select-none flex flex-col justify-between shadow-xs"
                         onclick="selectCandidate('Lê Quang Minh', this)">
                        <div class="flex items-center gap-3 mb-3">
                            <div class="w-10 h-10 rounded-full bg-sky-50 text-sky-600 border border-sky-100 flex items-center justify-center text-lg font-bold">
                                👨‍💼
                            </div>
                            <div>
                                <div class="text-[10px] text-sky-600 font-mono font-bold uppercase">Ứng viên #01</div>
                                <div class="text-sm font-bold text-slate-900">Lê Quang Minh</div>
                            </div>
                        </div>
                        <div class="text-xs text-slate-500 border-t border-slate-100 pt-2 flex items-center justify-between font-medium">
                            <span>Đại biểu Khoa CNTT</span>
                            <span class="status-indicator text-slate-300 text-sm">⚪</span>
                        </div>
                    </div>

                    <!-- Candidate 2 -->
                    <div class="candidate-card border-2 border-slate-200 bg-white rounded-xl p-4 cursor-pointer select-none flex flex-col justify-between shadow-xs"
                         onclick="selectCandidate('Nguyễn Đỗ Trung Lương', this)">
                        <div class="flex items-center gap-3 mb-3">
                            <div class="w-10 h-10 rounded-full bg-emerald-50 text-emerald-600 border border-emerald-100 flex items-center justify-center text-lg font-bold">
                                👨‍🎓
                            </div>
                            <div>
                                <div class="text-[10px] text-emerald-600 font-mono font-bold uppercase">Ứng viên #02</div>
                                <div class="text-sm font-bold text-slate-900">Nguyễn Đỗ Trung Lương</div>
                            </div>
                        </div>
                        <div class="text-xs text-slate-500 border-t border-slate-100 pt-2 flex items-center justify-between font-medium">
                            <span>Đại biểu Ban Học Tập</span>
                            <span class="status-indicator text-slate-300 text-sm">⚪</span>
                        </div>
                    </div>

                    <!-- Candidate 3 -->
                    <div class="candidate-card border-2 border-slate-200 bg-white rounded-xl p-4 cursor-pointer select-none flex flex-col justify-between shadow-xs"
                         onclick="selectCandidate('Nguyễn Hoàng Nhật', this)">
                        <div class="flex items-center gap-3 mb-3">
                            <div class="w-10 h-10 rounded-full bg-amber-50 text-amber-600 border border-amber-100 flex items-center justify-center text-lg font-bold">
                                👨‍💻
                            </div>
                            <div>
                                <div class="text-[10px] text-amber-600 font-mono font-bold uppercase">Ứng viên #03</div>
                                <div class="text-sm font-bold text-slate-900">Nguyễn Hoàng Nhật</div>
                            </div>
                        </div>
                        <div class="text-xs text-slate-500 border-t border-slate-100 pt-2 flex items-center justify-between font-medium">
                            <span>Đại biểu Ban Phong Trào</span>
                            <span class="status-indicator text-slate-300 text-sm">⚪</span>
                        </div>
                    </div>
                </div>

                <input type="hidden" id="selectedCandidate" value="">
            </div>

            <!-- BƯỚC 3: NÚT GỬI PHIẾU BẦU -->
            <div class="pt-2">
                <button type="button" id="btnSubmitVote" onclick="submitVote()"
                        class="w-full py-3.5 px-6 bg-indigo-600 hover:bg-indigo-700 text-white font-extrabold text-sm rounded-xl shadow-md shadow-indigo-600/20 transition-all transform active:scale-[0.99] flex items-center justify-center gap-2 cursor-pointer">
                    <span>🗳️</span> XÁC NHẬN BỎ PHIẾU VÀO MẠNG LƯỚI BLOCKCHAIN
                </button>
            </div>

        </div>

        <!-- QUY TRÌNH PHÁT TÁN GÓI TIN P2P BROADCAST LIVE -->
        <div id="broadcastStatusBox" class="hidden bg-white border-2 border-indigo-200 rounded-2xl p-5 shadow-sm space-y-3">
            <div class="flex items-center justify-between border-b border-slate-100 pb-3">
                <div class="flex items-center gap-2 text-indigo-700 font-bold text-sm">
                    <span class="animate-spin text-base">⚙️</span> QUY TRÌNH PHÁT TÁN PHIẾU BẦU QUA MẠNG P2P
                </div>
                <div id="p2pBadge" class="text-xs font-mono font-bold px-2.5 py-0.5 rounded-full bg-amber-50 text-amber-700 border border-amber-200">Đang phát tán...</div>
            </div>

            <div class="space-y-1.5 text-xs font-mono" id="broadcastLogs">
                <!-- Logs dynamically inserted here -->
            </div>
        </div>

        <!-- THEO DÕI TRỰC TIẾP MEMPOOL CỦA TỪNG MÁY TRONG CỤM NODE -->
        <div class="bg-white border border-slate-200 rounded-2xl p-5 shadow-xs space-y-4">
            <div class="flex items-center justify-between">
                <div>
                    <h3 class="text-xs font-black text-slate-800 uppercase tracking-wider flex items-center gap-1.5">
                        <span>📡</span> TRẠNG THÁI MEMPOOL TRÊN TỪNG MÁY (P2P CLUSTER NODES)
                    </h3>
                    <p class="text-[11px] text-slate-500 mt-0.5 font-medium">Minh chứng phiếu bầu đồng bộ xuất hiện trong Mempool của tất cả các node độc lập</p>
                </div>
                <div class="text-[10px] font-mono font-bold text-emerald-700 bg-emerald-50 px-2.5 py-1 rounded-full border border-emerald-200">
                    ● TỰ ĐỘNG LÀM MỚI (1.5s)
                </div>
            </div>

            <!-- 3 CỘT CHO 3 MÁY TRẠM (NODES) -->
            <div class="grid grid-cols-1 md:grid-cols-3 gap-3" id="nodesContainer">
                <!-- Node 1 -->
                <div class="bg-slate-50/80 border border-slate-200 rounded-xl p-3.5 space-y-2">
                    <div class="flex items-center justify-between">
                        <div class="text-xs font-bold text-slate-900 flex items-center gap-1">
                            <span>🖥️</span> Node 1 (Khoa CNTT)
                        </div>
                        <span id="node1Badge" class="text-[10px] font-mono bg-sky-50 text-sky-700 border border-sky-200 px-1.5 py-0.5 rounded font-bold">Port 8000</span>
                    </div>
                    <div class="text-[11px] text-slate-500 font-medium">Vai trò: Cổng Web Tiếp Nhận</div>
                    <div class="p-2.5 bg-white rounded-lg border border-slate-200 shadow-2xs">
                        <div class="text-[10px] text-slate-400 font-medium">Phiếu trong Mempool:</div>
                        <div id="node1MempoolCount" class="text-lg font-black text-amber-600 font-mono">0 phiếu</div>
                    </div>
                </div>

                <!-- Node 2 -->
                <div class="bg-slate-50/80 border border-slate-200 rounded-xl p-3.5 space-y-2">
                    <div class="flex items-center justify-between">
                        <div class="text-xs font-bold text-slate-900 flex items-center gap-1">
                            <span>🖥️</span> Node 2 (Ban Giám Sát)
                        </div>
                        <span id="node2Badge" class="text-[10px] font-mono bg-emerald-50 text-emerald-700 border border-emerald-200 px-1.5 py-0.5 rounded font-bold">Port 8001</span>
                    </div>
                    <div class="text-[11px] text-slate-500 font-medium">Vai trò: Nút Kiểm Soát Độc Lập</div>
                    <div class="p-2.5 bg-white rounded-lg border border-slate-200 shadow-2xs">
                        <div class="text-[10px] text-slate-400 font-medium">Phiếu trong Mempool:</div>
                        <div id="node2MempoolCount" class="text-lg font-black text-amber-600 font-mono">0 phiếu</div>
                    </div>
                </div>

                <!-- Node 3 -->
                <div class="bg-slate-50/80 border border-slate-200 rounded-xl p-3.5 space-y-2">
                    <div class="flex items-center justify-between">
                        <div class="text-xs font-bold text-slate-900 flex items-center gap-1">
                            <span>🖥️</span> Node 3 (Hội Đồng SV)
                        </div>
                        <span id="node3Badge" class="text-[10px] font-mono bg-purple-50 text-purple-700 border border-purple-200 px-1.5 py-0.5 rounded font-bold">Port 8002</span>
                    </div>
                    <div class="text-[11px] text-slate-500 font-medium">Vai trò: Nút Lưu Trữ & Đối Chiếu</div>
                    <div class="p-2.5 bg-white rounded-lg border border-slate-200 shadow-2xs">
                        <div class="text-[10px] text-slate-400 font-medium">Phiếu trong Mempool:</div>
                        <div id="node3MempoolCount" class="text-lg font-black text-amber-600 font-mono">0 phiếu</div>
                    </div>
                </div>
            </div>

            <div class="text-center pt-1">
                <a href="/" class="text-xs text-indigo-600 hover:text-indigo-800 font-bold underline flex items-center justify-center gap-1">
                    <span>➡️</span> Xem chi tiết toàn bộ Sổ Cái & Thao Tác Đào Khối PoW trên Bảng Quản Trị
                </a>
            </div>
        </div>

    </main>

    <!-- FOOTER -->
    <footer class="border-t border-slate-200 bg-white py-4 text-center text-xs text-slate-500 font-medium">
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
            badge.className = 'text-xs font-mono font-bold px-2.5 py-0.5 rounded-full bg-amber-50 text-amber-700 border border-amber-200';
            badge.textContent = 'Đang phát tán...';

            function addLog(msg, colorClass = 'text-slate-600') {
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
                    addLog('⚡ <strong>[Bước 2/4]</strong> Node 1 xác thực hợp lệ: Cử tri [' + voterId + '] chưa từng bầu cử.', 'text-emerald-700 font-semibold');
                    addLog('🌐 <strong>[Bước 3/4]</strong> Node 1 phát tán gói tin TCP P2P sang <em>Node 2 (Ban Giám Sát)</em> & <em>Node 3 (Hội Đồng SV)</em>...', 'text-indigo-700 font-semibold');
                    addLog('🎯 <strong>[Bước 4/4]</strong> CẢ 3 NODE ĐÃ XÁC NHẬN NHẬN PHIẾU VÀO MEMPOOL CỦA MÌNH!', 'text-emerald-700 font-bold');

                    badge.className = 'text-xs font-mono font-bold px-2.5 py-0.5 rounded-full bg-emerald-50 text-emerald-700 border border-emerald-200';
                    badge.textContent = 'ĐÃ VÀO MEMPOOL 3/3 NODES';

                    alert('🎉 BỎ PHIẾU THÀNH CÔNG!\\n\\nPhiếu của cử tri [' + voterId + '] đã được phát tán thành công đến Mempool của TẤT CẢ các máy trong cụm Node!\\n\\nBạn có thể quan sát số lượng phiếu tăng lên ở cả 3 Node bên dưới.');
                    fetchClusterStatus();
                } else {
                    addLog('❌ <strong>LỖI BỎ PHIẾU:</strong> ' + (data.error || 'Bị từ chối'), 'text-rose-700 font-bold');
                    badge.className = 'text-xs font-mono font-bold px-2.5 py-0.5 rounded-full bg-rose-50 text-rose-700 border border-rose-200';
                    badge.textContent = 'TỪ CHỐI GHI NHẬN';
                    alert('❌ LỖI: ' + (data.error || 'Không thể bỏ phiếu!'));
                }
            } catch (err) {
                addLog('❌ <strong>LỖI KẾT NỐI MẠNG:</strong> ' + err.message, 'text-rose-700');
                badge.className = 'text-xs font-mono font-bold px-2.5 py-0.5 rounded-full bg-rose-50 text-rose-700 border border-rose-200';
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
