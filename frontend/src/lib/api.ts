import { getAccessToken, getTenantId, setAccessToken } from "@/lib/auth";

const API_BASE =
  process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

export async function apiFetch<T>(path: string, options: RequestInit = {}): Promise<T> {
  const headers = new Headers(options.headers || {});
  const token = getAccessToken();
  const tenantId = getTenantId();
  if (token) {
    headers.set("Authorization", `Bearer ${token}`);
  }
  if (tenantId) {
    headers.set("X-Tenant-Id", tenantId);
  }
  headers.set("Content-Type", headers.get("Content-Type") || "application/json");

  const response = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers
  });

  if (response.status === 401) {
    const refreshed = await refreshToken();
    if (refreshed) {
      return apiFetch<T>(path, options);
    }
  }

  if (!response.ok) {
    throw new Error(await response.text());
  }
  return response.json();
}

async function refreshToken() {
  const tenantId = getTenantId();
  if (!tenantId) {
    return false;
  }
  const refreshToken = localStorage.getItem("refreshToken");
  if (!refreshToken) {
    return false;
  }
  const res = await fetch(`${API_BASE}/auth/refresh`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "X-Tenant-Id": tenantId
    },
    body: JSON.stringify({ refreshToken })
  });
  if (!res.ok) {
    return false;
  }
  const data = await res.json();
  setAccessToken(data.accessToken);
  return true;
}
