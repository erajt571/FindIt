'use client';

import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { api, clearCsrfToken, type UserProfile } from '@/lib/api';

type AuthContextValue = {
  user: UserProfile | null;
  loading: boolean;
  error: string | null;
  refresh: () => Promise<UserProfile | null>;
  logout: () => Promise<void>;
};

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<UserProfile | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const refresh = useCallback(async () => {
    try {
      const profile = await api<UserProfile>('/users/me');
      setUser(profile);
      setError(null);
      return profile;
    } catch (error) {
      const status = error && typeof error === 'object' && 'status' in error ? error.status : undefined;
      if (status === 401) {
        setUser(null);
        setError(null);
        return null;
      }
      setError(error instanceof Error ? error.message : 'Unable to load your account.');
      throw error;
    }
  }, []);

  useEffect(() => {
    refresh().catch(() => undefined).finally(() => setLoading(false));
  }, [refresh]);

  const logout = useCallback(async () => {
    await api<void>('/auth/logout', { method: 'POST' });
    clearCsrfToken();
    setUser(null);
    setError(null);
  }, []);

  const value = useMemo(() => ({ user, loading, error, refresh, logout }), [user, loading, error, refresh, logout]);
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const value = useContext(AuthContext);
  if (!value) throw new Error('useAuth must be used inside AuthProvider');
  return value;
}
