export function setPlatformKey(key: string) {
  if (typeof window !== "undefined") {
    localStorage.setItem("platformAdminKey", key);
  }
}

export function getPlatformKey() {
  if (typeof window !== "undefined") {
    return localStorage.getItem("platformAdminKey");
  }
  return null;
}
