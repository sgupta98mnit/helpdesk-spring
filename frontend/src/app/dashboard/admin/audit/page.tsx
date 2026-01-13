"use client";

import { useQuery } from "@tanstack/react-query";
import { apiFetch } from "@/lib/api";
import { Card } from "@/components/ui/card";
import AdminGate from "@/components/AdminGate";

type AuditLog = {
  id: string;
  action: string;
  resourceType: string;
  resourceId: string;
  actorUserId: string;
  createdAt: string;
};

export default function AuditPage() {
  const { data } = useQuery({
    queryKey: ["audit"],
    queryFn: () => apiFetch<{ content: AuditLog[] }>("/audit")
  });

  return (
    <AdminGate>
      <Card className="p-6">
        <h2 className="font-display text-2xl mb-4">Audit Log</h2>
        <div className="divide-y divide-sand">
          {data?.content?.map((log) => (
            <div key={log.id} className="py-3 text-sm">
              <p className="font-medium">
                {log.action} · {log.resourceType}
              </p>
              <p className="text-xs text-ink/50">{log.resourceId}</p>
            </div>
          ))}
        </div>
      </Card>
    </AdminGate>
  );
}
