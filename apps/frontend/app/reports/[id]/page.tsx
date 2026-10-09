'use client';

import Link from 'next/link';
import { FormEvent, useCallback, useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { AppShell } from '@/components/AppShell';
import { useAuth } from '@/components/AuthProvider';
import { api, type Match, type Report } from '@/lib/api';

export default function ReportDetailPage() {
  const params = useParams<{ id: string }>();
  const id = params.id;
  const router = useRouter();
  const { user } = useAuth();
  const [report, setReport] = useState<Report | null>(null);
  const [matches, setMatches] = useState<Match[]>([]);
  const [error, setError] = useState('');
  const [matchMessage, setMatchMessage] = useState('');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [editing, setEditing] = useState(false);

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const current = await api<Report>(`/reports/${id}`);
      setReport(current);
      if (user?.id === current.ownerId) {
        const suggestions = await api<Match[]>(`/reports/${id}/matches`);
        setMatches(suggestions);
      } else {
        setMatches([]);
      }
    } catch (loadError) {
      setError(loadError instanceof Error ? loadError.message : 'Unable to load this report.');
    } finally {
      setLoading(false);
    }
  }, [id, user?.id]);

  useEffect(() => { void load(); }, [load]);

  async function updateStatus(status: string) {
    setSaving(true);
    setError('');
    try {
      const updated = await api<Report>(`/reports/${id}/status`, { method: 'PATCH', body: JSON.stringify({ status }) });
      setReport(updated);
      await load();
    } catch (saveError) {
      setError(saveError instanceof Error ? saveError.message : 'Unable to update status.');
    } finally {
      setSaving(false);
    }
  }

  async function saveEdit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (!report) return;
    setSaving(true);
    setError('');
    const form = new FormData(event.currentTarget);
    try {
      const updated = await api<Report>(`/reports/${id}`, {
        method: 'PUT',
        body: JSON.stringify({
          reportType: report.reportType,
          itemName: form.get('itemName'),
          category: form.get('category'),
          description: form.get('description'),
          distinguishingDetails: form.get('distinguishingDetails'),
          locationName: form.get('locationName'),
          incidentDate: report.incidentDate,
        }),
      });
      setReport(updated);
      setEditing(false);
      setMatchMessage('Report saved. Match suggestions will refresh in the background.');
      await load();
    } catch (saveError) {
      setError(saveError instanceof Error ? saveError.message : 'Unable to save your report.');
    } finally {
      setSaving(false);
    }
  }

  if (loading) return <AppShell><p aria-live="polite" className="py-16 text-center text-slate-400">Loading report…</p></AppShell>;
  if (!report) return <AppShell><div className="rounded-xl border border-rose-500/40 bg-rose-500/10 p-5 text-rose-200" role="alert">{error || 'Report not found.'}<p className="mt-4"><Link className="underline" href="/reports">Browse reports</Link></p></div></AppShell>;
  const isOwner = user?.id === report.ownerId;

  return (
    <AppShell>
      <Link className="text-sm text-brand-200 hover:text-white" href="/reports">← Back to reports</Link>
      {error && <p className="mt-5 rounded-lg border border-rose-500/40 bg-rose-500/10 p-3 text-sm text-rose-200" role="alert">{error}</p>}
      {matchMessage && <p aria-live="polite" className="mt-5 rounded-lg border border-emerald-500/30 bg-emerald-500/10 p-3 text-sm text-emerald-200">{matchMessage}</p>}
      <article className="mt-5 rounded-3xl border border-slate-800 bg-slate-900 p-6 sm:p-9">
        {editing && isOwner ? (
          <form className="space-y-5" onSubmit={saveEdit}>
            <h1 className="text-2xl font-bold text-white">Edit report</h1>
            <label className="block text-sm font-medium">Item name<input className="form-input mt-2" defaultValue={report.itemName} maxLength={120} name="itemName" required /></label>
            <label className="block text-sm font-medium">Category<input className="form-input mt-2" defaultValue={report.category} maxLength={80} name="category" required /></label>
            <label className="block text-sm font-medium">Description<textarea className="form-input mt-2 min-h-28" defaultValue={report.description} maxLength={5000} name="description" required /></label>
            <label className="block text-sm font-medium">Distinguishing details<textarea className="form-input mt-2 min-h-20" defaultValue={report.distinguishingDetails || ''} maxLength={2000} name="distinguishingDetails" /></label>
            <label className="block text-sm font-medium">Location<input className="form-input mt-2" defaultValue={report.locationName} maxLength={120} name="locationName" required /></label>
            <div className="flex gap-3"><button className="rounded-lg bg-brand-500 px-4 py-2 font-semibold" disabled={saving} type="submit">{saving ? 'Saving…' : 'Save changes'}</button><button className="rounded-lg border border-slate-700 px-4 py-2" onClick={() => setEditing(false)} type="button">Cancel</button></div>
          </form>
        ) : (
          <>
            <div className="flex flex-wrap items-start justify-between gap-4">
              <div><p className="text-xs font-semibold uppercase tracking-wider text-brand-200">{report.reportType} · {report.category}</p><h1 className="mt-2 text-3xl font-bold text-white">{report.itemName}</h1></div>
              <span className="rounded-full border border-slate-700 px-3 py-1.5 text-xs text-slate-300">{report.status.replaceAll('_', ' ')}</span>
            </div>
            <p className="mt-6 whitespace-pre-wrap leading-7 text-slate-200">{report.description}</p>
            {report.distinguishingDetails && <div className="mt-5 rounded-xl border border-slate-800 bg-slate-950 p-4"><h2 className="text-sm font-semibold text-slate-300">Additional details</h2><p className="mt-2 whitespace-pre-wrap text-sm leading-6 text-slate-400">{report.distinguishingDetails}</p></div>}
            <dl className="mt-6 grid gap-4 border-t border-slate-800 pt-5 text-sm sm:grid-cols-3">
              <div><dt className="text-slate-500">Location</dt><dd className="mt-1 text-slate-200">{report.locationName}</dd></div>
              <div><dt className="text-slate-500">Date</dt><dd className="mt-1 text-slate-200">{report.incidentDate ? new Date(report.incidentDate).toLocaleString() : 'Not specified'}</dd></div>
              <div><dt className="text-slate-500">Reported by</dt><dd className="mt-1 text-slate-200">{report.ownerDisplayName}</dd></div>
            </dl>
            {isOwner && <div className="mt-6 flex flex-wrap gap-3 border-t border-slate-800 pt-5">
              {report.status === 'ACTIVE' && <button className="rounded-lg border border-slate-700 px-4 py-2 text-sm hover:border-slate-500" disabled={saving} onClick={() => setEditing(true)} type="button">Edit report</button>}
              {report.status === 'ACTIVE' && <button className="rounded-lg border border-emerald-700 px-4 py-2 text-sm text-emerald-200 hover:bg-emerald-500/10" disabled={saving} onClick={() => updateStatus('RESOLVED')} type="button">Mark resolved</button>}
              {report.status === 'PENDING_VERIFICATION' && <button className="rounded-lg border border-slate-700 px-4 py-2 text-sm" disabled={saving} onClick={() => updateStatus('ACTIVE')} type="button">Return to active</button>}
            </div>}
          </>
        )}
      </article>
      {isOwner && !editing && (
        <section className="mt-8">
          <div className="flex flex-wrap items-end justify-between gap-3"><div><p className="text-sm font-semibold uppercase tracking-wider text-brand-200">Suggestions</p><h2 className="mt-1 text-2xl font-bold text-white">Possible matches</h2></div><p className="max-w-lg text-xs leading-5 text-slate-500">Suggestions are ranked from item descriptions and report metadata. They are not proof of ownership.</p></div>
          {matches.length ? <div className="mt-4 grid gap-4 md:grid-cols-2">{matches.map((match) => {
            const candidate = match.lostReport.id === report.id ? match.foundReport : match.lostReport;
            return <article className="rounded-2xl border border-slate-800 bg-slate-900 p-5" key={match.id}>
              <div className="flex items-start justify-between gap-3"><div><p className="text-xs font-semibold uppercase tracking-wider text-brand-200">{candidate.reportType} · {candidate.category}</p><h3 className="mt-2 text-lg font-semibold text-white">{candidate.itemName}</h3></div><span className="rounded-full bg-slate-800 px-3 py-1 text-xs">{Math.round(match.score)}% match</span></div>
              <p className="mt-3 text-sm text-slate-300">{candidate.locationName} · {candidate.ownerDisplayName}</p>
              <p className="mt-3 text-xs leading-5 text-slate-400">{match.explanation}</p>
              <p className="mt-2 text-xs text-slate-500">State: {match.state.replaceAll('_', ' ').toLowerCase()}</p>
              {match.state === 'SUGGESTED' && <button className="mt-4 rounded-lg bg-brand-500 px-4 py-2 text-sm font-semibold text-white hover:bg-brand-400" onClick={async () => {
                setError('');
                try { await api(`/matches/${match.id}/verification`, { method: 'POST' }); setMatchMessage('Verification request sent. You will see updates in notifications.'); await load(); }
                catch (actionError) { setError(actionError instanceof Error ? actionError.message : 'Unable to send request.'); }
              }} type="button">Request verification</button>}
            </article>;
          })}</div> : <p className="mt-4 rounded-xl border border-dashed border-slate-700 p-6 text-sm text-slate-400">No suggestions yet. FindIt will look for potential matches in the background.</p>}
        </section>
      )}
    </AppShell>
  );
}
