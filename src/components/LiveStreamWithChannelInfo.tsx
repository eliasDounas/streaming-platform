'use client';

import IvsPlayer from './IvsPlayer';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';

interface LiveStreamWithChannelInfoProps {
  playbackUrl: string;
}

const LiveStreamWithChannelInfo: React.FC<LiveStreamWithChannelInfoProps> = ({ playbackUrl }) => {
  // Boilerplate/mock data
  const stream = {
    title: '🔥 Grinding Ranked | Apex Legends Live',
    views: 18231,
  };

  const channel = {
    avatarUrl: 'https://i.pravatar.cc/150',
    name: 'GamerX',
    description: 'Competitive FPS streamer. Join the grind!',
    started: '2h ago',
  };

  return (
    <div className="w-full mx-4 xl:mx-6 3xl:mx-8 space-y-4">
      {/* Video */}
      <div className="aspect-video rounded-xl overflow-hidden shadow-lg">
        <IvsPlayer playbackUrl={playbackUrl} />
      </div>

      {/* Stream title */}
      <h1 className="text-2xl md:text-3xl font-bold tracking-tight mt-2 text-black dark:text-white">{stream.title}</h1>

      {/* Channel info and actions */}
      <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div className="flex items-center gap-4">
          <Avatar className="h-14 w-14">
            <AvatarImage src={channel.avatarUrl} alt={channel.name} />
            <AvatarFallback>{channel.name[0]}</AvatarFallback>
          </Avatar>
          <div>
            <div className="flex items-center gap-2">
              <span className="font-semibold text-lg text-black dark:text-white">{channel.name}</span>
            </div>
            <p className="text-xs text-muted-foreground">{channel.description}</p>
            <span className="text-xs text-gray-500">Started {channel.started}</span>
          </div>
        </div>
        <div className="flex flex-col gap-4 mb-4">
            <div className="text-sm text-right mr-2 text-gray-600">
                <span>{stream.views.toLocaleString()} watching</span>
            </div>
            <div className="flex items-center gap-2">
            <Button className="bg-red-600 hover:bg-red-700 text-white font-semibold rounded-full px-6 py-2" disabled>Follow</Button>
            <Button variant="secondary" className="rounded-full px-6 py-2" disabled>Share</Button>
            
            </div>
        
        </div>
      </div>

      {/* Description (expandable in real YouTube) */}
      <Card className="p-3 mt-2 bg-pink-50 dark:border-pink-900 dark:bg-neutral-950 dark:border-4 text-center hidden lg:block">
        <p className="text-sm text-gray-800 dark:text-white">Welcome to the stream! Chat, have fun, and enjoy the gameplay. Don’t forget to subscribe for more live action!
        </p>
        
      </Card>
    </div>
  );
};

export default LiveStreamWithChannelInfo;
