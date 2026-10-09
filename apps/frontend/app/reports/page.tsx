'use client';

import Link from 'next/link';
import { FormEvent, useCallback, useEffect, useState } from 'react';
import { Suspense } from 'react';
import { usePathname, useRouter, useSearchParams } from 'next/navigation';
import { AppShell } from '@/components/AppShell';
import { ReportCard } from '@/components/ReportCard';
import { api, type Page, type Report } from '@/lib/api';

function ReportsPageContent() {
  const params = useSearchParams();
  const router = useRouter();
  const pathname = usePathname();
  const queryString = params.toString();
  const dateToParam = params.get('dateTo');
  const dateToDate = dateToParam ? new Date(dateToParam) : undefined;
  const dateToValue = dateToDate && !Number.isNaN(dateToDate.getTime())
    ? new Date(dateToDate.getTime() - 86400000).toISOString().slice(0, 10)
    : '';
  const [result, setResult] = useState<Page<Report> | null>(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const data = await api<Page<Report>>(`/reports${queryString ? `?${queryString}` : ''}`);
      setResult(data);
    } catch (loadError) {
      setError(loadError instanceof Error ? loadError.message : 'Unable to load reports.');
    } finally {
      setLoading(false);
    }
  }, [queryString]);

  useEffect(() => { void load(); }, [load]);

  function applyFilters(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const form = new FormData(event.currentTarget);
    const next = new URLSearchParams();
    for (const key of ['q', 'type', 'category', 'location', 'dateFrom', 'dateTo']) {
      const value = String(form.get(key) || '').trim();
      if (value && key === 'dateFrom') next.set(key, `${value}T00:00:00.000Z`);
      else if (value && key === 'dateTo') {
        const exclusiveDate = new Date(`${value}T00:00:00.000Z`);
        exclusiveDate.setUTCDate(exclusiveDate.getUTCDate() + 1);
        next.set(key, exclusiveDate.toISOString());
      } else if (value) next.set(key, value);
    }
    next.set('page', '0');
    next.set('size', '20');
    router.push(`${pathname}?${next.toString()}`);
  }

  function changePage(page: number) {
    const next = new URLSearchParams(queryString);
    next.set('page', String(page));
    router.push(`${pathname}?${next.toString()}`);
  }

  return (
    <AppShell>
      <div className="flex flex-wrap items-end justify-between gap-4">
        <div><p className="text-sm font-semibold uppercase tracking-wider text-brand-200">Campus listings</p><h1 className="mt-2 text-3xl font-bold text-white">Browse reports</h1></div>
        <Link className="rounded-xl bg-brand-500 px-4 py-2.5 font-semibold text-white hover:bg-brand-400" href="/reports/new">Create a report</Link>
      </div>
      <form className="mt-7 grid gap-3 rounded-2xl border border-slate-800 bg-slate-900 p-4 sm:grid-cols-2 lg:grid-cols-7" onSubmit={applyFilters}>
        <label className="text-xs font-semibold text-slate-300 lg:col-span-2">Search
          <input className="form-input mt-1.5" defaultValue={params.get('q') || ''} name="q" placeholder="Item, details, or place" />
        </label>
        <label className="text-xs font-semibold text-slate-300">Report type
          <select className="form-input mt-1.5" defaultValue={params.get('type') || ''} name="type">
            <option value="">All types</option><option value="LOST">Lost</option><option value="FOUND">Found</option>
          </select>
        </label>
        <label className="text-xs font-semibold text-slate-300">Category
          <input className="form-input mt-1.5" defaultValue={params.get('category') || ''} name="category" placeholder="Electronics" />
        </label>
        <label className="text-xs font-semibold text-slate-300">Location
          <input className="form-input mt-1.5" defaultValue={params.get('location') || ''} name="location" placeholder="Library" />
        </label>
        <label className="block text-xs font-semibold text-slate-300">From date
          <input className="form-input mt-1.5" defaultValue={params.get('dateFrom')?.slice(0, 10) || ''} name="dateFrom" type="date" />
        </label>
        <label className="block text-xs font-semibold text-slate-300">Through date
          <input className="form-input mt-1.5" defaultValue={dateToValue} name="dateTo" type="date" />
        </label>
        <div className="flex items-end gap-2 sm:col-span-2 lg:col-span-7">
          <button className="rounded-lg bg-slate-700 px-4 py-2 font-semibold hover:bg-slate-600" type="submit">Apply filters</button>
          <button className="rounded-lg border border-slate-700 px-4 py-2 text-slate-300 hover:border-slate-500" onClick={() => router.push(pathname)} type="button">Clear</button>
        </div>
      </form>
      {error && <p className="mt-6 rounded-xl border border-rose-500/40 bg-rose-500/10 p-4 text-rose-200" role="alert">{error}</p>}
      {loading ? <p aria-live="polite" className="py-12 text-center text-slate-400">Loading reports…</p> :
        result?.data.length ? (
          <>
            <div aria-live="polite" className="mt-6 grid gap-4 md:grid-cols-2">
              {result.data.map((report) => <ReportCard key={report.id} report={report} />)}
            </div>
            <div className="mt-7 flex items-center justify-between text-sm text-slate-400">
              <span>{result.totalElements} active {result.totalElements === 1 ? 'report' : 'reports'}</span>
              <div className="flex gap-2">
                <button className="rounded-lg border border-slate-700 px-3 py-2 disabled:opacity-40" disabled={result.page === 0} onClick={() => changePage(result.page - 1)} type="button">Previous</button>
                <span className="px-2 py-2">Page {result.page + 1} of {Math.max(result.totalPages, 1)}</span>
                <button className="rounded-lg border border-slate-700 px-3 py-2 disabled:opacity-40" disabled={result.page + 1 >= result.totalPages} onClick={() => changePage(result.page + 1)} type="button">Next</button>
              </div>
            </div>
          </>
        ) : !error && <div className="mt-8 rounded-2xl border border-dashed border-slate-700 p-10 text-center">
          <h2 className="text-xl font-semibold text-white">No reports found</h2>
          <p className="mt-2 text-sm text-slate-400">Try broadening your filters or create a report to get started.</p>
        </div>}
    </AppShell>
  );
}

export default function ReportsPage() {
  return <Suspense fallback={<main className="min-h-screen bg-slate-950 p-10 text-center text-slate-400">Loading reports…</main>}><ReportsPageContent /></Suspense>;
}
