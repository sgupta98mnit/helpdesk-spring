"use client";

import { useEffect, useState } from "react";
import { Card } from "@/components/ui/card";
import { API_BASE } from "@/lib/api";

type DemoCredentials = {
  tenantSlug: string;
  email: string;
  password: string;
};

export default function DemoCredentialsCard() {
  const [data, setData] = useState<DemoCredentials | null>(null);

  useEffect(() => {
    fetch(`${API_BASE}/public/demo-credentials`)
      .then((res) => (res.ok ? res.json() : null))
      .then((payload) => {
        if (payload && payload.tenantSlug) {
          setData(payload);
        }
      })
      .catch(() => undefined);
  }, []);

  if (!data) {
    return null;
  }

  return (
    <Card className="p-4 text-left max-w-md mx-auto">
      <p className="text-xs uppercase tracking-[0.2em] text-ink/50">
        Demo Credentials
      </p>
      <div className="mt-3 text-sm text-ink/80">
        <div>Tenant: {data.tenantSlug}</div>
        <div>Email: {data.email}</div>
        <div>Password: {data.password}</div>
      </div>
    </Card>
  );
}
