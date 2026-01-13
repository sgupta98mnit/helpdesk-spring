import * as React from "react";
import { cn } from "@/lib/utils";

export function Card({
  className,
  ...props
}: React.HTMLAttributes<HTMLDivElement>) {
  return (
    <div
      className={cn(
        "rounded-3xl bg-white/70 border border-sand shadow-[0_10px_30px_rgba(0,0,0,0.08)]",
        className
      )}
      {...props}
    />
  );
}
