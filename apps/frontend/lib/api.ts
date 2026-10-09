export type ApiError = Error & { status?: number };

export type Page<T> = {
  data: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
};

export type UserProfile = {
  id: string;
  displayName: string;
  email: string;
  role: 'USER' | 'ADMIN';
};

export type Report = {
  id: string;
  ownerId: string;
  ownerDisplayName: string;
  reportType: 'LOST' | 'FOUND';
  itemName: string;
  category: string;
  description: string;
  distinguishingDetails?: string | null;
  locationName: string;
  incidentDate?: string | null;
  imageUrl?: string | null;
  status: 'ACTIVE' | 'PENDING_VERIFICATION' | 'RESOLVED' | 'REMOVED';
  createdAt: string;
  updatedAt: string;
};

export type Match = {
  id: string;
  lostReport: Report;
  foundReport: Report;
  score: number;
  explanation: string;
  state: 'SUGGESTED' | 'ACCEPTED_FOR_VERIFICATION' | 'REJECTED' | 'CLOSED';
  verificationRequestedById?: string | null;
  createdAt: string;
};

export type Notification = {
  id: string;
  title: string;
  type: string;
  message: string;
  relatedReportId?: string | null;
  matchId?: string | null;
  readAt?: string | null;
  createdAt: string;
};

export type DashboardSummary = {
  activeReports: number;
  resolvedReports: number;
  suggestedMatches: number;
  unreadNotifications: number;
  campusActiveReports: number;
};

const apiBase = (process.env.NEXT_PUBLIC_API_BASE_URL || '').replace(/\/$/, '');
let csrfToken: string | undefined;

async function getCsrfToken(): Promise<string> {
  if (csrfToken) return csrfToken;
  const response = await fetch(`${apiBase}/api/csrf`, {
    credentials: 'include',
    cache: 'no-store',
  });
  if (!response.ok) throw new Error('Unable to initialize a secure session. Please retry.');
  const body = (await response.json()) as { token: string };
  csrfToken = body.token;
  return csrfToken;
}

export async function api<T>(path: string, init: RequestInit = {}): Promise<T> {
  const method = (init.method || 'GET').toUpperCase();
  const headers = new Headers(init.headers);
  headers.set('Accept', 'application/json');
  if (init.body && !headers.has('Content-Type')) headers.set('Content-Type', 'application/json');
  if (!['GET', 'HEAD', 'OPTIONS'].includes(method)) {
    headers.set('X-XSRF-TOKEN', await getCsrfToken());
  }

  const response = await fetch(`${apiBase}/api${path}`, {
    ...init,
    headers,
    credentials: 'include',
    cache: 'no-store',
  });
  if (response.status === 204) return undefined as T;
  const body = await response.json().catch(() => null);
  if (!response.ok) {
    const error = new Error(body?.message || `Request failed (${response.status})`) as ApiError;
    error.status = response.status;
    throw error;
  }
  return body as T;
}

export function clearCsrfToken() {
  csrfToken = undefined;
}
