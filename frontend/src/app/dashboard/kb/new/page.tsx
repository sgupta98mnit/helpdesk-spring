"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { apiFetch } from "@/lib/api";
import { useToast } from "@/components/ToastProvider";

export default function NewArticlePage() {
  const router = useRouter();
  const toast = useToast();
  const [form, setForm] = useState({
    title: "",
    slug: "",
    content: ""
  });

  async function create() {
    try {
      const article = await apiFetch<{ id: string; slug: string }>(
        "/kb/articles",
        {
          method: "POST",
          body: JSON.stringify(form)
        }
      );
      toast.push("Article created.");
      router.push(`/dashboard/kb/${article.slug}`);
    } catch (err) {
      toast.push("Failed to create article.");
    }
  }

  return (
    <Card className="p-6 max-w-2xl">
      <h2 className="font-display text-2xl mb-4">New Knowledge Article</h2>
      <div className="grid gap-3">
        <input
          className="border border-sand rounded-full px-3 py-2"
          placeholder="Title"
          value={form.title}
          onChange={(e) => setForm({ ...form, title: e.target.value })}
        />
        <input
          className="border border-sand rounded-full px-3 py-2"
          placeholder="Slug"
          value={form.slug}
          onChange={(e) => setForm({ ...form, slug: e.target.value })}
        />
        <textarea
          className="border border-sand rounded-2xl px-3 py-2 min-h-[200px]"
          placeholder="Markdown content"
          value={form.content}
          onChange={(e) => setForm({ ...form, content: e.target.value })}
        />
        <Button onClick={create}>Create Article</Button>
      </div>
    </Card>
  );
}
