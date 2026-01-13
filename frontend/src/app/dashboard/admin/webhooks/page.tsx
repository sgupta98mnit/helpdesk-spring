"use client";

import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { apiFetch } from "@/lib/api";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import AdminGate from "@/components/AdminGate";

type Webhook = {
  id: string;
  url: string;
  events: string;
  enabled: boolean;
};

export default function AdminWebhooksPage() {
  const { data, refetch } = useQuery({
    queryKey: ["webhooks"],
    queryFn: () => apiFetch<Webhook[]>("/webhooks")
  });
  const [form, setForm] = useState({ url: "", events: "ticket.created", secret: "" });

  async function create() {
    await apiFetch<Webhook>("/webhooks", {
      method: "POST",
      body: JSON.stringify({ ...form, enabled: true })
    });
    setForm({ url: "", events: "ticket.created", secret: "" });
    refetch();
  }

  async function remove(id: string) {
    await apiFetch(`/webhooks/${id}`, { method: "DELETE" });
    refetch();
  }

  return (
    <AdminGate>
      <div className="grid gap-6">
        <Card className="p-6">
          <h2 className="font-display text-2xl mb-4">Register webhook</h2>
          <div className="grid gap-3">
            <input
              className="border border-sand rounded-full px-3 py-2"
              placeholder="URL"
              value={form.url}
              onChange={(e) => setForm({ ...form, url: e.target.value })}
            />
            <input
              className="border border-sand rounded-full px-3 py-2"
              placeholder="Events (comma separated)"
              value={form.events}
              onChange={(e) => setForm({ ...form, events: e.target.value })}
            />
            <input
              className="border border-sand rounded-full px-3 py-2"
              placeholder="Secret"
              value={form.secret}
              onChange={(e) => setForm({ ...form, secret: e.target.value })}
            />
            <Button onClick={create}>Save Webhook</Button>
          </div>
        </Card>
        <Card className="p-6">
          <h3 className="font-display text-xl mb-3">Active Webhooks</h3>
          <div className="divide-y divide-sand">
            {data?.map((hook) => (
              <div key={hook.id} className="flex items-center justify-between py-2">
                <div>
                  <p className="text-sm">{hook.url}</p>
                  <p className="text-xs text-ink/50">{hook.events}</p>
                </div>
                <Button variant="ghost" onClick={() => remove(hook.id)}>
                  Remove
                </Button>
              </div>
            ))}
          </div>
        </Card>
      </div>
    </AdminGate>
  );
}
