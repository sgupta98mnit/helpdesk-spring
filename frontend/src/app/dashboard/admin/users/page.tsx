"use client";

import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { apiFetch } from "@/lib/api";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import AdminGate from "@/components/AdminGate";

type User = {
  id: string;
  email: string;
  role: string;
};

export default function AdminUsersPage() {
  const { data, refetch } = useQuery({
    queryKey: ["admin-users"],
    queryFn: () => apiFetch<User[]>("/admin/users")
  });
  const [form, setForm] = useState({ email: "", role: "AGENT", password: "" });

  async function invite() {
    await apiFetch<User>("/admin/users", {
      method: "POST",
      body: JSON.stringify(form)
    });
    setForm({ email: "", role: "AGENT", password: "" });
    refetch();
  }

  return (
    <AdminGate>
      <div className="grid gap-6">
        <Card className="p-6">
          <h2 className="font-display text-2xl mb-4">Invite User</h2>
          <div className="grid gap-3">
            <input
              className="border border-sand rounded-full px-3 py-2"
              placeholder="Email"
              value={form.email}
              onChange={(e) => setForm({ ...form, email: e.target.value })}
            />
            <input
              className="border border-sand rounded-full px-3 py-2"
              placeholder="Temp password"
              value={form.password}
              onChange={(e) => setForm({ ...form, password: e.target.value })}
            />
            <select
              className="border border-sand rounded-full px-3 py-2"
              value={form.role}
              onChange={(e) => setForm({ ...form, role: e.target.value })}
            >
              <option value="TENANT_ADMIN">TENANT_ADMIN</option>
              <option value="AGENT">AGENT</option>
              <option value="VIEWER">VIEWER</option>
            </select>
            <Button onClick={invite}>Invite</Button>
          </div>
        </Card>
        <Card className="p-6">
          <h3 className="font-display text-xl mb-3">Users</h3>
          <div className="divide-y divide-sand">
            {data?.map((user) => (
              <div key={user.id} className="flex justify-between py-2 text-sm">
                <span>{user.email}</span>
                <span className="text-ink/60">{user.role}</span>
              </div>
            ))}
          </div>
        </Card>
      </div>
    </AdminGate>
  );
}
