import { NextRequest, NextResponse } from 'next/server';

export const runtime = 'nodejs';
export const dynamic = 'force-dynamic';

async function proxy(request: NextRequest, context: { params: { path: string[] } }) {
  const backend = (process.env.FINDIT_BACKEND_URL || 'http://localhost:8080').replace(/\/$/, '');
  const incoming = new URL(request.url);
  const target = `${backend}/api/${context.params.path.map((part) => encodeURIComponent(part)).join('/')}${incoming.search}`;
  const headers = new Headers();
  for (const name of ['accept', 'content-type', 'cookie', 'x-xsrf-token', 'x-request-id']) {
    const value = request.headers.get(name);
    if (value) headers.set(name, value);
  }
  const body = ['GET', 'HEAD'].includes(request.method) ? undefined : await request.arrayBuffer();
  const upstream = await fetch(target, {
    method: request.method,
    headers,
    body,
    cache: 'no-store',
    redirect: 'manual',
  });
  const responseHeaders = new Headers();
  for (const name of ['content-type', 'cache-control', 'x-request-id']) {
    const value = upstream.headers.get(name);
    if (value) responseHeaders.set(name, value);
  }
  const upstreamHeaders = upstream.headers as Headers & { getSetCookie?: () => string[] };
  const combinedCookies = upstream.headers.get('set-cookie');
  const cookies = upstreamHeaders.getSetCookie?.()
    ?? (combinedCookies ? combinedCookies.split(/,(?=\s*[^;,\s]+=)/) : []);
  for (const cookie of cookies) responseHeaders.append('set-cookie', cookie);
  return new NextResponse(upstream.body, { status: upstream.status, headers: responseHeaders });
}

export const GET = proxy;
export const HEAD = proxy;
export const POST = proxy;
export const PUT = proxy;
export const PATCH = proxy;
export const DELETE = proxy;
