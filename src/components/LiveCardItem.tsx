import Image from "next/image";
import Link from "next/link";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Eye } from "lucide-react";
import { LiveStreamDto, StreamCategory } from "@/types/api";

interface LiveCardItemProps {
  stream: LiveStreamDto;
}

// Helper function to get display name for categories
const getCategoryDisplayName = (category: StreamCategory): string => {
  const displayNames: Record<StreamCategory, string> = {
    [StreamCategory.GAMING]: "Gaming",
    [StreamCategory.JUST_CHATTING]: "Just Chatting",
    [StreamCategory.CREATIVE]: "Art & Creative",
    [StreamCategory.SPORTS]: "Sports",
    [StreamCategory.TRAVEL_AND_OUTDOORS]: "Travel & Outdoors",
    [StreamCategory.FOOD_AND_DRINK]: "Food & Drink",
    [StreamCategory.FITNESS_AND_HEALTH]: "Fitness & Health",
    [StreamCategory.SCIENCE_AND_TECHNOLOGY]: "Science & Technology",
    [StreamCategory.EDUCATIONAL]: "Educational",
    [StreamCategory.PODCAST]: "Podcast",
    [StreamCategory.TALK_SHOWS]: "Talk Shows",
    [StreamCategory.ESPORTS]: "Esports",
    [StreamCategory.POLITICS]: "Politics",
    [StreamCategory.ASMR]: "ASMR",
    [StreamCategory.VARIETY]: "Variety",
    [StreamCategory.OTHER]: "Other"
  };
  return displayNames[category];
};

// Helper function to format viewer count
const formatViewerCount = (viewers: number | undefined): string => {
  if (!viewers || viewers < 0) {
    return "0";
  }
  if (viewers >= 1000000) {
    return `${(viewers / 1000000).toFixed(1)}M`;
  } else if (viewers >= 1000) {
    return `${(viewers / 1000).toFixed(1)}K`;
  }
  return viewers.toString();
};

const LiveCardItem = ({ stream }: LiveCardItemProps) => {
  return (
    <div className="group w-full min-w-[320px] max-w-[360px] rounded-xl overflow-hidden bg-card/80 backdrop-blur-sm border border-border/40 shadow-lg hover:shadow-2xl hover:shadow-primary/5 hover:border-primary/20 transition-all duration-500 hover:-translate-y-2 hover:scale-[1.02] relative before:absolute before:inset-0 before:rounded-xl before:bg-gradient-to-br before:from-primary/5 before:to-transparent before:opacity-0 before:transition-opacity before:duration-500 hover:before:opacity-100">
      {/* Clickable thumbnail area - directs to stream */}
      <Link href={`/streams/${stream.streamId}`}>        <div className="relative w-full h-[200px] overflow-hidden rounded-t-xl">
          <Image
            src={stream.thumbnailUrl || "/placeholder-thumbnail.jpg"}
            alt={stream.title || 'Stream thumbnail'}
            fill
            className="object-cover transition-all duration-700 group-hover:scale-110 group-hover:brightness-110"
          />
            {/* Enhanced gradient overlay */}
          <div className="absolute inset-0 bg-gradient-to-t from-black/70 via-black/20 to-transparent" />
          <div className="absolute inset-0 bg-gradient-to-br from-transparent via-transparent to-primary/10 opacity-0 group-hover:opacity-100 transition-opacity duration-500" />          
          {/* Enhanced Live badge with glow effect */}
          {stream.isLive && (
            <div className="absolute top-4 right-4 bg-gradient-to-r from-red-500 to-red-600 text-white text-xs font-bold px-3 py-1 rounded-full shadow-xl border border-red-400/50 animate-pulse">
              <span className="tracking-wide">LIVE</span>
            </div>
          )}
          
          {/* Enhanced Viewer count with animated background */}
          <div className="absolute bottom-4 right-4 bg-black/90 backdrop-blur-md text-white text-sm px-3 py-1 rounded-full flex items-center gap-2.5 border border-white/30 shadow-lg hover:bg-primary/90 hover:border-primary/50 transition-all duration-300 group/viewers">
            <Eye className="w-4 h-4 group-hover/viewers:animate-pulse" />
            <span className="font-semibold tabular-nums">{formatViewerCount(stream.viewers)}</span>
          </div>
          
          {/* Subtle animated border on hover */}
          <div className="absolute inset-0 rounded-t-xl border-2 border-transparent group-hover:border-primary/30 transition-colors duration-500" />
        </div>
      </Link>      {/* Enhanced Stream Info */}
      <div className="relative p-3 space-y-4">
        <div className="flex items-start gap-4">
          {/* Enhanced Clickable avatar */}          <Link href={`/channels/${stream.channelId}`} className="shrink-0 group/avatar">
            <div className="relative">
              <Avatar className="w-12 h-12 cursor-pointer hover:ring-4 hover:ring-primary/30 transition-all duration-300 border-3 border-background shadow-lg group-hover/avatar:scale-110">
                <AvatarImage src={stream.avatarUrl} alt={stream.channelName || 'Channel avatar'} className="transition-all duration-300 group-hover/avatar:brightness-110" />
                <AvatarFallback className="bg-gradient-to-br from-primary/30 to-primary/10 text-primary font-bold text-base">
                  {(stream.channelName || 'U').charAt(0).toUpperCase()}
                </AvatarFallback>
              </Avatar>              {/* Online indicator for live streams */}
              {stream.isLive && (
                <div className="absolute -bottom-1 -right-1 w-4 h-4 bg-green-500 border-3 border-background rounded-full shadow-lg">
                  <div className="w-full h-full bg-green-400 rounded-full animate-ping opacity-75" />
                </div>
              )}
            </div>
          </Link>          <div className="flex-1 min-w-0">            {/* Enhanced Stream title */}
            <Link href={`/streams/${stream.streamId}`}>
              <h3 className="font-bold text-foreground leading-snug group-hover:text-primary transition-colors duration-300 text-base hover:underline decoration-primary/50 decoration-2 underline-offset-2 truncate">
                {stream.title || 'Untitled Stream'}
              </h3>
            </Link>
            
            <div className="space-y-0.5">
              {/* Enhanced Channel name */}            <Link 
                href={`/channels/${stream.channelId}`}
                className="text-sm text-muted-foreground hover:text-primary transition-colors duration-300 block truncate font-medium hover:underline decoration-primary/50"
              >
                {stream.channelName || 'Unknown Channel'}
              </Link>
              
              {/* Category text */}
              <p className="text-sm text-muted-foreground">
                {getCategoryDisplayName(stream.category)}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LiveCardItem;