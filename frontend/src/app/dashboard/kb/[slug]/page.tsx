"use client";

import { useParams } from "next/navigation";
import { useQuery } from "@tanstack/react-query";
import { apiFetch } from "@/lib/api";
import { Card } from "@/components/ui/card";
import { marked } from "marked";

type Article = {
  id: string;
  title: string;
  slug: string;
  status: string;
  currentVersion?: number;
};

type ArticleVersion = {
  content: string;
  versionNumber: number;
};

export default function ArticleDetailPage() {
  const params = useParams<{ slug: string }>();
  const { data: article } = useQuery({
    queryKey: ["kb-article", params.slug],
    queryFn: () => apiFetch<Article>(`/kb/articles/${params.slug}`)
  });
  const { data: versions } = useQuery({
    queryKey: ["kb-versions", article?.id],
    queryFn: () => apiFetch<ArticleVersion[]>(`/kb/articles/${article?.id}/versions`),
    enabled: !!article?.id
  });

  const content = versions?.[0]?.content || "";
  const html = marked.parse(content || "");

  if (!article) {
    return <div>Loading...</div>;
  }

  return (
    <div className="grid gap-6">
      <Card className="p-6">
        <h2 className="font-display text-3xl">{article.title}</h2>
        <p className="text-xs text-ink/50">{article.slug}</p>
      </Card>
      <Card className="p-6 prose prose-slate max-w-none">
        <div dangerouslySetInnerHTML={{ __html: html }} />
      </Card>
    </div>
  );
}
