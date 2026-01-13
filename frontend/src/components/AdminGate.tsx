"use client";

import { useEffect, useState } from "react";
import { getRole } from "@/lib/user";

export default function AdminGate({ children }: { children: React.ReactNode }) {
  const [allowed, setAllowed] = useState(false);

  useEffect(() => {
    const role = getRole();
    setAllowed(role === "TENANT_ADMIN");
  }, []);

  if (!allowed) {
    return <div className="p-6">Admin access required.</div>;
  }

  return <>{children}</>;
}
