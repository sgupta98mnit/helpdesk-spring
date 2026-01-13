"use client";

import { useState } from "react";
import Link from "next/link";
import { useQuery } from "@tanstack/react-query";
import { apiFetch } from "@/lib/api";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";

type Article = {
  id: string;
  title: string;
  slug: string;
  status: string;
};

export default function KnowledgeBasePage() {
  const [q, setQ] = useState("");
  const [status, setStatus] = useState("");
  const { data, isLoading } = useQuery({
    queryKey: ["kb", q, status],
    queryFn: () =>
      apiFetch<{ content: Article[] }>(
        `/kb/articles?status=${status || ""}&q=${q || ""}`
      )
  });

  return (
    <div className="grid gap-6">
      <Card className="p-4 flex gap-3">
        <input
          className="border border-sand rounded-full px-3 py-1"
          placeholder="Search articles"
          value={q}
          onChange={(e) => setQ(e.target.value)}
        />
        <select
          className="border border-sand rounded-full px-3 py-1"
          value={status}
          onChange={(e) => setStatus(e.target.value)}
        >
          <option value="">All</option>
          <option value="DRAFT">Draft</option>
          <option value="PUBLISHED">Published</option>
        </select>
        <Link href="/dashboard/kb/new">
          <Button variant="outline">New Article</Button>
        </Link>
      </Card>
      <Card className="p-4">
        {isLoading && <p>Loading articles...</p>}
        <div className="divide-y divide-sand">
          {data?.content?.map((article) => (
            <Link
              key={article.id}
              href={`/dashboard/kb/${article.slug}`}
              className="flex items-center justify-between py-4"
            >
              <div>
                <p className="font-medium">{article.title}</p>
                <p className="text-xs text-ink/50">{article.slug}</p>
              </div>
              <span className="text-xs uppercase tracking-[0.2em]">
                {article.status}
              </span>
            </Link>
          ))}
        </div>
      </Card>
    </div>
  );
}
