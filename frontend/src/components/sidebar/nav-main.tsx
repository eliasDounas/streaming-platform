"use client"

import Link from "next/link"
import { usePathname } from "next/navigation"
import {
  Collapsible,
  CollapsibleContent,
  CollapsibleTrigger,
} from "@/components/ui/collapsible"
import {
  SidebarGroup,
  SidebarGroupLabel,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarMenuSub,
  SidebarMenuSubButton,
  SidebarMenuSubItem,
} from "@/components/ui/sidebar"
import { Badge } from "@/components/ui/badge"

import {
  ChevronRight,
  House,
  Heart,
  Calendar,
  Rss,
} from "lucide-react"

const items = [
  {
    title: "Home",
    url: "/",
    icon: House,
  },  {
    title: "Following",
    url: "#",
    icon: Heart,
    badge: "Soon",
  },
  {
    title: "Events",
    url: "#",
    icon: Calendar,
    badge: "Soon",
  },
  {
    title: "Blog",
    url: "#",
    icon: Rss,
    items: [
      {
        title: "Game Reviews",
        url: "/blogs/reviews",
      },
      {
        title: "Gaming News",
        url: "/blogs/news",
      },
      {
        title: "Create Blog",
        url: "/blogs/create",
      },
    ],
        
  },
]

export function NavMain() {
  const pathname = usePathname()
  
  return (
    <SidebarGroup>
      <SidebarGroupLabel>Platform</SidebarGroupLabel>
      <SidebarMenu>
        {items.map((item) => {
          const isActive = item.url === pathname
          const hasActiveChild = item.items?.some((subItem) => pathname.startsWith(subItem.url))
          
          return item.items && item.items.length > 0 ? (
            <Collapsible
              key={item.title}
              asChild
              defaultOpen={hasActiveChild}
              className="group/collapsible"
            >
              <SidebarMenuItem>
                <CollapsibleTrigger asChild>
                  <SidebarMenuButton tooltip={item.title}>
                    {item.icon && <item.icon />}
                    <span className={hasActiveChild ? "font-semibold" : ""}>{item.title}</span>
                    <ChevronRight className="ml-auto transition-transform duration-200 group-data-[state=open]/collapsible:rotate-90" />
                  </SidebarMenuButton>
                </CollapsibleTrigger>
                <CollapsibleContent>
                  <SidebarMenuSub>
                    {item.items.map((subItem) => {
                      const isSubItemActive = pathname === subItem.url
                      return (
                        <SidebarMenuSubItem key={subItem.title}>
                          <SidebarMenuSubButton asChild isActive={isSubItemActive}>
                            <Link href={subItem.url}>
                              <span className={isSubItemActive ? "font-semibold" : ""}>{subItem.title}</span>
                            </Link>
                          </SidebarMenuSubButton>
                        </SidebarMenuSubItem>
                      )
                    })}
                  </SidebarMenuSub>
                </CollapsibleContent>
              </SidebarMenuItem>
            </Collapsible>
          ) : (            
          <SidebarMenuItem key={item.title}>
              <SidebarMenuButton
                asChild
                tooltip={item.title}
                isActive={isActive}
              >
                <Link 
                  href={item.url} 
                  className="flex items-center gap-2 w-full"
                  onClick={(e) => {
                    if (item.url === "#") {
                      e.preventDefault();
                    }
                  }}
                >
                  {item.icon && <item.icon />}
                  <span className={isActive ? "font-semibold" : ""}>{item.title}</span>
                  {item.badge && (
                    <Badge variant="coming_soon" className="ml-auto text-[10px] px-1.5 py-0.5">
                      {item.badge}
                    </Badge>
                  )}
                </Link>
              </SidebarMenuButton>
            </SidebarMenuItem>
          )
        })}
      </SidebarMenu>
    </SidebarGroup>
  )
}
