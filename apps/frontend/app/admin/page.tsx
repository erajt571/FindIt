'use client';

import { FormEvent, useCallback, useEffect, useState } from 'react';
import { AppShell } from '@/components/AppShell';
import { useAuth } from '@/components/AuthProvider';
import { api, type Page, type Report } from '@/lib/api';

export default function AdminPage() {
  const { user, loading: authLoading } = useAuth();
  const [reports, setReports] = useState<Report[]>([]);
  const [reasons, setReasons] = useState<Record<string, string>>({});
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');
  const [loading, setLoading] = useState(true);

  const load = useCallback(async () => {
    if (user?.role !== 'ADMIN') { setLoading(false); return; }
    setLoading(true);
    setError('');
    try { setReports((await api<Page<Report>>('/admin/reports?page=0&size=50')).data); }
    catch (loadError) { setError(loadError instanceof Error ? loadError.message : 'Unable to load the moderation queue.'); }
    finally { setLoading(false); }
  }, [user?.role]);
  useEffect(() => { if (!authLoading) void load(); }, [authLoading, load]);

  async function remove(id: string, event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError('');
    setNotice('');
    const reason = reasons[id]?.trim();
    if (!reason || reason.length < 5) { setError('Provide a moderation reason of at least five characters.'); return; }
    try {
      await api(`/admin/reports/${id}/remove`, { method: 'POST', body: JSON.stringify({ reason }) });
      setNotice('Report removed and moderation action recorded.');
      await load();
    } catch (actionError) { setError(actionError instanceof Error ? actionError.message : 'Unable to remove the report.'); }
  }

  return (
    <AppShell>
      <p className="text-sm font-semibold uppercase tracking-wider text-brand-200">Admin only</p>
      <h1 className="mt-2 text-3xl font-bold text-white">Moderation queue</h1>
      <p className="mt-2 max-w-2xl text-sm leading-6 text-slate-400">Review active and verification-pending listings. Every removal requires a reason and is retained in the audit log.</p>
      {user && user.role !== 'ADMIN' && <p className="mt-6 rounded-xl border border-rose-500/40 bg-rose-500/10 p-4 text-rose-200" role="alert">This area is restricted to administrators.</p>}
      {error && <p className="mt-5 rounded-xl border border-rose-500/40 bg-rose-500/10 p-4 text-rose-200" role="alert">{error}</p>}
      {notice && <p aria-live="polite" className="mt-5 rounded-xl border border-emerald-500/30 bg-emerald-500/10 p-4 text-emerald-200">{notice}</p>}
      {loading && user?.role === 'ADMIN' ? <p aria-live="polite" className="py-12 text-center text-slate-400">Loading moderation queue…</p> :
        user?.role === 'ADMIN' && reports.length ? <div className="mt-7 space-y-4">{reports.map((report) => <article className="rounded-2xl border border-slate-800 bg-slate-900 p-5" key={report.id}>
          <div className="flex flex-wrap items-start justify-between gap-3"><div><p className="text-xs uppercase tracking-wider text-brand-200">{report.reportType} · {report.category}</p><h2 className="mt-1 text-lg font-semibold text-white">{report.itemName}</h2><p className="mt-1 text-sm text-slate-400">{report.locationName} · {report.ownerDisplayName}</p></div><span className="text-xs text-slate-400">{report.status.replaceAll('_', ' ')}</span></div>
          <p className="mt-3 text-sm leading-6 text-slate-300">{report.description}</p>
          <form className="mt-4 flex flex-col gap-3 sm:flex-row" onSubmit={(event) => remove(report.id, event)}>
            <label className="sr-only" htmlFor={`reason-${report.id}`}>Removal reason for {report.itemName}</label>
            <input className="form-input" id={`reason-${report.id}`} maxLength={1000} minLength={5} onChange={(event) => setReasons((current) => ({ ...current, [report.id]: event.target.value }))} placeholder="Reason for moderation action" required value={reasons[report.id] || ''} />
            <button className="shrink-0 rounded-lg border border-rose-800 px-4 py-2 font-semibold text-rose-200 hover:bg-rose-500/10" type="submit">Remove report</button>
          </form>
        </article>)}</div> : user?.role === 'ADMIN' && !loading && !error && <div className="mt-7 rounded-2xl border border-dashed border-slate-700 p-10 text-center text-slate-400">The moderation queue is clear.</div>}
    </AppShell>
  );
}
