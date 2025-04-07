"use client"
 
import { useState, useEffect } from "react";
import { ThemeProvider as NextThemesProvider } from "next-themes"
 

export function ThemeProvider({
  children,
  ...props
}: React.ComponentProps<typeof NextThemesProvider>) {
    const [mounted, setMounted] = useState(false);

  useEffect(() => {
    setMounted(true);
  }, []);

  if (!mounted) {
    return <>{children}</>; // Render children without ThemeProvider during SSR
  }
  
  return <NextThemesProvider {...props} attribute="class">{children}</NextThemesProvider>
}