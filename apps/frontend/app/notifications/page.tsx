'use client';

import Link from 'next/link';
import { useCallback, useEffect, useState } from 'react';
import { AppShell } from '@/components/AppShell';
import { useAuth } from '@/components/AuthProvider';
import { api, type Notification, type Page } from '@/lib/api';

export default function NotificationsPage() {
  const { user, loading: authLoading } = useAuth();
  const [items, setItems] = useState<Notification[]>([]);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  const load = useCallback(async () => {
    if (!user) { setLoading(false); return; }
    setLoading(true);
    setError('');
    try { setItems((await api<Page<Notification>>('/notifications?page=0&size=50')).data); }
    catch (loadError) { setError(loadError instanceof Error ? loadError.message : 'Unable to load notifications.'); }
    finally { setLoading(false); }
  }, [user]);
  useEffect(() => { if (!authLoading) void load(); }, [authLoading, load]);

  async function markRead(id: string) {
    setError('');
    try {
      const updated = await api<Notification>(`/notifications/${id}/read`, { method: 'PATCH' });
      setItems((existing) => existing.map((item) => item.id === updated.id ? updated : item));
    } catch (actionError) {
      setError(actionError instanceof Error ? actionError.message : 'Unable to mark notification read.');
    }
  }

  return (
    <AppShell>
      <p className="text-sm font-semibold uppercase tracking-wider text-brand-200">Your activity</p>
      <h1 className="mt-2 text-3xl font-bold text-white">Notifications</h1>
      {error && <p className="mt-5 rounded-xl border border-rose-500/40 bg-rose-500/10 p-4 text-rose-200" role="alert">{error}</p>}
      {!authLoading && !user && <p className="mt-6 rounded-xl border border-slate-800 bg-slate-900 p-5">Sign in to see notifications. <Link className="text-brand-200 underline" href="/login">Sign in</Link></p>}
      {loading && user ? <p aria-live="polite" className="py-12 text-center text-slate-400">Loading notifications…</p> :
        items.length ? <ul className="mt-7 space-y-3">{items.map((item) => <li className={`rounded-2xl border p-5 ${item.readAt ? 'border-slate-800 bg-slate-900' : 'border-brand-500/40 bg-slate-900'}`} key={item.id}>
          <div className="flex flex-wrap items-start justify-between gap-4"><div><p className="text-xs font-semibold uppercase tracking-wider text-brand-200">{item.type.replaceAll('_', ' ')}</p><h2 className="mt-1 font-semibold text-white">{item.title}</h2><p className="mt-2 text-sm leading-6 text-slate-300">{item.message}</p><p className="mt-2 text-xs text-slate-500">{new Date(item.createdAt).toLocaleString()}</p></div>
            <div className="flex gap-3">{item.matchId && <Link className="text-sm text-brand-200 underline" href={`/matches/${item.matchId}`}>View match</Link>}{item.relatedReportId && <Link className="text-sm text-brand-200 underline" href={`/reports/${item.relatedReportId}`}>View report</Link>}{!item.readAt && <button className="text-sm text-slate-300 underline" onClick={() => markRead(item.id)} type="button">Mark read</button>}</div>
          </div>
        </li>)}</ul> : !loading && user && !error && <div className="mt-7 rounded-2xl border border-dashed border-slate-700 p-10 text-center text-slate-400">You’re all caught up. New match and verification updates will appear here.</div>}
    </AppShell>
  );
}
