"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { apiFetch } from "@/lib/api";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { useToast } from "@/components/ToastProvider";

export default function TicketCreatePage() {
  const router = useRouter();
  const toast = useToast();
  const [form, setForm] = useState({
    title: "",
    description: "",
    requesterEmail: "",
    priority: "MEDIUM"
  });

  async function submit() {
    try {
      const idempotencyKey = crypto.randomUUID();
      const ticket = await apiFetch<{ id: string }>("/tickets", {
        method: "POST",
        headers: { "Idempotency-Key": idempotencyKey },
        body: JSON.stringify(form)
      });
      toast.push("Ticket created.");
      router.push(`/dashboard/tickets/${ticket.id}`);
    } catch (err) {
      toast.push("Failed to create ticket.");
    }
  }

  return (
    <Card className="p-6 max-w-xl">
      <h2 className="font-display text-2xl mb-4">Create Ticket</h2>
      <div className="grid gap-3">
        <input
          className="border border-sand rounded-full px-3 py-2"
          placeholder="Title"
          value={form.title}
          onChange={(e) => setForm({ ...form, title: e.target.value })}
        />
        <textarea
          className="border border-sand rounded-2xl px-3 py-2 min-h-[140px]"
          placeholder="Description"
          value={form.description}
          onChange={(e) => setForm({ ...form, description: e.target.value })}
        />
        <input
          className="border border-sand rounded-full px-3 py-2"
          placeholder="Requester Email"
          value={form.requesterEmail}
          onChange={(e) => setForm({ ...form, requesterEmail: e.target.value })}
        />
        <select
          className="border border-sand rounded-full px-3 py-2"
          value={form.priority}
          onChange={(e) => setForm({ ...form, priority: e.target.value })}
        >
          <option value="LOW">LOW</option>
          <option value="MEDIUM">MEDIUM</option>
          <option value="HIGH">HIGH</option>
          <option value="URGENT">URGENT</option>
        </select>
        <Button onClick={submit}>Create</Button>
      </div>
    </Card>
  );
}
