// API service functions for mutations and direct calls
import { channelApi, streamApi } from '@/lib/api';
import { ChannelPreviewDTO, LiveStreamDto } from '@/types/api';

// ========== STREAM SERVICES ==========
export const streamServices = {
  // Get live streams using streamApi
  async getLiveStreams(): Promise<LiveStreamDto[]> {
    const response = await streamApi.get('/live');
    return response.data;
  },
};

// ========== CHANNEL SERVICES ==========
export const channelServices = {
  // Get live channels using channelApi
  async getLiveChannels(): Promise<ChannelPreviewDTO[]> {
    const response = await channelApi.get('/live');
    return response.data;
  },

} 