"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import { useQuery } from "@tanstack/react-query";
import { apiFetch } from "@/lib/api";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { useToast } from "@/components/ToastProvider";

type Ticket = {
  id: string;
  title: string;
  description: string;
  status: string;
  priority: string;
  assigneeUserId?: string;
  requesterEmail: string;
};

type Comment = {
  id: string;
  body: string;
  authorUserId: string;
  createdAt: string;
};

type Attachment = {
  id: string;
  filename: string;
};

export default function TicketDetailPage() {
  const params = useParams<{ id: string }>();
  const [comment, setComment] = useState("");
  const [status, setStatus] = useState("");
  const toast = useToast();
  const { data: ticket } = useQuery({
    queryKey: ["ticket", params.id],
    queryFn: () => apiFetch<Ticket>(`/tickets/${params.id}`)
  });
  const { data: comments, refetch } = useQuery({
    queryKey: ["ticket-comments", params.id],
    queryFn: () => apiFetch<Comment[]>(`/tickets/${params.id}/comments`)
  });
  const { data: attachments, refetch: refetchAttachments } = useQuery({
    queryKey: ["ticket-attachments", params.id],
    queryFn: () => apiFetch<Attachment[]>(`/tickets/${params.id}/attachments`)
  });

  async function addComment() {
    await apiFetch(`/tickets/${params.id}/comments`, {
      method: "POST",
      body: JSON.stringify({ body: comment })
    });
    setComment("");
    refetch();
  }

  async function uploadAttachment(file: File) {
    const formData = new FormData();
    formData.append("file", file);
    await fetch(
      `${process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080"}/tickets/${params.id}/attachments`,
      {
        method: "POST",
        headers: {
          Authorization: `Bearer ${localStorage.getItem("accessToken") || ""}`,
          "X-Tenant-Id": localStorage.getItem("tenantId") || ""
        },
        body: formData
      }
    );
    refetchAttachments();
  }

  async function updateTicket() {
    await apiFetch(`/tickets/${params.id}`, {
      method: "PATCH",
      body: JSON.stringify({ status: status || ticket?.status })
    });
    toast.push("Ticket updated.");
  }

  if (!ticket) {
    return <div>Loading...</div>;
  }

  useEffect(() => {
    setStatus(ticket.status);
  }, [ticket.status]);

  return (
    <div className="grid gap-6">
      <Card className="p-6">
        <h3 className="font-display text-2xl">{ticket.title}</h3>
        <p className="text-sm text-ink/60">{ticket.requesterEmail}</p>
        <p className="mt-4">{ticket.description}</p>
        <div className="mt-4 text-sm text-ink/60">
          Status: {ticket.status} · Priority: {ticket.priority}
        </div>
        <div className="mt-4 flex flex-wrap items-center gap-3">
          <select
            className="border border-sand rounded-full px-3 py-2 text-sm"
            value={status}
            onChange={(e) => setStatus(e.target.value)}
          >
            <option value="OPEN">OPEN</option>
            <option value="IN_PROGRESS">IN_PROGRESS</option>
            <option value="RESOLVED">RESOLVED</option>
            <option value="CLOSED">CLOSED</option>
          </select>
          <Button variant="outline" onClick={updateTicket}>
            Update
          </Button>
        </div>
      </Card>
      <Card className="p-6">
        <h4 className="font-display text-xl mb-3">Comments</h4>
        <div className="grid gap-3">
          {comments?.map((c) => (
            <div key={c.id} className="border border-sand rounded-2xl p-3">
              <p className="text-sm text-ink/70">{c.body}</p>
              <p className="text-xs text-ink/40 mt-1">{c.authorUserId}</p>
            </div>
          ))}
        </div>
        <div className="mt-4 flex gap-2">
          <input
            className="flex-1 border border-sand rounded-full px-3 py-2"
            placeholder="Add comment"
            value={comment}
            onChange={(e) => setComment(e.target.value)}
          />
          <Button onClick={addComment}>Send</Button>
        </div>
      </Card>
      <Card className="p-6">
        <h4 className="font-display text-xl mb-3">Attachments</h4>
        <input
          type="file"
          onChange={(e) => {
            const file = e.target.files?.[0];
            if (file) {
              uploadAttachment(file);
            }
          }}
        />
        <div className="mt-4 grid gap-2 text-sm">
          {attachments?.map((att) => (
            <a
              key={att.id}
              href={`${process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080"}/attachments/${att.id}/download`}
              className="underline"
            >
              {att.filename}
            </a>
          ))}
        </div>
      </Card>
    </div>
  );
}
