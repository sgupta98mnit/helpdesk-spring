"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { z } from "zod";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { apiFetch } from "@/lib/api";
import { setAccessToken, setRefreshToken, setTenantId } from "@/lib/auth";
import { setRole } from "@/lib/user";
import { useToast } from "@/components/ToastProvider";
import DemoCredentialsCard from "@/components/DemoCredentialsCard";

const schema = z.object({
  tenant: z.string().min(2),
  email: z.string().email(),
  password: z.string().min(6)
});

export default function LoginPage() {
  const router = useRouter();
  const [form, setForm] = useState({ tenant: "", email: "", password: "" });
  const [error, setError] = useState<string | null>(null);
  const toast = useToast();

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();
    const result = schema.safeParse(form);
    if (!result.success) {
      setError("Enter tenant, email, and password.");
      return;
    }
    try {
      setTenantId(form.tenant);
      const response = await apiFetch<{ accessToken: string; refreshToken: string }>(
        "/auth/login",
        {
          method: "POST",
          body: JSON.stringify({ email: form.email, password: form.password })
        }
      );
      setAccessToken(response.accessToken);
      setRefreshToken(response.refreshToken);
      const me = await apiFetch<{ role: string }>("/auth/me");
      setRole(me.role);
      router.push("/dashboard");
    } catch (err) {
      setError("Login failed.");
      toast.push("Login failed.");
    }
  }

  return (
    <main className="min-h-screen flex items-center justify-center bg-gradient-to-br from-sand via-canvas to-white">
      <Card className="w-[420px] p-8">
        <h1 className="font-display text-3xl mb-2">Welcome back</h1>
        <p className="text-sm text-ink/60 mb-6">
          Sign in to manage tickets and knowledge.
        </p>
        <form onSubmit={onSubmit} className="flex flex-col gap-4">
          <input
            className="border border-sand rounded-full px-4 py-2"
            placeholder="Tenant slug"
            value={form.tenant}
            onChange={(e) => setForm({ ...form, tenant: e.target.value })}
          />
          <input
            className="border border-sand rounded-full px-4 py-2"
            placeholder="Email"
            value={form.email}
            onChange={(e) => setForm({ ...form, email: e.target.value })}
          />
          <input
            className="border border-sand rounded-full px-4 py-2"
            type="password"
            placeholder="Password"
            value={form.password}
            onChange={(e) => setForm({ ...form, password: e.target.value })}
          />
          {error && <p className="text-sm text-signal">{error}</p>}
          <Button type="submit">Sign In</Button>
        </form>
        <div className="mt-6">
          <DemoCredentialsCard />
        </div>
      </Card>
    </main>
  );
}
