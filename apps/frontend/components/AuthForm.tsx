'use client';

import Link from 'next/link';
import { FormEvent, useState } from 'react';
import { useRouter } from 'next/navigation';
import { AppShell } from '@/components/AppShell';
import { useAuth } from '@/components/AuthProvider';
import { api } from '@/lib/api';

export function AuthForm({ mode }: { mode: 'login' | 'register' }) {
  const registering = mode === 'register';
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const router = useRouter();
  const { refresh } = useAuth();

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError('');
    setSubmitting(true);
    const form = new FormData(event.currentTarget);
    const payload: Record<string, string> = {
      email: String(form.get('email') || ''),
      password: String(form.get('password') || ''),
    };
    if (registering) payload.displayName = String(form.get('displayName') || '');
    try {
      await api(`/auth/${mode}`, { method: 'POST', body: JSON.stringify(payload) });
      if (registering) router.push('/login?registered=1');
      else {
        await refresh();
        router.push('/dashboard');
      }
    } catch (submitError) {
      setError(submitError instanceof Error ? submitError.message : 'Unable to complete the request.');
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <AppShell>
      <div className="mx-auto max-w-md rounded-3xl border border-slate-800 bg-slate-900 p-7 sm:p-9">
        <p className="text-sm font-semibold uppercase tracking-wider text-brand-200">{registering ? 'Get started' : 'Welcome back'}</p>
        <h1 className="mt-2 text-3xl font-bold text-white">{registering ? 'Create your account' : 'Sign in to FindIt'}</h1>
        <p className="mt-3 text-sm leading-6 text-slate-400">Use your campus email and keep private ownership details out of public reports.</p>
        <form className="mt-7 space-y-5" onSubmit={submit}>
          {registering && (
            <label className="block text-sm font-medium text-slate-200">
              Display name
              <input autoComplete="name" className="form-input mt-2" maxLength={120} minLength={2} name="displayName" required />
            </label>
          )}
          <label className="block text-sm font-medium text-slate-200">
            Email
            <input autoComplete="email" className="form-input mt-2" name="email" required type="email" />
          </label>
          <label className="block text-sm font-medium text-slate-200">
            Password
            <input autoComplete={registering ? 'new-password' : 'current-password'} className="form-input mt-2" minLength={registering ? 8 : undefined} name="password" required type="password" />
          </label>
          {error && <p className="rounded-lg border border-rose-500/40 bg-rose-500/10 p-3 text-sm text-rose-200" role="alert">{error}</p>}
          <button className="w-full rounded-xl bg-brand-500 px-4 py-3 font-semibold text-white hover:bg-brand-400 disabled:cursor-not-allowed disabled:opacity-60" disabled={submitting} type="submit">
            {submitting ? 'Please wait…' : registering ? 'Create account' : 'Sign in'}
          </button>
        </form>
        <p className="mt-6 text-sm text-slate-400">
          {registering ? 'Already have an account?' : 'New to FindIt?'}{' '}
          <Link className="font-semibold text-brand-200 hover:text-white" href={registering ? '/login' : '/register'}>{registering ? 'Sign in' : 'Create an account'}</Link>
        </p>
      </div>
    </AppShell>
  );
}
