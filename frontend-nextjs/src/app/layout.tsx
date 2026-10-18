import type { Metadata } from 'next';
import './globals.css';

export const metadata: Metadata = {
  title: 'P2P Blockchain Network Simulator - Lập Trình Mạng',
  description: 'Mô phỏng mạng P2P Proof-of-Work Blockchain bằng Next.js và lõi Java 21 Virtual Threads',
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="vi" className="dark">
      <body className="min-h-screen bg-[#090d16] text-slate-100 antialiased">
        {children}
      </body>
    </html>
  );
}
