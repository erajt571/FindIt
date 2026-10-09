import './globals.css';
import type { Metadata } from 'next';

export const metadata: Metadata = {
  title: 'FindIt',
  description: 'AI-powered campus lost and found platform',
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}
