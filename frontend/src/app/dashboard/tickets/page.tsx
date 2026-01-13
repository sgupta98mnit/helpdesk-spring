"use client";

import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { apiFetch } from "@/lib/api";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import Link from "next/link";

type Ticket = {
  id: string;
  title: string;
  status: string;
  priority: string;
  assigneeUserId?: string;
  updatedAt: string;
};

export default function TicketListPage() {
  const [status, setStatus] = useState("");
  const [q, setQ] = useState("");
  const { data, isLoading } = useQuery({
    queryKey: ["tickets", status, q],
    queryFn: () =>
      apiFetch<{ content: Ticket[] }>(
        `/tickets?status=${status || ""}&q=${q || ""}`
      )
  });

  return (
    <div className="grid gap-6">
      <Card className="p-4 flex flex-wrap items-center gap-3">
        <input
          className="border border-sand rounded-full px-3 py-1"
          placeholder="Search"
          value={q}
          onChange={(e) => setQ(e.target.value)}
        />
        <select
          className="border border-sand rounded-full px-3 py-1"
          value={status}
          onChange={(e) => setStatus(e.target.value)}
        >
          <option value="">All</option>
          <option value="OPEN">Open</option>
          <option value="IN_PROGRESS">In Progress</option>
          <option value="RESOLVED">Resolved</option>
          <option value="CLOSED">Closed</option>
        </select>
        <Link href="/dashboard/tickets/new">
          <Button variant="outline">New Ticket</Button>
        </Link>
      </Card>
      <Card className="p-4">
        {isLoading && <p>Loading tickets...</p>}
        {!isLoading && data?.content?.length === 0 && <p>No tickets yet.</p>}
        <div className="divide-y divide-sand">
          {data?.content?.map((ticket) => (
            <Link
              key={ticket.id}
              href={`/dashboard/tickets/${ticket.id}`}
              className="flex items-center justify-between py-4"
            >
              <div>
                <p className="font-medium">{ticket.title}</p>
                <p className="text-xs text-ink/60">{ticket.id}</p>
              </div>
              <div className="text-right text-sm">
                <p>{ticket.status}</p>
                <p className="text-ink/50">{ticket.priority}</p>
              </div>
            </Link>
          ))}
        </div>
      </Card>
    </div>
  );
}
