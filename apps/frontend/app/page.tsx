'use client';

import Link from 'next/link';
import Image from 'next/image';
import { FormEvent, useEffect, useState } from 'react';
import { AppShell } from '@/components/AppShell';
import { api, type Page, type Report } from '@/lib/api';

const categories = ['Electronics', 'Accessories', 'ID Cards', 'Bags', 'Keys', 'Other'];

function CategoryIcon({ category }: { category: string }) {
  const name = category.toLowerCase();
  if (name.includes('key')) {
    return <svg aria-hidden="true" className="h-9 w-9" fill="none" viewBox="0 0 24 24"><circle cx="8" cy="15" r="4" stroke="currentColor" strokeWidth="1.7" /><path d="m11 12 8-8m-3 3 2 2m-5 1 2 2" stroke="currentColor" strokeLinecap="round" strokeWidth="1.7" /></svg>;
  }
  if (name.includes('electronic') || name.includes('headphone') || name.includes('audio')) {
    return <svg aria-hidden="true" className="h-9 w-9" fill="none" viewBox="0 0 24 24"><path d="M4 13v-1a8 8 0 0 1 16 0v1m-16 0v4a2 2 0 0 0 2 2h2v-7H6a2 2 0 0 0-2 1Zm16 0v4a2 2 0 0 1-2 2h-2v-7h2a2 2 0 0 1 2 1Z" stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="1.7" /></svg>;
  }
  if (name.includes('card') || name.includes('id')) {
    return <svg aria-hidden="true" className="h-9 w-9" fill="none" viewBox="0 0 24 24"><rect height="14" rx="2" stroke="currentColor" strokeWidth="1.7" width="19" x="2.5" y="5" /><path d="M3 9h18m-14 4h4m-4 3h7" stroke="currentColor" strokeLinecap="round" strokeWidth="1.7" /></svg>;
  }
  if (name.includes('bag')) {
    return <svg aria-hidden="true" className="h-9 w-9" fill="none" viewBox="0 0 24 24"><rect height="14" rx="2" stroke="currentColor" strokeWidth="1.7" width="18" x="3" y="7" /><path d="M9 7V5a3 3 0 0 1 6 0v2m-3 4v6" stroke="currentColor" strokeLinecap="round" strokeWidth="1.7" /></svg>;
  }
  return <svg aria-hidden="true" className="h-9 w-9" fill="none" viewBox="0 0 24 24"><path d="m12 3 8 4.5v9L12 21l-8-4.5v-9L12 3Z" stroke="currentColor" strokeLinejoin="round" strokeWidth="1.7" /><path d="m4 7.5 8 4.5 8-4.5M12 12v9" stroke="currentColor" strokeLinejoin="round" strokeWidth="1.7" /></svg>;
}

function ItemArtwork({ report }: { report: Report }) {
  if (report.imageUrl) {
    return <Image alt={report.itemName} className="object-cover" fill sizes="(min-width: 1024px) 25vw, (min-width: 640px) 50vw, 100vw" src={report.imageUrl} unoptimized />;
  }
  return (
    <div aria-label={`${report.category} illustration`} className="grid h-full w-full place-items-center bg-gradient-to-br from-[#e8f2f6] to-[#d9f5f1] text-[#91aabd]" role="img">
      <CategoryIcon category={report.category} />
    </div>
  );
}

