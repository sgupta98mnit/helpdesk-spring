import Link from "next/link";
import { Button } from "@/components/ui/button";

export default function NotFound() {
  return (
    <main className="min-h-screen flex flex-col items-center justify-center gap-4">
      <h1 className="font-display text-3xl">Page not found</h1>
      <Link href="/">
        <Button>Back home</Button>
      </Link>
    </main>
  );
}
