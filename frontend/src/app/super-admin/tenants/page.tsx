"use client";

import { useState } from "react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { useToast } from "@/components/ToastProvider";
import { setPlatformKey, getPlatformKey } from "@/lib/platform";

export default function SuperAdminTenantPage() {
  const toast = useToast();
  const [platformKey, setKey] = useState(getPlatformKey() || "");
  const [form, setForm] = useState({
    slug: "",
    name: "",
    adminEmail: "",
    adminPassword: ""
  });

  async function createTenant() {
    setPlatformKey(platformKey);
    const res = await fetch("http://localhost:8080/tenants", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "X-Platform-Admin-Key": platformKey
      },
      body: JSON.stringify(form)
    });
    if (!res.ok) {
      toast.push("Tenant creation failed.");
      return;
    }
    toast.push("Tenant created. You can now log in.");
    setForm({ slug: "", name: "", adminEmail: "", adminPassword: "" });
  }

  return (
    <main className="min-h-screen flex items-center justify-center bg-gradient-to-br from-sand via-canvas to-white">
      <Card className="p-8 w-full max-w-2xl">
        <h1 className="font-display text-3xl mb-2">Platform Admin</h1>
        <p className="text-sm text-ink/60 mb-6">
          Use the platform admin key to provision the first tenant.
        </p>
        <div className="grid gap-3">
          <input
            className="border border-sand rounded-full px-3 py-2"
            placeholder="Platform admin key"
            value={platformKey}
            onChange={(e) => setKey(e.target.value)}
          />
          <input
            className="border border-sand rounded-full px-3 py-2"
            placeholder="Tenant slug"
            value={form.slug}
            onChange={(e) => setForm({ ...form, slug: e.target.value })}
          />
          <input
            className="border border-sand rounded-full px-3 py-2"
            placeholder="Tenant name"
            value={form.name}
            onChange={(e) => setForm({ ...form, name: e.target.value })}
          />
          <input
            className="border border-sand rounded-full px-3 py-2"
            placeholder="Admin email"
            value={form.adminEmail}
            onChange={(e) =>
              setForm({ ...form, adminEmail: e.target.value })
            }
          />
          <input
            className="border border-sand rounded-full px-3 py-2"
            placeholder="Admin password"
            type="password"
            value={form.adminPassword}
            onChange={(e) =>
              setForm({ ...form, adminPassword: e.target.value })
            }
          />
          <Button onClick={createTenant}>Create Tenant</Button>
        </div>
      </Card>
    </main>
  );
}
