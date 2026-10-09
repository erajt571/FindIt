'use client';

import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useState } from 'react';
import { useAuth } from '@/components/AuthProvider';

export function AppShell({ children }: { children: React.ReactNode }) {
  const { user, loading, error: authError, logout } = useAuth();
  const router = useRouter();
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
    <div className="min-h-screen bg-slate-950 text-slate-100">
      <header className="border-b border-slate-800 bg-slate-950/95">
        <nav aria-label="Main navigation" className="mx-auto flex max-w-7xl flex-wrap items-center justify-between gap-4 px-5 py-4">
          <Link className="flex items-center gap-3 font-bold text-white" href="/">
            <span aria-hidden="true" className="grid h-9 w-9 place-items-center rounded-xl bg-brand-500">F</span>
            FindIt
          </Link>
          <div className="flex flex-wrap items-center gap-x-5 gap-y-2 text-sm text-slate-300">
            <Link className="hover:text-white" href="/reports">Browse</Link>
            {user && <Link className="hover:text-white" href="/dashboard">Dashboard</Link>}
            {user && <Link className="hover:text-white" href="/my-reports">My reports</Link>}
            {user && <Link className="hover:text-white" href="/reports/new">New report</Link>}
            {user && <Link className="hover:text-white" href="/notifications">Notifications</Link>}
            {user?.role === 'ADMIN' && <Link className="hover:text-white" href="/admin">Moderation</Link>}
            {loading ? <span aria-live="polite">Loading account…</span> : user ? (
              <>
                <span className="hidden text-slate-500 sm:inline">{user.displayName}</span>
                <button className="rounded-lg border border-slate-700 px-3 py-1.5 hover:border-slate-500" onClick={handleLogout} type="button">
                  Sign out
                </button>
              </>
            ) : (
              <>
                <Link className="hover:text-white" href="/login">Sign in</Link>
                <Link className="rounded-lg bg-brand-500 px-3 py-1.5 font-semibold text-white hover:bg-brand-400" href="/register">Create account</Link>
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
      <main className="mx-auto max-w-7xl px-5 py-8">{children}</main>
      <footer className="mx-auto max-w-7xl border-t border-slate-800 px-5 py-6 text-sm text-slate-500">
        FindIt campus lost &amp; found · Match suggestions are not proof of ownership.
      </footer>
    </div>
  );
}
