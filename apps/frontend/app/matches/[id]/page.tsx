'use client';

import Link from 'next/link';
import { useCallback, useEffect, useState } from 'react';
import { useParams } from 'next/navigation';
import { AppShell } from '@/components/AppShell';
import { useAuth } from '@/components/AuthProvider';
import { api, type Match } from '@/lib/api';

export default function MatchDetailPage() {
  const params = useParams<{ id: string }>();
  const { user } = useAuth();
  const [match, setMatch] = useState<Match | null>(null);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(true);
  const load = useCallback(async () => {
    setLoading(true);
    try { setMatch(await api<Match>(`/matches/${params.id}`)); }
    catch (loadError) { setError(loadError instanceof Error ? loadError.message : 'Unable to load this match.'); }
    finally { setLoading(false); }
  }, [params.id]);
  useEffect(() => { void load(); }, [load]);

  async function action(path: string, method: 'POST' | 'PATCH', body?: object) {
    setError('');
    setMessage('');
    try {
      await api(`/matches/${params.id}${path}`, { method, body: body ? JSON.stringify(body) : undefined });
      setMessage('Your response has been saved.');
      await load();
    } catch (actionError) {
      setError(actionError instanceof Error ? actionError.message : 'Unable to save your response.');
    }
  }

  return (
    <AppShell>
      <Link className="text-sm text-brand-200 hover:text-white" href="/notifications">← Back to notifications</Link>
      {loading ? <p aria-live="polite" className="py-16 text-center text-slate-400">Loading match…</p> : !match ? <p className="mt-6 rounded-xl border border-rose-500/40 bg-rose-500/10 p-4 text-rose-200" role="alert">{error || 'Match not found.'}</p> : (
        <>
          <div className="mt-5 flex flex-wrap items-start justify-between gap-4"><div><p className="text-sm font-semibold uppercase tracking-wider text-brand-200">Possible match · {Math.round(match.score)}%</p><h1 className="mt-2 text-3xl font-bold text-white">Review this suggestion</h1></div><span className="rounded-full border border-slate-700 px-3 py-1.5 text-xs text-slate-300">{match.state.replaceAll('_', ' ')}</span></div>
          <p className="mt-4 max-w-3xl text-sm leading-6 text-slate-400">{match.explanation}</p>
          <div className="mt-7 grid gap-4 lg:grid-cols-2">
            {[match.lostReport, match.foundReport].map((report) => <article className="rounded-2xl border border-slate-800 bg-slate-900 p-6" key={report.id}>
              <p className="text-xs font-semibold uppercase tracking-wider text-brand-200">{report.reportType} report</p><h2 className="mt-2 text-xl font-semibold text-white">{report.itemName}</h2><p className="mt-1 text-sm text-slate-400">{report.category} · {report.locationName}</p><p className="mt-4 whitespace-pre-wrap text-sm leading-6 text-slate-300">{report.description}</p><Link className="mt-4 inline-block text-sm text-brand-200 underline" href={`/reports/${report.id}`}>Open report</Link>
            </article>)}
          </div>
          <p className="mt-5 rounded-xl border border-amber-500/20 bg-amber-500/5 p-4 text-sm leading-6 text-amber-100">A match is only a suggestion. Verify privately with the other report owner and do not share passwords, identification numbers, or private ownership proof in public.</p>
          {error && <p className="mt-5 rounded-lg border border-rose-500/40 bg-rose-500/10 p-3 text-sm text-rose-200" role="alert">{error}</p>}
          {message && <p aria-live="polite" className="mt-5 rounded-lg border border-emerald-500/30 bg-emerald-500/10 p-3 text-sm text-emerald-200">{message}</p>}
          <div className="mt-6 flex flex-wrap gap-3">
            {match.state === 'SUGGESTED' && <>
              <button className="rounded-lg bg-brand-500 px-4 py-2 font-semibold text-white" onClick={() => action('/verification', 'POST')} type="button">Request verification</button>
              <button className="rounded-lg border border-slate-700 px-4 py-2" onClick={() => action('/feedback', 'POST', { feedback: 'RELEVANT' })} type="button">Relevant</button>
              <button className="rounded-lg border border-slate-700 px-4 py-2" onClick={() => action('/feedback', 'POST', { feedback: 'NOT_RELEVANT' })} type="button">Not relevant</button>
              <button className="rounded-lg border border-rose-800 px-4 py-2 text-rose-200" onClick={() => action('/status', 'PATCH', { state: 'REJECTED' })} type="button">Reject suggestion</button>
            </>}
            {match.state === 'ACCEPTED_FOR_VERIFICATION' && match.verificationRequestedById === user?.id && <button className="rounded-lg border border-slate-700 px-4 py-2" onClick={() => action('/status', 'PATCH', { state: 'REJECTED' })} type="button">Cancel verification request</button>}
            {match.state === 'ACCEPTED_FOR_VERIFICATION' && match.verificationRequestedById !== user?.id && <>
              <button className="rounded-lg bg-emerald-700 px-4 py-2 font-semibold text-white" onClick={() => action('/status', 'PATCH', { state: 'CLOSED' })} type="button">Confirm recovery</button>
              <button className="rounded-lg border border-rose-800 px-4 py-2 text-rose-200" onClick={() => action('/status', 'PATCH', { state: 'REJECTED' })} type="button">Decline request</button>
            </>}
          </div>
        </>
      )}
    </AppShell>
  );
}
