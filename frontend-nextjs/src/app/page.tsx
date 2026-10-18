'use client';

import React, { useState, useEffect, useRef } from 'react';
import {
  Activity,
  AlertTriangle,
  ArrowRight,
  CheckCircle2,
  Cpu,
  Layers,
  Network,
  Pickaxe,
  RefreshCw,
  Send,
  ShieldAlert,
  Terminal,
  Zap,
} from 'lucide-react';

interface PeerNode {
  id: string;
  port: number;
  chainHeight: number;
  mempoolCount: number;
  peerCount: number;
  tipHash: string;
}

interface Transaction {
  txId: string;
  sender: string;
  recipient: string;
  amount: number;
  timestamp: number;
}

interface Block {
  index: number;
  timestamp: number;
  previousHash: string;
  merkleRoot: string;
  nonce: number;
  difficultyBits: number;
  hash: string;
  shortHash: string;
  isCompromised: boolean;
  transactions: Transaction[];
}

interface NetworkStatus {
  isTampered: boolean;
  tamperedBlockIndex: number;
  tamperReason: string;
  nodes: PeerNode[];
  logs: string[];
}

export default function Dashboard() {
  const [status, setStatus] = useState<NetworkStatus | null>(null);
  const [chain, setChain] = useState<Block[]>([]);
  const [isConnected, setIsConnected] = useState<boolean>(false);
  const [isMining, setIsMining] = useState<boolean>(false);
  const [autoScroll, setAutoScroll] = useState<boolean>(true);
  const terminalRef = useRef<HTMLDivElement>(null);
  const canvasRef = useRef<HTMLCanvasElement>(null);

  // Particles for canvas
  const particlesRef = useRef<
    Array<{ from: string; to: string; progress: number; speed: number; color: string }>
  >([]);

  // Fetch status and chain periodically
  const fetchData = async () => {
    try {
      const [resStatus, resChain] = await Promise.all([
        fetch('/api/status'),
        fetch('/api/chain'),
      ]);

      if (!resStatus.ok || !resChain.ok) throw new Error('API request failed');

      const dataStatus: NetworkStatus = await resStatus.json();
      const dataChain: Block[] = await resChain.json();

      setStatus(dataStatus);
      setChain(dataChain);
      setIsConnected(true);
    } catch (err) {
      setIsConnected(false);
    }
  };

  useEffect(() => {
    fetchData();
    const interval = setInterval(fetchData, 1200);
    return () => clearInterval(interval);
  }, []);

  // Auto-scroll logs
  useEffect(() => {
    if (autoScroll && terminalRef.current) {
      terminalRef.current.scrollTop = terminalRef.current.scrollHeight;
    }
  }, [status?.logs, autoScroll]);

  // Actions
  const triggerPacket = (from: string, to: string, color: string) => {
    particlesRef.current.push({
      from,
      to,
      progress: 0,
      speed: 0.035,
      color,
    });
  };

  const handleSendTx = async () => {
    triggerPacket('C', 'B', '#8b5cf6');
    setTimeout(() => triggerPacket('B', 'A', '#06b6d4'), 200);

    try {
      const sender = 'Student_' + Math.floor(Math.random() * 900 + 100);
      const recipient = 'Cafe_' + Math.floor(Math.random() * 50 + 1);
      const amount = parseFloat((Math.random() * 20 + 1).toFixed(2));

      await fetch('/api/send-tx', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ sender, recipient, amount }),
      });
      fetchData();
    } catch (err: any) {
      alert('Lỗi gửi Tx: ' + err.message);
    }
  };

  const handleMine = async () => {
    if (isMining) return;
    setIsMining(true);
    try {
      triggerPacket('A', 'B', '#f59e0b');
      setTimeout(() => {
        triggerPacket('B', 'C', '#f59e0b');
        triggerPacket('A', 'D', '#f59e0b');
      }, 200);

      await fetch('/api/mine', { method: 'POST' });
      fetchData();
    } catch (err: any) {
      alert('Lỗi khai thác: ' + err.message);
    } finally {
      setIsMining(false);
    }
  };

  const handleConnectNodeD = async () => {
    try {
      triggerPacket('D', 'A', '#10b981');
      await fetch('/api/connect-node-d', { method: 'POST' });
      fetchData();
    } catch (err: any) {
      alert('Lỗi kết nối Node D: ' + err.message);
    }
  };

  const handleTamper = async () => {
    try {
      await fetch('/api/tamper', { method: 'POST' });
      fetchData();
    } catch (err: any) {
      alert('Lỗi tấn công: ' + err.message);
    }
  };

  const handleReset = async () => {
    if (!confirm('Bạn có chắc muốn Reset mạng về Genesis?')) return;
    try {
      await fetch('/api/reset', { method: 'POST' });
      fetchData();
    } catch (err: any) {
      alert('Lỗi reset: ' + err.message);
    }
  };

  // Canvas animation loop
  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    let animId: number;

    const render = () => {
      const w = canvas.width;
      const h = canvas.height;
      ctx.clearRect(0, 0, w, h);

      const nodeCoords: Record<string, { x: number; y: number; label: string; port: string; color: string }> = {
        A: { x: w * 0.5, y: h * 0.22, label: 'Node A (Miner)', port: '8001', color: '#f59e0b' },
        B: { x: w * 0.22, y: h * 0.65, label: 'Node B (Relay)', port: '8002', color: '#06b6d4' },
        C: { x: w * 0.78, y: h * 0.65, label: 'Node C (Wallet)', port: '8003', color: '#8b5cf6' },
        D: { x: w * 0.5, y: h * 0.82, label: 'Node D (Sync)', port: '8004', color: '#10b981' },
      };

      // Draw links
      const links = [
        ['B', 'A'],
        ['C', 'B'],
        ['D', 'A'],
      ];

      links.forEach(([from, to]) => {
        const p1 = nodeCoords[from];
        const p2 = nodeCoords[to];
        ctx.beginPath();
        ctx.moveTo(p1.x, p1.y);
        ctx.lineTo(p2.x, p2.y);
        ctx.strokeStyle = '#1e293b';
        ctx.lineWidth = 2;
        ctx.setLineDash([4, 4]);
        ctx.stroke();
        ctx.setLineDash([]);
      });

      // Update & draw particles
      const particles = particlesRef.current;
      for (let i = particles.length - 1; i >= 0; i--) {
        const p = particles[i];
        p.progress += p.speed;
        if (p.progress >= 1) {
          particles.splice(i, 1);
          continue;
        }

        const p1 = nodeCoords[p.from];
        const p2 = nodeCoords[p.to];
        if (p1 && p2) {
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
      }

      // Draw nodes
      Object.keys(nodeCoords).forEach((key) => {
        const node = nodeCoords[key];
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

        ctx.font = 'bold 11px sans-serif';
        ctx.fillStyle = '#f8fafc';
        ctx.textAlign = 'center';
        ctx.fillText(node.label, node.x, node.y - 28);

        ctx.font = '9px monospace';
        ctx.fillStyle = '#94a3b8';
        ctx.fillText('TCP :' + node.port, node.x, node.y + 35);
      });

      animId = requestAnimationFrame(render);
    };

    animId = requestAnimationFrame(render);
    return () => cancelAnimationFrame(animId);
  }, []);

  return (
    <div className="min-h-screen flex flex-col font-sans">
      {/* Top Header */}
      <header className="border-b border-slate-800 bg-slate-950/80 backdrop-blur sticky top-0 z-50 px-6 py-3">
        <div className="max-w-7xl mx-auto flex flex-col md:flex-row items-center justify-between gap-4">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-cyan-500 to-indigo-600 flex items-center justify-center shadow-lg shadow-cyan-500/20 text-white font-bold text-xl">
              ⛓️
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h1 className="text-lg font-bold text-white tracking-wide">
                  P2P Blockchain Network Simulator
                </h1>
                <span className="px-2 py-0.5 text-xs font-semibold rounded bg-cyan-950 text-cyan-400 border border-cyan-800">
                  Next.js + Java 21 Core
                </span>
              </div>
              <p className="text-xs text-slate-400">
                Đồ Án Môn Học: <span className="text-slate-300 font-medium">Lập Trình Mạng</span> | HUTECH
              </p>
            </div>
          </div>

          <div className="flex items-center gap-3 text-xs">
            <div className="hidden lg:flex items-center gap-2 bg-slate-900 border border-slate-800 px-3 py-1.5 rounded-lg text-slate-300">
              <span className="text-slate-400">Thành viên:</span>
              <span className="font-semibold text-amber-400">Hoàng Nhật (2380601549)</span> •
              <span className="font-semibold text-cyan-400">Trung Lương (2380601305)</span> •
              <span className="font-semibold text-purple-400">Quang Minh (2380601342)</span>
            </div>
            <div
              className={`flex items-center gap-2 px-3 py-1.5 rounded-lg border font-medium ${
                isConnected
                  ? 'bg-emerald-950/80 border-emerald-700/50 text-emerald-400'
                  : 'bg-rose-950/80 border-rose-700/50 text-rose-400'
              }`}
            >
              <span
                className={`w-2 h-2 rounded-full ${
                  isConnected ? 'bg-emerald-400 animate-ping' : 'bg-rose-400'
                }`}
              />
              <span>{isConnected ? 'Java Backend: Online' : 'Java Backend: Disconnected'}</span>
            </div>
          </div>
        </div>
      </header>

      {/* Main Container */}
      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 py-6 space-y-6">
        {/* Control Actions Bar */}
        <section className="bg-slate-900/90 border border-slate-800 rounded-2xl p-5 shadow-xl backdrop-blur">
          <div className="flex flex-col md:flex-row items-center justify-between gap-4 mb-4">
            <div>
              <h2 className="text-base font-bold text-white flex items-center gap-2">
                <span>⚡ Trung Tâm Điều Khiển Mạng P2P</span>
                <span className="text-xs font-normal text-slate-400">
                  (Phát tán TCP Socket Gossip & Khai thác Đồng thuận PoW)
                </span>
              </h2>
            </div>
            <div className="flex items-center gap-2">
              <button
                onClick={handleReset}
                className="px-3 py-1.5 text-xs font-medium bg-slate-800 hover:bg-slate-700 text-slate-300 rounded-lg border border-slate-700 transition flex items-center gap-1.5"
              >
                <RefreshCw className="w-3.5 h-3.5" />
                Reset Genesis
              </button>
              <button
                onClick={fetchData}
                className="px-3 py-1.5 text-xs font-medium bg-cyan-950 hover:bg-cyan-900 text-cyan-300 rounded-lg border border-cyan-800 transition flex items-center gap-1.5"
              >
                <RefreshCw className="w-3.5 h-3.5" />
                Làm Mới
              </button>
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3">
            {/* 1. Send Tx */}
            <button
              onClick={handleSendTx}
              className="group relative bg-gradient-to-br from-purple-950/60 to-slate-900 border border-purple-800/50 hover:border-purple-500/80 rounded-xl p-4 text-left transition hover:shadow-lg hover:shadow-purple-950/50"
            >
              <div className="flex items-center justify-between mb-2">
                <Send className="w-6 h-6 text-purple-400" />
                <span className="text-[10px] font-semibold uppercase px-2 py-0.5 rounded bg-purple-900/60 text-purple-300 border border-purple-700/50">
                  Gossip TCP
                </span>
              </div>
              <div className="text-sm font-bold text-white group-hover:text-purple-300 transition">
                1. Gửi Giao Dịch
              </div>
              <p className="text-xs text-slate-400 mt-1">
                Node C (Ví) phát tán Tx qua Node B rồi tới Node A vào Mempool.
              </p>
            </button>

            {/* 2. Mine Block */}
            <button
              onClick={handleMine}
              disabled={isMining}
              className="group relative bg-gradient-to-br from-amber-950/60 to-slate-900 border border-amber-800/50 hover:border-amber-500/80 rounded-xl p-4 text-left transition hover:shadow-lg hover:shadow-amber-950/50"
            >
              <div className="flex items-center justify-between mb-2">
                <Pickaxe className="w-6 h-6 text-amber-400" />
                <span className="text-[10px] font-semibold uppercase px-2 py-0.5 rounded bg-amber-900/60 text-amber-300 border border-amber-700/50">
                  Proof-of-Work
                </span>
              </div>
              <div className="text-sm font-bold text-white group-hover:text-amber-300 transition flex items-center gap-2">
                <span>2. Khai Thác Block</span>
                {isMining && <RefreshCw className="w-3.5 h-3.5 animate-spin text-amber-400" />}
              </div>
              <p className="text-xs text-slate-400 mt-1">
                Node A chạy Virtual Threads tìm Nonce, đóng block và broadcast TCP.
              </p>
            </button>

            {/* 3. Connect Node D */}
            <button
              onClick={handleConnectNodeD}
              className="group relative bg-gradient-to-br from-emerald-950/60 to-slate-900 border border-emerald-800/50 hover:border-emerald-500/80 rounded-xl p-4 text-left transition hover:shadow-lg hover:shadow-emerald-950/50"
            >
              <div className="flex items-center justify-between mb-2">
                <Network className="w-6 h-6 text-emerald-400" />
                <span className="text-[10px] font-semibold uppercase px-2 py-0.5 rounded bg-emerald-900/60 text-emerald-300 border border-emerald-700/50">
                  Nakamoto Sync
                </span>
              </div>
              <div className="text-sm font-bold text-white group-hover:text-emerald-300 transition">
                3. Kết Nối Node D
              </div>
              <p className="text-xs text-slate-400 mt-1">
                Khởi động Node D (Port 8004), gửi TCP Handshake & Sync toàn bộ Ledger.
              </p>
            </button>

            {/* 4. Tamper Attack */}
            <button
              onClick={handleTamper}
              className="group relative bg-gradient-to-br from-rose-950/60 to-slate-900 border border-rose-800/50 hover:border-rose-500/80 rounded-xl p-4 text-left transition hover:shadow-lg hover:shadow-rose-950/50"
            >
              <div className="flex items-center justify-between mb-2">
                <ShieldAlert className="w-6 h-6 text-rose-400" />
                <span className="text-[10px] font-semibold uppercase px-2 py-0.5 rounded bg-rose-900/60 text-rose-300 border border-rose-700/50">
                  Audit Check
                </span>
              </div>
              <div className="text-sm font-bold text-white group-hover:text-rose-300 transition">
                4. Thử Tấn Công Giả Mạo
              </div>
              <p className="text-xs text-slate-400 mt-1">
                Sửa lén Tx trong Block #1. Kiểm tra tính toàn vẹn Merkle Root & Hash.
              </p>
            </button>
          </div>
        </section>

        {/* Tamper Alert */}
        {status?.isTampered && (
          <div className="bg-rose-950/90 border-2 border-rose-600 text-rose-200 p-4 rounded-xl shadow-2xl flex items-start gap-4">
            <AlertTriangle className="w-8 h-8 text-rose-400 flex-shrink-0 mt-0.5" />
            <div className="flex-1">
              <div className="font-bold text-white text-base">
                CẢNH BÁO TẤN CÔNG GIẢ MẠO ĐÃ ĐƯỢC PHÁT HIỆN!
              </div>
              <div className="text-xs text-rose-300 mt-1 font-mono">
                Khối #{status.tamperedBlockIndex} bị vi phạm: {status.tamperReason}
              </div>
              <div className="mt-2 text-xs text-rose-400">
                Mật mã học SHA-256 + Merkle Tree giúp phát hiện sự sai lệch ngay lập tức. Bấm "Reset Genesis" để khôi phục.
              </div>
            </div>
          </div>
        )}

        {/* P2P Topology + Nodes */}
        <section className="grid grid-cols-1 lg:grid-cols-12 gap-6">
          <div className="lg:col-span-7 bg-slate-900/90 border border-slate-800 rounded-2xl p-5 shadow-xl flex flex-col">
            <div className="flex items-center justify-between mb-3">
              <div className="flex items-center gap-2">
                <Activity className="w-5 h-5 text-cyan-400" />
                <h2 className="text-sm font-bold text-white">Sơ Đồ Mạng P2P TCP Sockets (Topology)</h2>
              </div>
              <span className="text-xs text-cyan-400 font-mono">4-Byte Prefix + JSON Frame</span>
            </div>

            <div className="relative w-full h-[320px] bg-slate-950 rounded-xl border border-slate-800/80 overflow-hidden flex items-center justify-center">
              <canvas ref={canvasRef} width={650} height={320} className="w-full h-full block" />
              <div className="absolute bottom-2 left-3 text-[11px] text-slate-500 flex items-center gap-3">
                <span className="flex items-center gap-1">
                  <span className="w-2 h-2 rounded-full bg-amber-400 inline-block" /> Node A (Miner)
                </span>
                <span className="flex items-center gap-1">
                  <span className="w-2 h-2 rounded-full bg-cyan-400 inline-block" /> Node B (Relay)
                </span>
                <span className="flex items-center gap-1">
                  <span className="w-2 h-2 rounded-full bg-purple-400 inline-block" /> Node C (Wallet)
                </span>
                <span className="flex items-center gap-1">
                  <span className="w-2 h-2 rounded-full bg-emerald-400 inline-block" /> Node D (Sync)
                </span>
              </div>
            </div>
            <p className="text-xs text-slate-400 mt-3 italic">
              * Các hạt màu chuyển động biểu diễn luồng gói tin TCP thực tế giữa các cổng 8001, 8002, 8003 và 8004.
            </p>
          </div>

          {/* Node Metrics Cards */}
          <div className="lg:col-span-5 flex flex-col gap-3">
            <div className="text-sm font-bold text-white flex items-center justify-between">
              <span className="flex items-center gap-1.5">
                <Cpu className="w-4 h-4 text-cyan-400" /> Trạng Thái Các Nút (Peer Nodes)
              </span>
              <span className="text-xs bg-slate-800 text-slate-300 px-2 py-0.5 rounded border border-slate-700">
                {status?.nodes.length || 0} Nodes Online
              </span>
            </div>

            <div className="grid grid-cols-1 gap-2.5 flex-1 overflow-y-auto max-h-[340px]">
              {status?.nodes.map((node) => {
                let badgeClass = 'bg-amber-950 text-amber-400 border-amber-800';
                let dotClass = 'bg-amber-400';
                if (node.id.includes('Node-B')) {
                  badgeClass = 'bg-cyan-950 text-cyan-400 border-cyan-800';
                  dotClass = 'bg-cyan-400';
                } else if (node.id.includes('Node-C')) {
                  badgeClass = 'bg-purple-950 text-purple-400 border-purple-800';
                  dotClass = 'bg-purple-400';
                } else if (node.id.includes('Node-D')) {
                  badgeClass = 'bg-emerald-950 text-emerald-400 border-emerald-800';
                  dotClass = 'bg-emerald-400';
                }

                return (
                  <div
                    key={node.id}
                    className="bg-slate-950 border border-slate-800 rounded-xl p-3 flex flex-col justify-between hover:border-slate-700 transition"
                  >
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-2">
                        <span className={`w-2.5 h-2.5 rounded-full ${dotClass}`} />
                        <span className="font-bold text-white text-xs">{node.id}</span>
                      </div>
                      <span className={`text-[10px] px-2 py-0.5 rounded font-mono border ${badgeClass}`}>
                        Port {node.port}
                      </span>
                    </div>
                    <div className="grid grid-cols-3 gap-2 mt-2 pt-2 border-t border-slate-900 text-center">
                      <div>
                        <div className="text-[10px] text-slate-500">Độ Cao</div>
                        <div className="text-xs font-bold text-cyan-400 font-mono">
                          {node.chainHeight}
                        </div>
                      </div>
                      <div>
                        <div className="text-[10px] text-slate-500">Mempool</div>
                        <div className="text-xs font-bold text-amber-400 font-mono">
                          {node.mempoolCount} tx
                        </div>
                      </div>
                      <div>
                        <div className="text-[10px] text-slate-500">Peers</div>
                        <div className="text-xs font-bold text-emerald-400 font-mono">
                          {node.peerCount} kết nối
                        </div>
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        </section>

        {/* Blockchain Ledger Explorer */}
        <section className="bg-slate-900/90 border border-slate-800 rounded-2xl p-5 shadow-xl">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 mb-4">
            <div>
              <h2 className="text-base font-bold text-white flex items-center gap-2">
                <Layers className="w-5 h-5 text-indigo-400" /> Sổ Cái Phân Tán (Blockchain Ledger Explorer)
              </h2>
              <p className="text-xs text-slate-400">
                Các khối được liên kết chặt chẽ bằng SHA-256 Hash con trỏ (Previous Hash) và Merkle Root
              </p>
            </div>
            <div className="text-xs font-mono text-slate-400 flex items-center gap-2">
              <span>Tổng Chiều Cao:</span>
              <span className="font-bold text-cyan-400 bg-cyan-950 px-2 py-0.5 rounded border border-cyan-800">
                {chain.length} Blocks
              </span>
            </div>
          </div>

          <div className="flex gap-4 overflow-x-auto pb-4 pt-2">
            {chain.map((block, idx) => {
              const isGenesis = block.index === 0;
              const isCorrupted = block.isCompromised;

              const borderColor = isCorrupted
                ? 'border-rose-500 bg-rose-950/20'
                : isGenesis
                ? 'border-cyan-800 bg-slate-950'
                : 'border-slate-800 bg-slate-950';

              return (
                <div key={block.index} className="flex items-center">
                  <div
                    className={`w-72 flex-shrink-0 border ${borderColor} rounded-xl p-4 shadow-lg flex flex-col justify-between`}
                  >
                    <div>
                      <div className="flex items-center justify-between pb-2 border-b border-slate-800">
                        <span className="font-bold text-white text-xs">Block #{block.index}</span>
                        {isCorrupted ? (
                          <span className="px-2 py-0.5 text-[10px] rounded bg-rose-900 text-rose-300 font-bold">
                            TAMPERED
                          </span>
                        ) : isGenesis ? (
                          <span className="px-2 py-0.5 text-[10px] rounded bg-cyan-900 text-cyan-300 font-bold">
                            GENESIS
                          </span>
                        ) : (
                          <span className="px-2 py-0.5 text-[10px] rounded bg-emerald-900 text-emerald-300 font-bold">
                            VALID
                          </span>
                        )}
                      </div>

                      <div className="mt-2.5 space-y-1.5 text-[11px] font-mono">
                        <div>
                          <span className="text-slate-500 block text-[9px] uppercase">Hash:</span>
                          <span className="text-cyan-300 font-bold truncate block" title={block.hash}>
                            {block.shortHash || block.hash.substring(0, 16) + '...'}
                          </span>
                        </div>
                        <div>
                          <span className="text-slate-500 block text-[9px] uppercase">Prev Hash:</span>
                          <span className="text-slate-400 truncate block" title={block.previousHash}>
                            {block.previousHash.length > 16
                              ? block.previousHash.substring(0, 16) + '...'
                              : block.previousHash}
                          </span>
                        </div>
                        <div className="grid grid-cols-2 gap-2 pt-1">
                          <div>
                            <span className="text-slate-500 text-[9px] uppercase block">Nonce:</span>
                            <span className="text-amber-400 font-bold">{block.nonce.toLocaleString()}</span>
                          </div>
                          <div>
                            <span className="text-slate-500 text-[9px] uppercase block">Diff:</span>
                            <span className="text-purple-400 font-bold">{block.difficultyBits}</span>
                          </div>
                        </div>
                      </div>
                    </div>

                    <div className="mt-3 pt-2 border-t border-slate-900">
                      <div className="text-[10px] text-slate-400 font-semibold mb-1">
                        Giao dịch ({block.transactions.length}):
                      </div>
                      <div className="max-h-24 overflow-y-auto space-y-1 pr-1">
                        {block.transactions.map((tx) => (
                          <div
                            key={tx.txId}
                            className="bg-slate-900 px-2 py-1 rounded text-[10px] border border-slate-800 flex items-center justify-between"
                          >
                            <span className="text-slate-300 truncate max-w-[120px]">
                              {tx.sender} → {tx.recipient}
                            </span>
                            <span className="text-amber-400 font-bold ml-1">{tx.amount} COIN</span>
                          </div>
                        ))}
                      </div>
                    </div>
                  </div>

                  {idx < chain.length - 1 && (
                    <div className="flex items-center justify-center px-2 text-cyan-500">
                      <ArrowRight className="w-5 h-5" />
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        </section>

        {/* Live Terminal Log Stream */}
        <section className="bg-slate-950 border border-slate-800 rounded-2xl p-5 shadow-xl font-mono">
          <div className="flex items-center justify-between mb-3 border-b border-slate-800 pb-3">
            <div className="flex items-center gap-2">
              <span className="w-3 h-3 rounded-full bg-rose-500 inline-block" />
              <span className="w-3 h-3 rounded-full bg-amber-500 inline-block" />
              <span className="w-3 h-3 rounded-full bg-emerald-500 inline-block" />
              <span className="text-xs font-bold text-slate-300 ml-2">
                Console Nhật Ký TCP Socket & Đồng Thuận (Live Stream)
              </span>
            </div>
            <label className="text-[11px] text-slate-400 flex items-center gap-1 cursor-pointer">
              <input
                type="checkbox"
                checked={autoScroll}
                onChange={(e) => setAutoScroll(e.target.checked)}
                className="rounded bg-slate-900 border-slate-700 text-cyan-500 focus:ring-0"
              />
              Auto-scroll
            </label>
          </div>

          <div ref={terminalRef} className="h-48 overflow-y-auto text-xs space-y-1 select-text">
            {status?.logs.map((line, i) => {
              let color = 'text-slate-300';
              if (line.includes('ATTACK') || line.includes('❌') || line.includes('Error')) {
                color = 'text-rose-400 font-bold';
              } else if (line.includes('MINED') || line.includes('PoW')) {
                color = 'text-amber-300';
              } else if (line.includes('TX_BROADCAST')) {
                color = 'text-purple-300';
              } else if (line.includes('TCP') || line.includes('HANDSHAKE')) {
                color = 'text-cyan-300';
              } else if (line.includes('SYNC')) {
                color = 'text-emerald-300';
              }
              return (
                <div key={i} className={color}>
                  {line}
                </div>
              );
            })}
          </div>
        </section>
      </main>

      {/* Footer */}
      <footer className="border-t border-slate-800 py-4 text-center text-xs text-slate-500">
        Đồ Án Lập Trình Mạng • Proof-of-Work P2P Blockchain Simulator • Java 21 & Next.js
      </footer>
    </div>
  );
}
