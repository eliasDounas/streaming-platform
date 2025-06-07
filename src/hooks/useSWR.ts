"use client"

import React from 'react';
// SWR configuration with Axios
import useSWR from 'swr';
import { channelApi, streamApi } from '@/lib/api';
import { Stream, Channel, ChannelPreviewDTO,  PaginatedResponse, StreamWithChannelDto } from '@/types/api';


// Channel API fetcher
const channelFetcher = (url: string) => channelApi.get(url).then(res => res.data);

// Stream API fetcher
const streamFetcher = (url: string) => streamApi.get(url).then(res => res.data);


// New hook for live streams using streamApi
export function useLiveStreams() {
  const { data, error, isLoading, mutate } = useSWR<{stream: any, channel: any}[]>(
    '/public/livestreams',
    streamFetcher,
    {
      revalidateOnFocus: false,
      revalidateOnReconnect: false,
    }
  );

  // Transform the nested data structure to flat LiveStreamDto format
  const liveStreams = React.useMemo(() => {
    if (!data) return [];
    
    return data.map(item => ({
      streamId: item.stream.id.toString(),
      channelId: item.stream.channelId,
      title: item.stream.title,
      description: item.stream.description,
      category: item.stream.category,
      viewers: item.stream.viewers,
      isLive: item.stream.isLive,
      thumbnailUrl: item.stream.thumbnailUrl,
      channelName: item.channel.name,
      avatarUrl: item.channel.avatarUrl,
      playbackUrl: item.channel.playbackUrl,
    }));
  }, [data]);

  return {
    liveStreams,
    isLoading,
    error,
    refresh: mutate
  };
}

// New hook for past streams using streamApi
export function usePastStreams(page: number = 0, size: number = 10) {
  const { data, error, isLoading, mutate } = useSWR<PaginatedResponse<StreamWithChannelDto>>(
    `/public/vods/popular?page=${page}&size=${size}`,
    streamFetcher,
    {
      revalidateOnFocus: false,
      revalidateOnReconnect: false,
    }
  );

  // Transform the data to match LiveStreamDto format for compatibility with LiveCardItem
  const pastStreams = React.useMemo(() => {
    if (!data?.content) return [];
    
    return data.content.map(item => ({
      streamId: item.stream.id,
      channelId: item.stream.channelId,
      title: item.stream.title,
      description: item.stream.description,
      category: item.stream.category,
      viewers: item.stream.viewers,
      isLive: false, // Past streams are never live
      thumbnailUrl: item.stream.thumbnailUrl,
      channelName: item.channel.name,
      avatarUrl: item.channel.avatarUrl,
      playbackUrl: item.stream.vodUrl || '', // Use VOD URL for past streams
    }));
  }, [data]);
  return {
    pastStreams,
    isLoading,
    error,
    refresh: mutate,
    pagination: data ? {
      page: data.page,
      size: data.size,
      totalElements: data.totalElements,
      totalPages: data.totalPages,
    } : null,
  };
}

// New hook for user's channel status
export function useUserChannel(userId: string) {
  const { data, error, isLoading, mutate } = useSWR<ChannelPreviewDTO>(
    userId ? `/user/${userId}` : null,
    channelFetcher,
    {
      revalidateOnFocus: false,
      revalidateOnReconnect: true,
      shouldRetryOnError: false, // Don't retry on 404 (no channel)
    }
  );

  return {
    userChannel: data,
    isLoading,
    error,
    refresh: mutate
  };
}