"use client"

import React from 'react';
// SWR configuration with Axios
import useSWR from 'swr';
import { channelApi, streamApi } from '@/lib/api';
import { Stream, Channel, ChannelPreviewDTO, StreamConnectionInfo, PaginatedResponse, StreamWithChannelDto } from '@/types/api';


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
      shouldRetryOnError: false, // Don't retry on errors
      errorRetryCount: 0, // No retries
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
      shouldRetryOnError: false, // Don't retry on errors
      errorRetryCount: 0, // No retries
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
export function useUserChannel() {
  const { data, error, isLoading, mutate } = useSWR<ChannelPreviewDTO>(
    `channels/my-channel`,
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

// Hook for getting stream connection info for streamers
export function useStreamConnectionInfo() {
  const { data, error, isLoading, mutate } = useSWR<StreamConnectionInfo>(
    `/channels/connection-info` ,
    channelFetcher,
    {
      revalidateOnFocus: false,
      revalidateOnReconnect: false,
      shouldRetryOnError: false,
      errorRetryCount: 0,
    }
  );

  return {
    connectionInfo: data,
    isLoading,
    error,
    refresh: mutate
  };
}

// New hook for individual stream by ID using streamApi
export function useStream(streamId: string | null) {
  const { data, error, isLoading, mutate } = useSWR<{stream: any, channel: any}>(
    streamId ? `/public/livestream/${streamId}` : null,
    streamFetcher,
    {
      revalidateOnFocus: false,
      revalidateOnReconnect: false,
      shouldRetryOnError: false, // Don't retry on errors
      errorRetryCount: 0, // No retries
    }
  );

  // Transform the single stream data to LiveStreamDto format
  const stream = React.useMemo(() => {
    if (!data) return null;
    
    return {
      streamId: data.stream.id.toString(),
      channelId: data.stream.channelId,
      title: data.stream.title,
      description: data.stream.description,
      category: data.stream.category,
      viewers: data.stream.viewers,
      isLive: data.stream.isLive,
      thumbnailUrl: data.stream.thumbnailUrl,
      channelName: data.channel.name,
      avatarUrl: data.channel.avatarUrl,
      playbackUrl: data.channel.playbackUrl,
    };
  }, [data]);

  return {
    stream,
    isLoading,
    error,
    refresh: mutate
  };
}

// Safe fetcher for public channel info that handles string responses
const safeChannelFetcher = async (url: string) => {
  try {
    const response = await channelApi.get(url);
    const data = response.data;
    
    // Check if response is a string (likely a token) instead of expected object
    if (typeof data === 'string') {
      console.warn('Received string response instead of object:', data.substring(0, 50) + '...');
      throw new Error('Invalid response format: expected object but got string');
    }
    
    return data;
  } catch (error) {
    console.error('Error fetching channel info:', error);
    throw error;
  }
};

// New hook for public channel info using channelApi
export function usePublicChannelInfo(channelId: string | null) {
  const { data, error, isLoading, mutate } = useSWR<any>(
    channelId ? `/public/${channelId}` : null,
    safeChannelFetcher,
    {
      revalidateOnFocus: false,
      revalidateOnReconnect: false,
      shouldRetryOnError: false, // Don't retry on errors
      errorRetryCount: 0, // No retries
      refreshInterval: 10000, // Refresh every 10 seconds
    }
  );

  // Transform the channel data to match the expected format
  const channelInfo = React.useMemo(() => {
    if (!data) return null;
    
    // Additional safety check in case data is not the expected object
    if (typeof data !== 'object' || data === null) {
      console.warn('Channel data is not an object:', data);
      return null;
    }
    
    return {
      channelId: data.channelId,
      name: data.name,
      description: data.description,
      isLive: data.isLive,
      playbackUrl: data.playbackUrl,
      avatarUrl: data.avatarUrl || '',
      createdAt: data.createdAt,
    };
  }, [data]);

  return {
    channelInfo,
    isLoading,
    error,
    refresh: mutate
  };
}

// New hook for channel's finished streams using streamApi
export function useChannelFinishedStreams(channelId: string | null, page: number = 0, size: number = 10) {
  const { data, error, isLoading, mutate } = useSWR<{
    content: any[];
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
  }>(
    channelId ? `/public/channels/${channelId}/finished?page=${page}&size=${size}` : null,
    streamFetcher,
    {
      revalidateOnFocus: false,
      revalidateOnReconnect: false,
      shouldRetryOnError: false,
      errorRetryCount: 0,
    }
  );

  // Transform the data to match expected format
  const finishedStreams = React.useMemo(() => {
    if (!data?.content) return [];
    
    return data.content.map(stream => ({
      streamId: stream.id,
      channelId: stream.channelId,
      title: stream.title,
      description: stream.description,
      category: stream.category,
      viewers: stream.viewers,
      isLive: false, // Finished streams are never live
      thumbnailUrl: stream.thumbnailUrl,
      playbackUrl: stream.vodUrl || '',
      startedAt: stream.startedAt,
      endedAt: stream.endedAt,
    }));
  }, [data]);

  return {
    finishedStreams,
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

// Chat token hook for AWS IVS Chat
export const useChatToken = (channelId: string | null) => {
  const { data, error, isLoading } = useSWR(
    channelId ? `/channels/${channelId}/chatroom/token` : null,
    channelFetcher,
    {
      revalidateOnFocus: false,
      revalidateOnReconnect: true,
      dedupingInterval: 5 * 60 * 1000, // 5 minutes - since token has long session
    }
  );

  return {
    chatToken: data?.token,
    isLoading,
    error,
  };
};