export default function HomePage() {
  const [reports, setReports] = useState<Report[]>([]);
  const [totals, setTotals] = useState({ lost: 0, found: 0, all: 0 });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let active = true;
    async function loadHomeData() {
      setLoading(true);
      setError('');
      try {
        const [recent, lost, found] = await Promise.all([
          api<Page<Report>>('/reports?page=0&size=4'),
          api<Page<Report>>('/reports?type=LOST&page=0&size=1'),
          api<Page<Report>>('/reports?type=FOUND&page=0&size=1'),
        ]);
        if (active) {
          setReports(recent.data);
          setTotals({ lost: lost.totalElements, found: found.totalElements, all: recent.totalElements });
        }
      } catch (loadError) {
        if (active) setError(loadError instanceof Error ? loadError.message : 'Unable to load recent reports.');
      } finally {
        if (active) setLoading(false);
      }
    }
    void loadHomeData();
    return () => { active = false; };
  }, []);

  function searchReports(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const query = new FormData(event.currentTarget).get('q')?.toString().trim();
    window.location.assign(query ? `/reports?q=${encodeURIComponent(query)}` : '/reports');
  }

  return (
    <AppShell>
      <section className="bg-white">
        <div className="mx-auto grid max-w-7xl items-center gap-10 px-5 py-14 md:min-h-[410px] md:grid-cols-[1.1fr_0.9fr] md:gap-14 md:py-16">
          <div>
            <p className="inline-flex items-center gap-2 rounded-full bg-[#d9f4f1] px-3 py-1 text-xs font-semibold text-[#075e62]">
              <span aria-hidden="true">✣</span> Smart matching for your campus
            </p>
            <h1 className="mt-5 max-w-xl text-[2.55rem] font-extrabold leading-[1.03] tracking-tight text-[#11264b] sm:text-[2.8rem]">
              Lost something?<br />Let&apos;s find it.
            </h1>
            <p className="mt-4 max-w-lg text-base leading-[1.55] text-[#61718a] sm:text-[1.05rem]">
              Post what you lost or found. FindIt compares every report by description, category, place and date, and shows you the most likely matches.
            </p>
            <div className="mt-6 flex flex-wrap gap-3">
              <Link className="rounded-[10px] bg-[#172d55] px-7 py-3 text-center text-sm font-semibold text-white shadow-sm transition hover:bg-[#223b6a]" href="/reports/new?type=LOST">Report Lost Item</Link>
              <Link className="rounded-[10px] bg-[#009b98] px-7 py-3 text-center text-sm font-semibold text-white shadow-sm transition hover:bg-[#008582]" href="/reports/new?type=FOUND">Report Found Item</Link>
            </div>
          </div>

          <div className="rounded-2xl border border-[#dce4ee] bg-white p-4 shadow-[0_2px_5px_rgba(17,38,75,0.06)] sm:p-[18px]">
            <form className="flex gap-2" onSubmit={searchReports}>
              <label className="relative min-w-0 flex-1">
                <span aria-hidden="true" className="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-[#8997aa]">
                  <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24"><circle cx="10.8" cy="10.8" r="6.3" stroke="currentColor" strokeWidth="2" /><path d="m15.5 15.5 4 4" stroke="currentColor" strokeLinecap="round" strokeWidth="2" /></svg>
                </span>
                <span className="sr-only">Search reports</span>
                <input className="h-10 w-full rounded-[9px] border border-[#dce4ee] bg-white pl-9 pr-3 text-sm text-[#172d55] placeholder:text-[#8997aa] focus:border-[#009b98] focus:outline-none focus:ring-2 focus:ring-[#009b98]/20" name="q" placeholder="Search headphones, Library, wallet..." />
              </label>
              <button className="rounded-[9px] bg-[#172d55] px-4 text-sm font-semibold text-white shadow-sm transition hover:bg-[#223b6a]" type="submit">Search</button>
            </form>
            <div aria-label="Browse by category" className="mt-3 flex flex-wrap gap-2">
              {categories.map((category) => (
                <Link className="rounded-full border border-[#dce4ee] bg-[#f8fafc] px-3 py-1.5 text-xs font-medium text-[#243754] transition hover:border-[#009b98] hover:bg-[#effaf8]" href={`/reports?category=${encodeURIComponent(category)}`} key={category}>
                  <span aria-hidden="true" className="mr-1.5">{category === 'Electronics' ? '♧' : category === 'Accessories' ? '♙' : category === 'ID Cards' ? '▱' : category === 'Bags' ? '▣' : category === 'Keys' ? '⚿' : '◇'}</span>{category}
                </Link>
              ))}
            </div>
            <div aria-label="Campus report totals" className="mt-4 grid grid-cols-3 border-t border-[#e2e8f0] pt-4 text-center">
              {[
                [totals.lost, 'Open lost'],
                [totals.found, 'Open found'],
                [totals.all, 'Active reports'],
              ].map(([value, label]) => (
                <div key={label}>
                  <p className="text-[1.3rem] font-bold leading-6 text-[#10254a]">{loading ? '—' : value}</p>
                  <p className="mt-0.5 text-[11px] text-[#758198]">{label}</p>
                </div>
              ))}
            </div>
          </div>
        </div>
      </section>

      <section className="border-y border-[#e1e7ef] bg-[#f6f8fb]">
        <div className="mx-auto max-w-7xl px-5 py-7">
          <div className="flex items-center justify-between gap-4">
            <h2 className="text-lg font-bold tracking-tight text-[#12284d]">Recently reported</h2>
            <Link className="flex items-center gap-1 text-sm font-medium text-[#007f82] hover:text-[#005f64]" href="/reports">Browse all <span aria-hidden="true">→</span></Link>
          </div>
          {error && <p className="mt-5 rounded-lg border border-rose-200 bg-rose-50 p-3 text-sm text-rose-700" role="alert">{error}</p>}
          {loading ? (
            <p aria-live="polite" className="py-12 text-center text-sm text-[#758198]">Loading recent reports…</p>
          ) : reports.length > 0 ? (
            <div className="mt-4 grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
              {reports.map((report) => (
                <article className="overflow-hidden rounded-2xl border border-[#e1e7ef] bg-white shadow-[0_2px_6px_rgba(17,38,75,0.04)]" key={report.id}>
                  <Link className="block" href={`/reports/${report.id}`}>
                    <div className="relative h-48">
                      <ItemArtwork report={report} />
                      <span className={`absolute left-2.5 top-2.5 rounded-full px-2.5 py-1 text-[11px] font-semibold ${report.reportType === 'FOUND' ? 'bg-[#d9f4f1] text-[#007f82]' : 'bg-[#ffebe6] text-[#d4553f]'}`}>
                        {report.reportType === 'FOUND' ? 'Found' : 'Lost'}
                      </span>
                    </div>
                    <div className="p-3.5">
                      <h3 className="truncate text-sm font-semibold text-[#172d55]">{report.itemName}</h3>
                      <p className="mt-1 truncate text-xs text-[#738198]">{report.locationName} · {report.category}</p>
                    </div>
                  </Link>
                </article>
              ))}
            </div>
          ) : !error ? (
            <div className="mt-5 rounded-2xl border border-dashed border-[#d1dbe7] bg-white px-5 py-9 text-center">
              <p className="font-semibold text-[#172d55]">No active reports yet</p>
              <p className="mt-1 text-sm text-[#738198]">Be the first to help reunite someone with their item.</p>
              <Link className="mt-4 inline-flex rounded-lg bg-[#009b98] px-4 py-2 text-sm font-semibold text-white hover:bg-[#008582]" href="/reports/new">Create a report</Link>
            </div>
          ) : null}
        </div>
      </section>
    </AppShell>
  );
}
