import Link from "next/link";
import { Button } from "@/components/ui/button";
import DemoCredentialsCard from "@/components/DemoCredentialsCard";

export default function LandingPage() {
  return (
    <main className="min-h-screen flex items-center justify-center bg-gradient-to-br from-sand via-canvas to-white">
      <div className="text-center max-w-xl">
        <p className="text-xs uppercase tracking-[0.3em] text-ink/60">Helpdesk</p>
        <h1 className="font-display text-5xl mt-2">Serve every tenant in one console.</h1>
        <p className="text-sm text-ink/60 mt-4">
          Ticketing, knowledge, and event automation with isolation built in.
        </p>
        <div className="mt-6 flex justify-center">
          <Link href="/login">
            <Button>Go to Login</Button>
          </Link>
        </div>
        <div className="mt-8">
          <DemoCredentialsCard />
        </div>
      </div>
    </main>
  );
}
