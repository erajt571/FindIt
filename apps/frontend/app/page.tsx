const stats = [
  { label: 'Campus reports', value: '1.2k+' },
  { label: 'Avg. match confidence', value: '94%' },
  { label: 'Recovery rate', value: '87%' },
];

const features = [
  'Lost and found report intake for students and staff',
  'AI-assisted candidate matching and confidence scoring',
  'Secure ownership checks and moderation workflow',
  'Responsive dashboard for quick recovery tracking',
];

export default function HomePage() {
  return (
    <main className="min-h-screen bg-slate-950 text-slate-50">
      <div className="mx-auto max-w-6xl px-6 py-16">
        <header className="flex items-center justify-between border-b border-slate-800 pb-6">
          <div className="flex items-center gap-3">
            <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-brand-500 font-bold text-white">
              F
            </div>
            <div>
              <p className="text-lg font-semibold">FindIt</p>
              <p className="text-xs uppercase tracking-[0.2em] text-slate-400">Campus lost & found</p>
            </div>
          </div>
          <nav className="hidden gap-6 text-sm text-slate-300 md:flex">
            <a href="#features">Features</a>
            <a href="#journey">Journey</a>
            <a href="#status">Status</a>
          </nav>
        </header>

        <section className="grid items-center gap-10 pb-16 pt-20 md:grid-cols-2">
          <div>
            <p className="mb-4 inline-flex rounded-full border border-brand-400/40 bg-brand-500/10 px-3 py-1 text-xs font-medium uppercase tracking-[0.2em] text-brand-200">
              MVP foundation ready
            </p>
            <h1 className="max-w-xl text-4xl font-black tracking-tight text-white md:text-6xl">
              Recover what matters, faster.
            </h1>
            <p className="mt-6 max-w-xl text-lg text-slate-300">
              FindIt brings lost-item reports, matching intelligence, and recovery flows into one campus-safe platform.
            </p>
            <div className="mt-8 flex flex-wrap gap-4">
              <a href="#journey" className="rounded-full bg-brand-500 px-6 py-3 text-sm font-semibold text-white transition hover:bg-brand-400">
                View workflow
              </a>
              <a href="#features" className="rounded-full border border-slate-700 px-6 py-3 text-sm font-semibold text-slate-200 transition hover:border-slate-500 hover:text-white">
                Explore features
              </a>
            </div>
          </div>

          <div className="rounded-3xl border border-slate-800 bg-slate-900 p-6 shadow-soft">
            <div className="flex items-center justify-between border-b border-slate-800 pb-4">
              <div>
                <p className="text-sm text-slate-400">Latest activity</p>
                <h2 className="text-2xl font-bold">Recovery board</h2>
              </div>
              <span className="rounded-full bg-emerald-500/10 px-3 py-1 text-xs font-medium text-emerald-300">
                Active
              </span>
            </div>

            <div className="mt-6 space-y-4">
              {[
                ['Black wireless headphones', 'Lost • Library', 'Match confidence 96%'],
                ['Blue water bottle', 'Found • Student Center', 'Awaiting verification'],
                ['Student ID card', 'Lost • Engineering Hall', 'Owner contacted'],
              ].map(([name, meta, status]) => (
                <div key={name} className="rounded-2xl border border-slate-800 bg-slate-950 p-4">
                  <div className="flex items-center justify-between gap-4">
                    <div>
                      <p className="font-semibold text-white">{name}</p>
                      <p className="text-sm text-slate-400">{meta}</p>
                    </div>
                    <span className="rounded-full bg-slate-800 px-2 py-1 text-[10px] uppercase tracking-[0.18em] text-slate-300">
                      {status}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </section>

        <section id="status" className="grid gap-6 md:grid-cols-3">
          {stats.map((stat) => (
            <div key={stat.label} className="rounded-2xl border border-slate-800 bg-slate-900 p-6">
              <p className="text-3xl font-black text-white">{stat.value}</p>
              <p className="mt-2 text-sm text-slate-400">{stat.label}</p>
            </div>
          ))}
        </section>

        <section id="features" className="mt-20 grid gap-8 md:grid-cols-2">
          <div>
            <p className="text-sm uppercase tracking-[0.2em] text-brand-200">Platform overview</p>
            <h3 className="mt-3 text-3xl font-bold text-white">Built for reliable campus recovery</h3>
          </div>
          <div className="space-y-4">
            {features.map((feature) => (
              <div key={feature} className="flex items-start gap-3 rounded-2xl border border-slate-800 bg-slate-900 p-4">
                <span className="mt-1 inline-flex h-6 w-6 items-center justify-center rounded-full bg-brand-500 text-xs font-bold text-white">
                  ✓
                </span>
                <p className="text-slate-200">{feature}</p>
              </div>
            ))}
          </div>
        </section>

        <section id="journey" className="mt-20 rounded-3xl border border-slate-800 bg-slate-900 p-8">
          <p className="text-sm uppercase tracking-[0.2em] text-brand-200">Core user journey</p>
          <div className="mt-6 grid gap-6 md:grid-cols-4">
            {[
              ['1', 'Report', 'Create a lost or found item entry'],
              ['2', 'Match', 'Review ranked suggestions with explanations'],
              ['3', 'Verify', 'Confirm recovery or reject mismatches'],
              ['4', 'Resolve', 'Close the case and keep the history'],
            ].map(([step, title, copy]) => (
              <div key={step} className="rounded-2xl border border-slate-700 bg-slate-950 p-5">
                <p className="text-sm text-brand-200">Step {step}</p>
                <h4 className="mt-3 text-xl font-semibold text-white">{title}</h4>
                <p className="mt-3 text-sm text-slate-400">{copy}</p>
              </div>
            ))}
          </div>
        </section>
      </div>
    </main>
  );
}
