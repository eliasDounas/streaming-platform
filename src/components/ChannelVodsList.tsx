"use client";

import React, { useState } from 'react';
import { Button } from '@/components/ui/button';
import { Card } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Loader2, Play, Clock, Eye, Calendar } from 'lucide-react';
import { useChannelFinishedStreams } from '@/hooks/useSWR';
import Link from 'next/link';

interface ChannelVodsListProps {
  channelId: string;
  channelName?: string;
}

interface StreamItem {
  streamId: string;
  channelId: string;
  title: string;
  description?: string;
  category: string;
  viewers: number;
  thumbnailUrl?: string;
  playbackUrl: string;
  startedAt?: string;
  endedAt?: string;
}

export default function ChannelVodsList({ channelId, channelName }: ChannelVodsListProps) {
  const [currentPage, setCurrentPage] = useState(0);
  const [allStreams, setAllStreams] = useState<StreamItem[]>([]);
  const [hasLoadedInitial, setHasLoadedInitial] = useState(false);
  
  const pageSize = 8;
  const { finishedStreams, isLoading, error, pagination } = useChannelFinishedStreams(channelId, currentPage, pageSize);

  // Accumulate streams when new page loads
  React.useEffect(() => {
    if (finishedStreams && finishedStreams.length > 0) {
      if (currentPage === 0) {
        // First page - replace all streams
        setAllStreams(finishedStreams);
      } else {
        // Subsequent pages - append to existing streams
        setAllStreams(prev => [...prev, ...finishedStreams]);
      }
      setHasLoadedInitial(true);
    }
  }, [finishedStreams, currentPage]);

  const handleLoadMore = () => {
    if (pagination && currentPage < pagination.totalPages - 1) {
      setCurrentPage(prev => prev + 1);
    }
  };

  const formatDuration = (startedAt?: string, endedAt?: string): string => {
    if (!startedAt || !endedAt) return 'Unknown duration';
    
    const start = new Date(startedAt);
    const end = new Date(endedAt);
    const durationMs = end.getTime() - start.getTime();
    const hours = Math.floor(durationMs / (1000 * 60 * 60));
    const minutes = Math.floor((durationMs % (1000 * 60 * 60)) / (1000 * 60));
    
    if (hours > 0) {
      return `${hours}h ${minutes}m`;
    }
    return `${minutes}m`;
  };

  const formatDate = (dateString?: string): string => {
    if (!dateString) return 'Unknown date';
    return new Date(dateString).toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric'
    });
  };

  const formatViewerCount = (count: number): string => {
    if (count >= 1000000) {
      return `${(count / 1000000).toFixed(1)}M`;
    } else if (count >= 1000) {
      return `${(count / 1000).toFixed(1)}K`;
    }
    return count.toString();
  };

  if (error) {
    return (
      <div className="w-full max-w-6xl mx-auto p-6">
        <div className="text-center space-y-3">
          <h3 className="text-lg font-semibold">Unable to load streams</h3>
          <p className="text-muted-foreground">
            There was an error loading the streams for this channel.
          </p>
        </div>
      </div>
    );
  }

  if (isLoading && !hasLoadedInitial) {
    return (
      <div className="w-full max-w-6xl mx-auto p-6">
        <div className="flex items-center justify-center h-64">
          <div className="flex items-center gap-3">
            <Loader2 className="w-6 h-6 animate-spin" />
            <span className="text-lg">Loading streams...</span>
          </div>
        </div>
      </div>
    );
  }

  if (!hasLoadedInitial || allStreams.length === 0) {
    return (
      <div className="w-full max-w-6xl mx-auto p-6">
        <div className="text-center space-y-3">
          <h3 className="text-lg font-semibold">No past streams</h3>
          <p className="text-muted-foreground">
            {channelName ? `${channelName} hasn't` : 'This channel hasn\'t'} streamed anything yet.
          </p>
        </div>
      </div>
    );
  }

  const hasMoreStreams = pagination && currentPage < pagination.totalPages - 1;

  return (
    <div className="w-full max-w-6xl mx-auto space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold">Past Streams</h2>
          {pagination && (
            <p className="text-muted-foreground">
              Showing {allStreams.length} of {pagination.totalElements} streams
            </p>
          )}
        </div>
      </div>

      {/* Stream Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4 mb-4">
        {allStreams.map((stream) => (
          <Card key={stream.streamId} className="group overflow-hidden hover:shadow-lg transition-all duration-300">
            <Link href={`/vods/${stream.streamId}`}>
              <div className="relative aspect-video overflow-hidden">
                <img
                  src={stream.thumbnailUrl || 'https://images.unsplash.com/photo-1606889476054-c73e32ef7ed3?auto=format&fit=crop&w=500&q=80'}
                  alt={stream.title}
                  className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
                />
                
                {/* Duration overlay */}
                <div className="absolute bottom-2 right-2 bg-black/80 text-white text-xs px-2 py-1 rounded">
                  <div className="flex items-center gap-1">
                    <Clock className="w-3 h-3" />
                    {formatDuration(stream.startedAt, stream.endedAt)}
                  </div>
                </div>

                {/* Play button overlay */}
                <div className="absolute inset-0 bg-black/20 opacity-0 group-hover:opacity-100 transition-opacity duration-300 flex items-center justify-center">
                  <div className="bg-white/90 text-black p-3 rounded-full">
                    <Play className="w-6 h-6 fill-current" />
                  </div>
                </div>
              </div>              <div className="p-3 space-y-2">
                {/* Title */}
                <h3 className="font-semibold line-clamp-2 group-hover:text-primary transition-colors text-sm">
                  {stream.title}
                </h3>

                {/* Condensed Footer */}
                <div className="flex items-center justify-between text-xs text-muted-foreground">
                  <div className="flex items-center gap-3">
                    <div className="flex items-center gap-1">
                      <Eye className="w-3 h-3" />
                      <span>{formatViewerCount(stream.viewers)}</span>
                    </div>
                    <div className="flex items-center gap-1">
                      <Calendar className="w-3 h-3" />
                      <span>{formatDate(stream.startedAt)}</span>
                    </div>
                  </div>
                  <Badge variant="secondary" className="text-xs py-0 px-2 h-5">
                    {stream.category}
                  </Badge>
                </div>
              </div>
            </Link>
          </Card>
        ))}
      </div>

      {/* Load More Button */}
      {hasMoreStreams && (
        <div className="flex justify-center pt-6">
          <Button
            onClick={handleLoadMore}
            disabled={isLoading}
            variant="outline"
            className="px-8 mb-8"
          >
            {isLoading ? (
              <>
                <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                Loading...
              </>
            ) : (
              'Show More'
            )}
          </Button>
        </div>
      )}
    </div>
  );
}
