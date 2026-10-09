'use client';

import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { api, clearCsrfToken, type UserProfile } from '@/lib/api';

type AuthContextValue = {
  user: UserProfile | null;
  loading: boolean;
  refresh: () => Promise<UserProfile | null>;
  logout: () => Promise<void>;
};

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<UserProfile | null>(null);
  const [loading, setLoading] = useState(true);

  const refresh = useCallback(async () => {
    try {
      const profile = await api<UserProfile>('/users/me');
      setUser(profile);
      return profile;
    } catch (error) {
      if ((error as { status?: number }).status === 401) {
        setUser(null);
        return null;
      }
      throw error;
    }
  }, []);

  useEffect(() => {
    refresh().catch(() => setUser(null)).finally(() => setLoading(false));
  }, [refresh]);

  const logout = useCallback(async () => {
    await api<void>('/auth/logout', { method: 'POST' });
    clearCsrfToken();
    setUser(null);
  }, []);

  const value = useMemo(() => ({ user, loading, refresh, logout }), [user, loading, refresh, logout]);
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const value = useContext(AuthContext);
  if (!value) throw new Error('useAuth must be used inside AuthProvider');
  return value;
}
