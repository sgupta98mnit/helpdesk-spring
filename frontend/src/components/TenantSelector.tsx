"use client";

import { useEffect, useState } from "react";
import { setTenantId, getTenantId } from "@/lib/auth";

export default function TenantSelector() {
  const [value, setValue] = useState("");

  useEffect(() => {
    setValue(getTenantId() || "");
  }, []);

  return (
    <div className="flex items-center gap-2">
      <span className="text-xs uppercase tracking-[0.2em] text-ink/60">
        Tenant
      </span>
      <input
        className="bg-white/80 border border-sand rounded-full px-3 py-1 text-sm"
        placeholder="tenant slug"
        value={value}
        onChange={(e) => {
          const next = e.target.value;
          setValue(next);
          setTenantId(next);
        }}
      />
    </div>
  );
}
