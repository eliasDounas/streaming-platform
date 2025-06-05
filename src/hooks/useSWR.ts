// SWR configuration with Axios
import useSWR from 'swr';
import api from '@/lib/api';
import { Stream, Channel } from '@/types/api';

// Generic fetcher function for SWR
const fetcher = (url: string) => api.get(url).then(res => res.data);

// Custom hooks using SWR + Axios
export function useStreams() {
  const { data, error, isLoading, mutate } = useSWR<Stream[]>('/streams', fetcher, {
    refreshInterval: 30000, // Refresh every 30 seconds for live data
    revalidateOnFocus: true,
    revalidateOnReconnect: true,
  });

  return {
    streams: data || [],
    isLoading,
    error,
    refresh: mutate
  };
}

export function useStream(streamId: string) {
  const { data, error, isLoading, mutate } = useSWR<Stream>(
    streamId ? `/streams/${streamId}` : null,
    fetcher,
    {
      refreshInterval: 10000, // Refresh every 10 seconds for live stream data
    }
  );

  return {
    stream: data,
    isLoading,
    error,
    refresh: mutate
  };
}

export function useChannel(channelId: string) {
  const { data, error, isLoading, mutate } = useSWR<Channel>(
    channelId ? `/channels/${channelId}` : null,
    fetcher
  );

  return {
    channel: data,
    isLoading,
    error,
    refresh: mutate
  };
}


// For mutations (POST, PUT, DELETE)
export async function createStream(streamData: Partial<Stream>) {
  const response = await api.post('/streams', streamData);
  return response.data;
}

export async function updateStream(streamId: string, updates: Partial<Stream>) {
  const response = await api.put(`/streams/${streamId}`, updates);
  return response.data;
}

export async function deleteStream(streamId: string) {
  await api.delete(`/streams/${streamId}`);
}
