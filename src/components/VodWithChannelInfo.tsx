'use client';

import IvsPlayer from './IvsPlayer';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { LiveStreamDto } from '@/types/api';

interface VodWithChannelInfoProps {
  vod: LiveStreamDto; // Reusing LiveStreamDto since VODs use the same structure
}

const VodWithChannelInfo: React.FC<VodWithChannelInfoProps> = ({ vod }) => {
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

  return (
    <div className="w-full max-w-6xl mx-auto space-y-4">
      {/* Video */}
      <div className="aspect-video rounded-xl overflow-hidden shadow-lg">
        <IvsPlayer playbackUrl={vod.playbackUrl} />
      </div>

      {/* VOD title */}
      <h1 className="text-2xl md:text-3xl font-bold tracking-tight mt-2 text-black dark:text-white">{vod.title}</h1>

      {/* Channel info and actions */}
      <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div className="flex items-center gap-4">
          <Avatar className="h-14 w-14">
            <AvatarImage src={vod.avatarUrl} alt={vod.channelName} />
            <AvatarFallback>{vod.channelName[0]}</AvatarFallback>
          </Avatar>
          <div>
            <div className="flex items-center gap-2">
              <span className="font-semibold text-lg text-black dark:text-white">{vod.channelName}</span>
            </div>
            <p className="text-xs text-muted-foreground">{vod.description}</p>
            <div className="flex items-center gap-3 text-xs text-gray-500 mt-1">
              <span>{formatViewerCount(vod.viewers)} views</span>
              <span>•</span>
              <span>Recorded</span>
            </div>
          </div>
        </div>
        <div className="flex items-center gap-2">
          <Button className="bg-red-600 hover:bg-red-700 text-white font-semibold rounded-full px-6 py-2" disabled>
            Follow
          </Button>
          <Button variant="secondary" className="rounded-full px-6 py-2" disabled>
            Share
          </Button>
        </div>
      </div>

      {/* Description */}
      <Card className="p-4 mt-2 bg-pink-50 dark:border-pink-900 dark:bg-neutral-950 dark:border-4 hidden lg:block">
        <h3 className="font-semibold text-sm mb-2 text-black dark:text-white">About this video</h3>
        <p className="text-sm text-gray-800 dark:text-white">
          {vod.description || "Check out this incredible recorded session! Perfect for learning new techniques and improving your own gameplay."}
        </p>
      </Card>
    </div>
  );
};

export default VodWithChannelInfo;
