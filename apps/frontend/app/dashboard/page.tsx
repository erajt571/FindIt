'use client';

import Link from 'next/link';
import { useCallback, useEffect, useState } from 'react';
import { AppShell } from '@/components/AppShell';
import { ReportCard } from '@/components/ReportCard';
import { useAuth } from '@/components/AuthProvider';
import { api, type DashboardSummary, type Page, type Report } from '@/lib/api';

export default function DashboardPage() {
  const { user, loading: authLoading } = useAuth();
  const [summary, setSummary] = useState<DashboardSummary | null>(null);
  const [reports, setReports] = useState<Report[]>([]);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  const load = useCallback(async () => {
    if (!user) { setLoading(false); return; }
    setLoading(true);
    setError('');
    try {
      const [metrics, mine] = await Promise.all([
        api<DashboardSummary>('/dashboard/summary'),
        api<Page<Report>>('/reports/mine?page=0&size=5'),
      ]);
      setSummary(metrics);
      setReports(mine.data);
    } catch (loadError) {
      setError(loadError instanceof Error ? loadError.message : 'Unable to load your dashboard.');
    } finally {
      setLoading(false);
    }
  }, [user]);

  useEffect(() => { if (!authLoading) void load(); }, [authLoading, load]);

  return (
    <AppShell>
      <div className="flex flex-wrap items-end justify-between gap-4">
        <div><p className="text-sm font-semibold uppercase tracking-wider text-brand-200">Personal workspace</p><h1 className="mt-2 text-3xl font-bold text-white">Welcome{user ? `, ${user.displayName}` : ''}</h1></div>
        <Link className="rounded-xl bg-brand-500 px-4 py-2.5 font-semibold text-white hover:bg-brand-400" href="/reports/new">Create report</Link>
      </div>
      {!authLoading && !user && <div className="mt-7 rounded-2xl border border-slate-800 bg-slate-900 p-6">Sign in to view your reports and activity. <Link className="ml-1 text-brand-200 underline" href="/login">Sign in</Link></div>}
      {error && <p className="mt-6 rounded-xl border border-rose-500/40 bg-rose-500/10 p-4 text-rose-200" role="alert">{error}</p>}
      {loading && user ? <p aria-live="polite" className="py-12 text-center text-slate-400">Loading your dashboard…</p> : summary && (
        <>
          <section aria-label="Dashboard metrics" className="mt-7 grid gap-4 sm:grid-cols-2 xl:grid-cols-5">
            {[
              ['Your active reports', summary.activeReports],
              ['Resolved by you', summary.resolvedReports],
              ['Suggested matches', summary.suggestedMatches],
              ['Unread notifications', summary.unreadNotifications],
              ['Campus active reports', summary.campusActiveReports],
            ].map(([label, value]) => <div className="rounded-2xl border border-slate-800 bg-slate-900 p-5" key={label}>
              <p className="text-3xl font-bold text-white">{value}</p><p className="mt-2 text-sm text-slate-400">{label}</p>
            </div>)}
          </section>
          <section className="mt-10">
            <div className="mb-4 flex items-center justify-between gap-3"><h2 className="text-2xl font-bold text-white">My recent reports</h2><Link className="text-sm text-brand-200 hover:text-white" href="/my-reports">View all</Link></div>
            {reports.length ? <div className="grid gap-4 md:grid-cols-2">{reports.map((report) => <ReportCard key={report.id} report={report} />)}</div> :
              <div className="rounded-2xl border border-dashed border-slate-700 p-8 text-center"><p className="text-slate-300">You have not created a report yet.</p><Link className="mt-3 inline-block text-sm text-brand-200 underline" href="/reports/new">Create your first report</Link></div>}
          </section>
        </>
      )}
    </AppShell>
  );
}
