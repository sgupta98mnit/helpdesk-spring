export function setAccessToken(token: string) {
  if (typeof window !== "undefined") {
    localStorage.setItem("accessToken", token);
  }
}

export function getAccessToken() {
  if (typeof window !== "undefined") {
    return localStorage.getItem("accessToken");
  }
  return null;
}

export function setRefreshToken(token: string) {
  if (typeof window !== "undefined") {
    localStorage.setItem("refreshToken", token);
  }
}

export function clearTokens() {
  if (typeof window !== "undefined") {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
  }
}

export function setTenantId(tenantId: string) {
  if (typeof window !== "undefined") {
    localStorage.setItem("tenantId", tenantId);
  }
}

export function getTenantId() {
  if (typeof window !== "undefined") {
    return localStorage.getItem("tenantId");
  }
  return null;
}
