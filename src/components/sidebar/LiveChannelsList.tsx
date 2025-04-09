"use client"

import { LiveChannelItem } from "./LiveChannelItem"
import { SidebarGroup, SidebarGroupLabel, SidebarMenu } from "@/components/ui/sidebar"

const mockChannels = [
  {
    username: "nasdas_off",
    category: "Just Chatting",
    viewers: "79.6K",
    avatarUrl: "https://static-cdn.jtvnw.net/jtv_user_pictures/6b34e7ac-cdde-4fd3-9e1e-547f2b3d0e2a-profile_image-70x70.png",
  },
  {
    username: "pokimane",
    category: "League of Legends",
    viewers: "52.3K",
    avatarUrl: "https://static-cdn.jtvnw.net/jtv_user_pictures/pokimane-profile_image.png",
  },
  {
    username: "xqc",
    category: "Valorant",
    viewers: "104K",
    avatarUrl: "https://static-cdn.jtvnw.net/jtv_user_pictures/xqc-profile_image.png",
  },
  {
    username: "kai_cenat",
    category: "GTA V",
    viewers: "91.1K",
    avatarUrl: "https://static-cdn.jtvnw.net/jtv_user_pictures/kai_cenat-profile_image.png",
  },
]

export function LiveChannelsList() {
  return (
    <SidebarGroup>
      <SidebarGroupLabel>Live Channels</SidebarGroupLabel>
      <SidebarMenu>
        {mockChannels.map((channel) => (
          <LiveChannelItem
            key={channel.username}
            avatarUrl={channel.avatarUrl}
            username={channel.username}
            category={channel.category}
            viewers={channel.viewers}
          />
        ))}
      </SidebarMenu>
    </SidebarGroup>
  )
}
