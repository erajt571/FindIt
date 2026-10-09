'use client';

import Link from 'next/link';
import { useCallback, useEffect, useState } from 'react';
import { AppShell } from '@/components/AppShell';
import { ReportCard } from '@/components/ReportCard';
import { useAuth } from '@/components/AuthProvider';
import { api, type Page, type Report } from '@/lib/api';

export default function MyReportsPage() {
  const { user, loading: authLoading } = useAuth();
  const [reports, setReports] = useState<Report[]>([]);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);
  const load = useCallback(async () => {
    if (!user) { setLoading(false); return; }
    setLoading(true);
    try { setReports((await api<Page<Report>>('/reports/mine?page=0&size=50')).data); }
    catch (loadError) { setError(loadError instanceof Error ? loadError.message : 'Unable to load your reports.'); }
    finally { setLoading(false); }
  }, [user]);
  useEffect(() => { if (!authLoading) void load(); }, [authLoading, load]);
  return (
    <AppShell>
      <div className="flex flex-wrap items-end justify-between gap-4"><div><p className="text-sm font-semibold uppercase tracking-wider text-brand-200">Your listings</p><h1 className="mt-2 text-3xl font-bold text-white">My reports</h1></div><Link className="rounded-xl bg-brand-500 px-4 py-2.5 font-semibold text-white" href="/reports/new">Create report</Link></div>
      {error && <p className="mt-5 rounded-xl border border-rose-500/40 bg-rose-500/10 p-4 text-rose-200" role="alert">{error}</p>}
      {!authLoading && !user && <p className="mt-6 rounded-xl border border-slate-800 bg-slate-900 p-5">Sign in to manage your reports. <Link className="text-brand-200 underline" href="/login">Sign in</Link></p>}
      {loading && user ? <p aria-live="polite" className="py-12 text-center text-slate-400">Loading your reports…</p> :
        reports.length ? <div className="mt-7 grid gap-4 md:grid-cols-2">{reports.map((report) => <ReportCard key={report.id} report={report} />)}</div> :
          user && !error && <div className="mt-7 rounded-2xl border border-dashed border-slate-700 p-10 text-center text-slate-400">No reports yet. <Link className="text-brand-200 underline" href="/reports/new">Create one</Link>.</div>}
    </AppShell>
  );
}
