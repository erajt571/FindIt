'use client';

import Link from 'next/link';
import { AppShell } from '@/components/AppShell';
import { useAuth } from '@/components/AuthProvider';

export default function HomePage() {
  const { user } = useAuth();
  return (
    <AppShell>
      <section className="grid items-center gap-10 py-12 md:grid-cols-[1.1fr_0.9fr] md:py-20">
        <div>
          <p className="inline-flex rounded-full border border-brand-400/40 bg-brand-500/10 px-3 py-1 text-xs font-semibold uppercase tracking-wider text-brand-200">
            Campus lost &amp; found
          </p>
          <h1 className="mt-5 max-w-2xl text-4xl font-black tracking-tight text-white sm:text-6xl">
            Lost something? Let’s bring it back.
          </h1>
          <p className="mt-6 max-w-xl text-lg leading-8 text-slate-300">
            Share a lost or found item, browse campus reports, and review possible matches with the people who can help.
          </p>
          <div className="mt-8 flex flex-wrap gap-3">
            <Link className="rounded-xl bg-brand-500 px-5 py-3 font-semibold text-white hover:bg-brand-400" href="/reports">Browse reports</Link>
            <Link className="rounded-xl border border-slate-700 px-5 py-3 font-semibold text-white hover:border-slate-500" href={user ? '/reports/new' : '/register'}>
              {user ? 'Create a report' : 'Join FindIt'}
            </Link>
          </div>
        </div>
        <div className="rounded-3xl border border-slate-800 bg-slate-900 p-7 shadow-soft">
          <p className="text-sm font-semibold uppercase tracking-wider text-brand-200">How recovery works</p>
          <ol className="mt-6 space-y-5">
            {[
              ['01', 'Post the details', 'Describe what was lost or found and where.'],
              ['02', 'Review suggestions', 'FindIt ranks possible matches using report details.'],
              ['03', 'Verify together', 'Contact the other report owner and confirm safely.'],
            ].map(([number, title, description]) => (
              <li className="flex gap-4" key={number}>
                <span className="grid h-9 w-9 shrink-0 place-items-center rounded-xl bg-slate-800 text-sm font-bold text-brand-200">{number}</span>
                <div><h2 className="font-semibold text-white">{title}</h2><p className="mt-1 text-sm leading-6 text-slate-400">{description}</p></div>
              </li>
            ))}
          </ol>
          <p className="mt-6 rounded-xl bg-slate-950 px-4 py-3 text-xs leading-5 text-slate-400">
            Keep private ownership evidence out of public descriptions. Match suggestions are advisory, never proof.
          </p>
        </div>
      </section>
    </AppShell>
  );
}
