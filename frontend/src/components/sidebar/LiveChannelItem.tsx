import Link from "next/link"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Dot } from "lucide-react"

type LiveChannelItemProps = {
  channelId: string
  channelName?: string
  avatarUrl?: string
  viewers: number
}

// Helper function to format viewer count
const formatViewerCount = (viewers: number): string => {
  if (viewers >= 1000000) {
    return `${(viewers / 1000000).toFixed(1)}M`;
  } else if (viewers >= 1000) {
    return `${(viewers / 1000).toFixed(1)}K`;
  }
  return viewers.toString();
  
};

export function LiveChannelItem({
  channelId,
  channelName,
  avatarUrl,
  viewers,
}: LiveChannelItemProps) {
  const displayName = channelName || 'Unknown Channel';
  const fallbackLetter = displayName.charAt(0).toUpperCase();

  return (
    <Link href={`/channels/${channelId}`}>
      <div className="flex items-center justify-between pr-3 py-1 hover:bg-muted transition rounded-md cursor-pointer">
        <div className="flex items-center gap-3">
          <Avatar className="w-8 h-8 rounded-lg">
            <AvatarImage src={avatarUrl} alt={displayName} />
            <AvatarFallback>{fallbackLetter}</AvatarFallback>
          </Avatar>
          <div className="flex flex-col">
            <span className="text-sm font-medium">{displayName}</span>
          </div>
        </div>
        <div className="flex items-center text-sm text-muted-foreground">
          <Dot className="text-red-500 fill-red-500 w-6 h-6 -mr-1" />
          <span className="text-xs">{formatViewerCount(viewers)}</span>
        </div>
      </div>
    </Link>
  )
}
