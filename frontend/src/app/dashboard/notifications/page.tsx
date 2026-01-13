"use client";

import { useQuery } from "@tanstack/react-query";
import { apiFetch } from "@/lib/api";
import { Card } from "@/components/ui/card";

type Notification = {
  id: string;
  message: string;
  type: string;
  read: boolean;
};

export default function NotificationsPage() {
  const { data } = useQuery({
    queryKey: ["notifications"],
    queryFn: () => apiFetch<Notification[]>("/notifications")
  });

  return (
    <Card className="p-6">
      <h2 className="font-display text-2xl mb-4">Notifications</h2>
      <div className="grid gap-3">
        {data?.map((note) => (
          <div
            key={note.id}
            className="border border-sand rounded-2xl p-3"
          >
            <p className="text-sm">{note.message}</p>
            <p className="text-xs text-ink/40">{note.type}</p>
          </div>
        ))}
      </div>
    </Card>
  );
}
