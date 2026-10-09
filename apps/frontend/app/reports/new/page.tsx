'use client';

import { FormEvent, useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { AppShell } from '@/components/AppShell';
import { useAuth } from '@/components/AuthProvider';
import { api } from '@/lib/api';

export default function NewReportPage() {
  const { user, loading } = useAuth();
  const router = useRouter();
  const [error, setError] = useState('');
  const [saving, setSaving] = useState(false);

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError('');
    setSaving(true);
    const form = new FormData(event.currentTarget);
    const date = String(form.get('incidentDate') || '');
    try {
      const report = await api<{ id: string }>('/reports', {
        method: 'POST',
        body: JSON.stringify({
          reportType: form.get('reportType'),
          itemName: form.get('itemName'),
          category: form.get('category'),
          description: form.get('description'),
          distinguishingDetails: form.get('distinguishingDetails'),
          locationName: form.get('locationName'),
          incidentDate: date ? new Date(`${date}T12:00:00`).toISOString() : null,
        }),
      });
      router.push(`/reports/${report.id}`);
    } catch (saveError) {
      setError(saveError instanceof Error ? saveError.message : 'Unable to create your report.');
    } finally {
      setSaving(false);
    }
  }

  return (
    <AppShell>
      <div className="mx-auto max-w-2xl">
        <Link className="text-sm text-brand-200 hover:text-white" href="/reports">← Back to reports</Link>
        <h1 className="mt-4 text-3xl font-bold text-white">Create a report</h1>
        <p className="mt-2 text-slate-400">Do not include private proof of ownership or sensitive personal details in your description.</p>
        {!loading && !user ? <div className="mt-6 rounded-xl border border-slate-800 bg-slate-900 p-5">Sign in to create a report. <Link className="text-brand-200 underline" href="/login">Sign in</Link></div> : (
          <form className="mt-7 space-y-5 rounded-2xl border border-slate-800 bg-slate-900 p-6" onSubmit={submit}>
            <label className="block text-sm font-medium text-slate-200">I am reporting
              <select className="form-input mt-2" name="reportType" required><option value="LOST">A lost item</option><option value="FOUND">An item I found</option></select>
            </label>
            <div className="grid gap-5 sm:grid-cols-2">
              <label className="block text-sm font-medium text-slate-200">Item name<input className="form-input mt-2" maxLength={120} name="itemName" required /></label>
              <label className="block text-sm font-medium text-slate-200">Category<input className="form-input mt-2" maxLength={80} name="category" placeholder="Electronics" required /></label>
            </div>
            <label className="block text-sm font-medium text-slate-200">Description<textarea className="form-input mt-2 min-h-28" maxLength={5000} name="description" required /></label>
            <label className="block text-sm font-medium text-slate-200">Distinguishing details <span className="font-normal text-slate-500">(optional; avoid private proof)</span><textarea className="form-input mt-2 min-h-20" maxLength={2000} name="distinguishingDetails" /></label>
            <div className="grid gap-5 sm:grid-cols-2">
              <label className="block text-sm font-medium text-slate-200">Campus location<input className="form-input mt-2" maxLength={120} name="locationName" placeholder="Student Center" required /></label>
              <label className="block text-sm font-medium text-slate-200">Date lost or found<input className="form-input mt-2" max={new Date().toISOString().slice(0, 10)} name="incidentDate" type="date" /></label>
            </div>
            {error && <p className="rounded-lg border border-rose-500/40 bg-rose-500/10 p-3 text-sm text-rose-200" role="alert">{error}</p>}
            <div className="flex flex-wrap items-center justify-between gap-3">
              <p className="max-w-md text-xs leading-5 text-slate-500">A matching job runs after the report is saved. A matching failure will not prevent the report from being created.</p>
              <button className="rounded-xl bg-brand-500 px-5 py-3 font-semibold text-white hover:bg-brand-400 disabled:opacity-50" disabled={saving || loading || !user} type="submit">{saving ? 'Saving…' : 'Publish report'}</button>
            </div>
          </form>
        )}
      </div>
    </AppShell>
  );
}
