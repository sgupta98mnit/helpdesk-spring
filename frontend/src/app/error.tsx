"use client";

import { Button } from "@/components/ui/button";

export default function GlobalError({ reset }: { reset: () => void }) {
  return (
    <main className="min-h-screen flex flex-col items-center justify-center gap-4">
      <h1 className="font-display text-3xl">Something went wrong</h1>
      <Button onClick={reset}>Try again</Button>
    </main>
  );
}
