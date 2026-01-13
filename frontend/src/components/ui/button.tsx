import * as React from "react";
import { cn } from "@/lib/utils";

export interface ButtonProps
  extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: "primary" | "ghost" | "outline";
}

const styles: Record<string, string> = {
  primary:
    "bg-ink text-canvas hover:bg-signal hover:text-white transition-colors",
  ghost: "bg-transparent text-ink hover:bg-sand transition-colors",
  outline:
    "border border-ink text-ink hover:bg-ink hover:text-canvas transition-colors"
};

export function Button({
  className,
  variant = "primary",
  ...props
}: ButtonProps) {
  return (
    <button
      className={cn(
        "px-4 py-2 rounded-full text-sm font-semibold",
        styles[variant],
        className
      )}
      {...props}
    />
  );
}
