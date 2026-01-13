export function setRole(role: string) {
  if (typeof window !== "undefined") {
    localStorage.setItem("role", role);
  }
}

export function getRole() {
  if (typeof window !== "undefined") {
    return localStorage.getItem("role");
  }
  return null;
}
