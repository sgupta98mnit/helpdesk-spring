import AuthGuard from "@/components/AuthGuard";
import TenantSelector from "@/components/TenantSelector";
import Link from "next/link";

const navItems = [
  { href: "/dashboard", label: "Overview" },
  { href: "/dashboard/tickets", label: "Tickets" },
  { href: "/dashboard/kb", label: "Knowledge Base" },
  { href: "/dashboard/notifications", label: "Notifications" },
  { href: "/dashboard/admin/tenants", label: "Admin · Tenants" },
  { href: "/dashboard/admin/users", label: "Admin · Users" },
  { href: "/dashboard/admin/webhooks", label: "Admin · Webhooks" },
  { href: "/dashboard/admin/audit", label: "Admin · Audit" }
];

export default function DashboardLayout({
  children
}: {
  children: React.ReactNode;
}) {
  return (
    <AuthGuard>
      <div className="min-h-screen grid grid-cols-[260px_1fr]">
        <aside className="bg-ink text-canvas p-6 flex flex-col gap-6">
          <div>
            <p className="text-xs uppercase tracking-[0.3em] text-canvas/60">
              Helpdesk
            </p>
            <h1 className="font-display text-2xl">Command Center</h1>
          </div>
          <nav className="flex flex-col gap-3 text-sm">
            {navItems.map((item) => (
              <Link
                key={item.href}
                href={item.href}
                className="hover:text-sand transition-colors"
              >
                {item.label}
              </Link>
            ))}
          </nav>
        </aside>
        <div>
          <header className="flex items-center justify-between px-8 py-6 border-b border-sand bg-canvas/80">
            <div>
              <h2 className="font-display text-3xl">Workspace</h2>
              <p className="text-sm text-ink/60">
                Multi-tenant service desk console
              </p>
            </div>
            <TenantSelector />
          </header>
          <main className="p-8">{children}</main>
        </div>
      </div>
    </AuthGuard>
  );
}
