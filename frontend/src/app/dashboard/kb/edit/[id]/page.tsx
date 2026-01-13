"use client";

import { useState } from "react";
import { useParams } from "next/navigation";
import { apiFetch } from "@/lib/api";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";

export default function ArticleEditPage() {
  const params = useParams<{ id: string }>();
  const [content, setContent] = useState("");
  const [status, setStatus] = useState<string | null>(null);

  async function saveVersion() {
    await apiFetch(`/kb/articles/${params.id}/versions`, {
      method: "POST",
      body: JSON.stringify({ content })
    });
    setStatus("Draft saved");
  }

  async function publish() {
    await apiFetch(`/kb/articles/${params.id}/publish`, {
      method: "POST",
      body: JSON.stringify({})
    });
    setStatus("Published");
  }

  return (
    <div className="grid gap-6">
      <Card className="p-6">
        <h2 className="font-display text-2xl">Edit Article</h2>
        <textarea
          className="w-full border border-sand rounded-2xl p-4 mt-4 min-h-[240px]"
          value={content}
          onChange={(e) => setContent(e.target.value)}
          placeholder="Write markdown..."
        />
        <div className="mt-4 flex gap-3">
          <Button onClick={saveVersion}>Save Draft</Button>
          <Button variant="outline" onClick={publish}>
            Publish
          </Button>
        </div>
        {status && <p className="text-sm text-ink/60 mt-2">{status}</p>}
      </Card>
    </div>
  );
}
