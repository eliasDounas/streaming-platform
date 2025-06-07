// Auth types
export interface User {
  id: string;
  email: string;
  username: string;
  avatarUrl?: string;
  createdAt: string;
  updatedAt: string;
}

export interface AuthResponse {
  user: User;
  token: string;
  refreshToken: string;
}

// Stream types
export interface Stream {
  id: string;
  title: string;
  description?: string;
  playbackUrl: string;
  thumbnailUrl?: string;
  isLive: boolean;
  viewers: number;
  channel: Channel;
}

// Stream category enum
export enum StreamCategory {
  GAMING = 'GAMING',
  JUST_CHATTING = 'JUST_CHATTING',
  CREATIVE = 'CREATIVE',
  SPORTS = 'SPORTS',
  TRAVEL_AND_OUTDOORS = 'TRAVEL_AND_OUTDOORS',
  FOOD_AND_DRINK = 'FOOD_AND_DRINK',
  FITNESS_AND_HEALTH = 'FITNESS_AND_HEALTH',
  SCIENCE_AND_TECHNOLOGY = 'SCIENCE_AND_TECHNOLOGY',
  EDUCATIONAL = 'EDUCATIONAL',
  PODCAST = 'PODCAST',
  TALK_SHOWS = 'TALK_SHOWS',
  ESPORTS = 'ESPORTS',
  POLITICS = 'POLITICS',
  ASMR = 'ASMR',
  VARIETY = 'VARIETY',
  OTHER = 'OTHER'
}

// Live stream with channel DTO for /streams/live endpoint
export interface LiveStreamDto {
  streamId: string;
  channelId: string;
  title: string;
  description?: string;
  category: StreamCategory;
  viewers: number;
  isLive: boolean;
  thumbnailUrl?: string;
  channelName: string;
  avatarUrl?: string;
  playbackUrl: string;
}

export interface Channel {
  id: string;
  name: string;
  description?: string;
  avatarUrl?: string;
  bannerUrl?: string;
  isLive: boolean;
  followers: number;
}

// DTO for live channels preview
export interface ChannelPreviewDTO {
  channelId: string;
  name: string;
  playbackUrl: string;
  avatarUrl?: string;
}

// Stream connection info for streamers
export interface StreamConnectionInfo {
  channelId: string;
  streamKey: string;
  ingestEndpoint: string;
}

// Blog types
export interface Blog {
  id: string;
  title: string;
  content: string;
  excerpt?: string;
  thumbnailUrl?: string;
  category: 'news' | 'reviews' | 'guides';
  published: boolean;
  views: number;
  authorId: string;
  author: User;
  createdAt: string;
  updatedAt: string;
}

// Chat types
export interface ChatMessage {
  id: string;
  content: string;
  streamId: string;
  userId: string;
  user: User;
  createdAt: string;
}

// API Response types
export interface ApiResponse<T> {
  data: T;
  message?: string;
  status: number;
}

export interface PaginatedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

// Error types
export interface ApiError {
  message: string;
  status: number;
  code?: string;
  details?: any;
}

// Upload types
export interface UploadResponse {
  url: string;
  filename: string;
  size: number;
  mimeType: string;
}

// Stream entity for past streams API
export interface StreamEntity {
  id: string;
  channelId: string;
  awsStreamId?: string;
  title: string;
  thumbnailUrl?: string;
  isLive: boolean;
  viewers: number;
  description?: string;
  startedAt: string;
  endedAt?: string;
  category: StreamCategory;
  vodUrl?: string;
}

// Stream with channel DTO for /streams/finished/popular endpoint
export interface StreamWithChannelDto {
  stream: StreamEntity;
  channel: ChannelPreviewDTO;
}
