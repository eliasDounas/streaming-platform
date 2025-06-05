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
  startedAt: string;
  endedAt?: string;
  channelId: string;
  channel: Channel;
  createdAt: string;
  updatedAt: string;
}

// Channel types
export interface Channel {
  id: string;
  name: string;
  description?: string;
  avatarUrl?: string;
  bannerUrl?: string;
  isLive: boolean;
  followers: number;
  userId: string;
  user: User;
  createdAt: string;
  updatedAt: string;
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
  data: T[];
  meta: {
    page: number;
    limit: number;
    total: number;
    totalPages: number;
  };
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
