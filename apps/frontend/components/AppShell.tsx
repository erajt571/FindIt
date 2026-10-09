'use client';

import Link from 'next/link';
import { usePathname, useRouter } from 'next/navigation';
import { useState } from 'react';
import { useAuth } from '@/components/AuthProvider';

export function AppShell({ children }: { children: React.ReactNode }) {
  const { user, loading, error: authError, logout } = useAuth();
  const router = useRouter();
  const pathname = usePathname();
  const isHome = pathname === '/';
  const [actionError, setActionError] = useState('');

  async function handleLogout() {
    setActionError('');
    try {
      await logout();
      router.push('/');
    } catch (error) {
      setActionError(error instanceof Error ? error.message : 'Unable to sign out.');
    }
  }

  return (
    <div className={isHome ? 'min-h-screen bg-white text-[#172d55]' : 'min-h-screen bg-slate-950 text-slate-100'}>
      <header className={isHome ? 'border-b border-[#e1e7ef] bg-white' : 'border-b border-slate-800 bg-slate-950/95'}>
        <nav aria-label="Main navigation" className="mx-auto flex max-w-7xl flex-wrap items-center justify-between gap-4 px-5 py-2.5">
          <Link className={`flex items-center gap-2 font-bold ${isHome ? 'text-[#172d55]' : 'text-white'}`} href="/">
            <span aria-hidden="true" className={`grid h-8 w-8 place-items-center rounded-[10px] ${isHome ? 'bg-[#172d55]' : 'bg-brand-500'} text-white`}>
              {isHome ? <svg className="h-[18px] w-[18px]" fill="none" viewBox="0 0 24 24"><circle cx="10.5" cy="10.5" r="6.5" stroke="currentColor" strokeWidth="2" /><path d="m15.5 15.5 4 4" stroke="currentColor" strokeLinecap="round" strokeWidth="2" /></svg> : 'F'}
            </span>
            FindIt
          </Link>
          <div className={`flex flex-wrap items-center gap-x-6 gap-y-2 text-[13px] ${isHome ? 'text-[#65738b]' : 'text-slate-300'}`}>
            {isHome ? (
              <>
                <Link className="order-1 hover:text-[#172d55]" href="/dashboard">Dashboard</Link>
                <Link className="order-2 hover:text-[#172d55]" href="/reports">Browse</Link>
                <Link className="order-3 hover:text-[#172d55]" href="/my-reports">My Reports</Link>
                <Link className="order-4 hover:text-[#172d55]" href="/notifications">Notifications</Link>
              </>
            ) : (
              <>
                <Link className="hover:text-white" href="/reports">Browse</Link>
                {user && <Link className="hover:text-white" href="/dashboard">Dashboard</Link>}
                {user && <Link className="hover:text-white" href="/my-reports">My reports</Link>}
                {user && <Link className="hover:text-white" href="/reports/new">New report</Link>}
                {user && <Link className="hover:text-white" href="/notifications">Notifications</Link>}
                {user?.role === 'ADMIN' && <Link className="hover:text-white" href="/admin">Moderation</Link>}
              </>
            )}
            {loading ? <span aria-live="polite">Loading account…</span> : user ? (
              <>
                <span className="hidden text-slate-500 sm:inline">{user.displayName}</span>
                <Link className={isHome ? 'rounded-[10px] bg-[#009b98] px-3.5 py-2 font-semibold text-white shadow-sm hover:bg-[#008582]' : 'rounded-lg bg-brand-500 px-3 py-1.5 font-semibold text-white hover:bg-brand-400'} href="/reports/new">＋ Report an Item</Link>
                <button className={`rounded-[10px] px-3.5 py-2 shadow-sm ${isHome ? 'border border-[#d6e0eb] bg-[#f8fafc] text-[#172d55] hover:bg-white' : 'border border-slate-700 hover:border-slate-500'}`} onClick={handleLogout} type="button">
                  Sign out
                </button>
              </>
            ) : (
              <>
                {isHome && <Link className="rounded-[10px] bg-[#009b98] px-3.5 py-2 font-semibold text-white shadow-sm hover:bg-[#008582]" href="/reports/new">＋ Report an Item</Link>}
                <Link className={isHome ? 'rounded-[10px] border border-[#d6e0eb] bg-[#f8fafc] px-3.5 py-2 text-[#172d55] shadow-sm hover:bg-white' : 'hover:text-white'} href="/login">Sign in</Link>
                {!isHome && <Link className="rounded-lg bg-brand-500 px-3 py-1.5 font-semibold text-white hover:bg-brand-400" href="/register">Create account</Link>}
              </>
            )}
          </div>
        </nav>
      </header>
      {(authError || actionError) && <div className="mx-auto max-w-7xl px-5 pt-5">
        <p className="rounded-lg border border-rose-500/40 bg-rose-500/10 p-3 text-sm text-rose-200" role="alert">
          {actionError || authError}
        </p>
      </div>}
      <main className={isHome ? '' : 'mx-auto max-w-7xl px-5 py-8'}>{children}</main>
      <footer className={isHome ? 'mx-auto max-w-7xl border-t border-[#e1e7ef] px-5 py-6 text-sm text-[#758198]' : 'mx-auto max-w-7xl border-t border-slate-800 px-5 py-6 text-sm text-slate-500'}>
        FindIt campus lost &amp; found · Match suggestions are not proof of ownership.
      </footer>
    </div>
  );
}
