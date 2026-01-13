"use client";

import { Card } from "@/components/ui/card";
import { useQuery } from "@tanstack/react-query";
import { apiFetch } from "@/lib/api";

type Stats = {
  openTickets: number;
  unassignedTickets: number;
  publishedArticles: number;
};

export default function DashboardPage() {
  const { data } = useQuery({
    queryKey: ["dashboard-stats"],
    queryFn: () => apiFetch<Stats>("/dashboard/stats")
  });
  return (
    <div className="grid gap-6">
      <section className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {[
          { label: "Open Tickets", value: data?.openTickets ?? 0 },
          { label: "Unassigned", value: data?.unassignedTickets ?? 0 },
          { label: "Published Articles", value: data?.publishedArticles ?? 0 }
        ].map((stat) => (
          <Card key={stat.label} className="p-6">
            <p className="text-xs uppercase tracking-[0.2em] text-ink/50">
              {stat.label}
            </p>
            <h3 className="font-display text-4xl mt-3">{stat.value}</h3>
          </Card>
        ))}
      </section>
      <Card className="p-6">
        <h3 className="font-display text-2xl mb-2">Today's pulse</h3>
        <p className="text-sm text-ink/70">
          Track ticket flow, knowledge coverage, and SLA commitments in one view.
        </p>
      </Card>
    </div>
  );
}
