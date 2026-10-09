import Link from 'next/link';
import type { Report } from '@/lib/api';

export function ReportCard({ report }: { report: Report }) {
  return (
    <article className="rounded-2xl border border-slate-800 bg-slate-900 p-5">
      <div className="flex flex-wrap items-start justify-between gap-3">
        <div>
          <p className="text-xs font-semibold uppercase tracking-wider text-brand-200">{report.reportType} · {report.category}</p>
          <h2 className="mt-2 text-xl font-semibold text-white">
            <Link className="hover:text-brand-200" href={`/reports/${report.id}`}>{report.itemName}</Link>
          </h2>
        </div>
        <span className="rounded-full border border-slate-700 px-3 py-1 text-xs text-slate-300">{report.status.replaceAll('_', ' ')}</span>
      </div>
      <p className="mt-3 line-clamp-3 text-sm leading-6 text-slate-300">{report.description}</p>
      <div className="mt-4 flex flex-wrap gap-x-4 gap-y-1 text-xs text-slate-400">
        <span>{report.locationName}</span>
        {report.incidentDate && <span>{new Date(report.incidentDate).toLocaleDateString()}</span>}
        <span>Reported by {report.ownerDisplayName}</span>
      </div>
    </article>
  );
}
