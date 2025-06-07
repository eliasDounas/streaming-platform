import React from 'react';
import { Avatar, AvatarImage, AvatarFallback } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import LiveCardList from '@/components/LiveCardList';
import IvsPlayer from '../IvsPlayer';
import ChannelVodsList from '../ChannelVodsList';
import { Calendar, Heart, Share2, Loader2 } from 'lucide-react';
import { usePublicChannelInfo } from '@/hooks/useSWR';

interface ChannelProps {
  channelId?: string;
  channel?: {
    channelId?: string;
    name: string;
    description: string;
    isLive?: boolean;
    playbackUrl?: string;
    avatarUrl: string;
    createdAt?: string;
  };
}

// Mock URLs for fallback only
const mockUrls = {
  avatarUrl: 'https://i.pravatar.cc/150',
  bannerUrl: 'https://images.unsplash.com/photo-1464983953574-0892a716854b?auto=format&fit=crop&w=1200&q=80',
};

const Channel: React.FC<ChannelProps> = ({ channelId, channel: channelProp }) => {
  // Use the hook to fetch channel data if channelId is provided
  const { channelInfo, isLoading, error } = usePublicChannelInfo(channelId || null);
  
  // Use API data first, fallback to prop, then show error state if no data
  const apiChannel = channelInfo || channelProp;  // Create channel object with mock URLs as fallbacks only for avatar and banner
  const channel = apiChannel ? {
    ...apiChannel,
    avatarUrl: apiChannel.avatarUrl || mockUrls.avatarUrl,
    bannerUrl: mockUrls.bannerUrl, // Always use mock banner since API doesn't provide it
  } : null;  // Helper function to format date from createdAt
  const formatCreatedDate = (dateString: string): string => {
    try {
      // Parse the LocalDateTime from API (ISO format)
      const date = new Date(dateString);
      if (isNaN(date.getTime())) {
        return 'Unknown'; // Return fallback if can't parse
      }
      
      return date.toLocaleDateString('en-US', { 
        month: 'long', 
        year: 'numeric' 
      });
    } catch {
      return 'Unknown'; // Return fallback if any error
    }
  };// Show loading state
  if (channelId && isLoading) {
    return (
      <div className="w-full max-w-6xl mx-auto space-y-6">
        <div className="flex items-center justify-center h-64">
          <div className="flex items-center gap-3">
            <Loader2 className="w-6 h-6 animate-spin" />
            <span className="text-lg">Loading channel...</span>
          </div>
        </div>
      </div>
    );
  }

  // Show error state if no channel data is available
  if (!channel) {
    return (
      <div className="w-full max-w-6xl mx-auto space-y-6">
        <div className="flex items-center justify-center h-64">
          <div className="text-center space-y-3">
            <h2 className="text-2xl font-bold">Channel not found</h2>
            <p className="text-muted-foreground">The channel you're looking for doesn't exist or is unavailable.</p>
          </div>
        </div>
      </div>
    );  }

  return (
    <div className="w-full max-w-6xl mx-auto space-y-6">{/* Enhanced Banner Section */}
      <div className="relative w-full h-64 md:h-80 overflow-hidden rounded-2xl shadow-2xl">        <img 
          src={channel.bannerUrl} 
          alt="Channel Banner" 
          className="w-full h-full object-cover"
        />
        {/* Gradient overlay for better text readability */}
        <div className="absolute inset-0 bg-gradient-to-t dark:from-black/70 dark:via-black/20 to-transparent from white/70 via-white/20" />
      </div>

      {/* Channel Info - Integrated into page */}
      <div className="mx-4 md:mx-8 -mt-3">
          <div className="flex flex-col md:flex-row items-start md:items-center gap-6">
            {/* Enhanced Avatar Section */}
            <div className="relative">
              <div className="relative">
                <Avatar className={`h-28 w-28 md:h-32 md:w-32 ring-4 ring-background shadow-xl ${
                  channel.isLive ? 'ring-red-500 ring-offset-4 ring-offset-background' : 'ring-border'
                }`}>
                  <AvatarImage src={channel.avatarUrl} alt={channel.name} className="object-cover" />
                  <AvatarFallback className="bg-gradient-to-br from-primary to-primary/70 text-primary-foreground text-2xl font-bold">
                    {channel.name[0]}
                  </AvatarFallback>
                </Avatar>
                
                {/* Live indicator on avatar */}
                {channel.isLive && (
                  <div className="absolute -bottom-2 -right-2">
                    <div className="bg-red-500 text-white text-xs font-bold px-3 py-1 rounded-full shadow-lg border-2 border-background">
                      LIVE
                    </div>
                  </div>
                )}
              </div>
            </div>

            {/* Enhanced Channel Details */}
            <div className="flex-1 min-w-0 space-y-4">
              {/* Name and Category */}              <div className="space-y-2">
                <div className="flex flex-col sm:flex-row sm:items-center gap-3">
                  <h1 className="text-3xl md:text-4xl font-bold bg-gradient-to-r from-foreground to-foreground/70 bg-clip-text text-transparent">
                    {channel.name}
                  </h1>
                </div>
                <p className="text-muted-foreground text-lg leading-relaxed max-w-2xl">
                  {channel.description}
                </p>
              </div>              {/* Enhanced Stats */}
              <div className="flex flex-wrap items-center gap-6 text-sm">
                {channel.createdAt && (
                  <div className="flex items-center gap-2">
                    <Calendar className="w-4 h-4 text-primary" />
                    <span className="text-muted-foreground">Joined {formatCreatedDate(channel.createdAt)}</span>
                  </div>
                )}
              </div>{/* Enhanced Action Buttons */}
              <div className="flex flex-wrap gap-2 pt-2">
                <Button className="bg-red-600 hover:bg-red-700 text-white font-semibold px-8 py-3 rounded-full shadow-lg hover:shadow-xl transition-all duration-300 hover:scale-105">
                  <Heart className="w-4 h-4 mr-1" />
                  Follow
                </Button>
                <Button variant="ghost" className="px-6 py-3 rounded-full hover:bg-primary/10 transition-all duration-300">
                  <Share2 className="w-4 h-4 mr-2" />
                  Share
                </Button>
              </div>            </div>
          </div>
        </div>

      {/* Enhanced Live Stream Section */}
      {channel.isLive && (
        <div className="space-y-4">
          <div className="flex items-center gap-3">
            <h2 className="text-2xl font-bold">Currently Streaming</h2>
          </div>
          
            <IvsPlayer playbackUrl={channel.playbackUrl || ''} />
        </div>
      )}      {/* Enhanced Recent Broadcasts Section */}
      <ChannelVodsList channelId={channelId || channel.channelId || ''} channelName={channel.name} />
    </div>
  );
};

export default Channel;
