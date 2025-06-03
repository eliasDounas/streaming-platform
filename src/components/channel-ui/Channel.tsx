import React from 'react';
import { Avatar, AvatarImage, AvatarFallback } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import LiveCardList from '@/components/LiveCardList';
import IvsPlayer from '../IvsPlayer';

interface ChannelProps {
  channel?: {
    avatarUrl: string;
    name: string;
    description: string;
    subscribers: string;
    isLive?: boolean;
    bannerUrl?: string;
  };
}

const mockChannel = {
  avatarUrl: 'https://i.pravatar.cc/150',
  name: 'GamerX',
  description: 'Competitive FPS streamer. Join the grind!',
  subscribers: '120K',
  isLive: true,
  bannerUrl: 'https://images.unsplash.com/photo-1464983953574-0892a716854b?auto=format&fit=crop&w=1200&q=80',
};

const Channel: React.FC<ChannelProps> = ({ channel = mockChannel }) => {
  return (
    <div className="w-full max-w-5xl mx-auto space-y-6 border-none shadow-none">
      {/* Banner */}
      {channel.bannerUrl && (
        <div className="w-full md:h-52 overflow-hidden mb-2">
          <img src={channel.bannerUrl} alt="Banner" className="w-full h-full object-cover" />
        </div>
      )}
      {/* Channel Card */}
      <Card className="-mt-4 flex flex-col md:flex-row items-center gap-6 rounded-none shadow-none border-none px-6 bg-transparent">
        <div className="relative mb-4 md:mb-0">
          <Avatar className={`h-24 w-24 ${channel.isLive ? 'ring-4 ring-red-600 ring-offset-2 ring-offset-background' : ''}`}>
            <AvatarImage src={channel.avatarUrl} alt={channel.name} />
            <AvatarFallback>{channel.name[0]}</AvatarFallback>
          </Avatar>
          {channel.isLive && (
            <span className="absolute left-1/2 -translate-x-1/2 bottom-0 translate-y-1/2 bg-red-500 text-white text-xs font-bold px-3 py-1 rounded-md shadow-md border-2 border-white">LIVE</span>
          )}
        </div>
        <div className="flex-1 min-w-0 w-full">
          <div className="flex items-center gap-2 mb-1">
            <h2 className="text-2xl font-bold truncate">{channel.name}</h2>
          </div>
          <p className="text-sm text-muted-foreground mb-2 truncate">{channel.description}</p>
          {/* Subscribe Button */}
          <div className="mt-4">
            <Button disabled className="text-white bg-red-600 font-semibold rounded-full px-6 py-2 w-full md:w-auto">Follow</Button>
          </div>
        </div>
      </Card>
      {/* Currently Streaming */}
      <IvsPlayer playbackUrl={''}  />
      {/* Recent Broadcasts */}
      <div className="mt-8">
        <h3 className="text-lg font-semibold mb-3">Recent Broadcasts</h3>
        {/* Fake data for recent broadcasts */}
        <LiveCardList />
      </div>
    </div>
  );
};

export default Channel;
