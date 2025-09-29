"use client"

import {
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from "@/components/ui/sidebar"

import {
  Cast
} from "lucide-react"

export function TeamSwitcher() {

  return (
    <SidebarMenu>
      <SidebarMenuItem>
       
            <SidebarMenuButton
              size="lg"
              className="data-[state=open]:bg-sidebar-accent data-[state=open]:text-sidebar-accent-foreground"
            >
              <div className="bg-sidebar-primary text-sidebar-primary-foreground flex aspect-square size-8 items-center justify-center rounded-lg">
                <Cast className="size-4" />
              </div>
              <div className="grid flex-1 text-left text-sm leading-tight">
                <span className="truncate font-medium">Live Streaming</span>
                <span className="truncate text-xs">Beta</span>
              </div>
            </SidebarMenuButton>
            </SidebarMenuItem>
    </SidebarMenu>
  )
}
