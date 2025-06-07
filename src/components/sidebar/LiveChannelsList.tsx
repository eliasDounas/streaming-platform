"use client"

import React from 'react'
import { LiveChannelItem } from "./LiveChannelItem"
import { SidebarGroup, SidebarGroupLabel, SidebarMenu } from "@/components/ui/sidebar"
import { useLiveStreams } from "@/hooks/useSWR"

export function LiveChannelsList() {
  const { liveStreams, isLoading, error } = useLiveStreams()
  // Extract channel data from live streams (assuming 1:1 relationship)
  const channels = React.useMemo(() => {
    return liveStreams
      .filter(stream => stream.channelId && stream.channelName) // Filter out invalid streams
      .map(stream => ({
        channelId: stream.channelId,
        channelName: stream.channelName,
        avatarUrl: stream.avatarUrl,
        viewers: stream.viewers || 0
      }));
  }, [liveStreams]);

  return (
    <SidebarGroup>
      <SidebarGroupLabel>Live Channels</SidebarGroupLabel>
      <SidebarMenu>
        {isLoading ? (
          <div className="px-3 py-2 text-sm text-muted-foreground">
            Loading live channels...
          </div>        ) : error ? (
          <div className="px-3 py-2 text-sm text-red-500">
            Failed to load channels
          </div>
        ) : channels.length === 0 ? (
          <div className="px-3 py-2 text-sm text-muted-foreground">
            No channels are live at the moment
          </div>        ) : (
          channels.map((channel, index) => (
            <LiveChannelItem
              key={`${channel.channelId}-${index}`}
              channelId={channel.channelId}
              channelName={channel.channelName}
              avatarUrl={channel.avatarUrl}
              viewers={channel.viewers}
            />
          ))
        )}
      </SidebarMenu>
    </SidebarGroup>
  )
}
