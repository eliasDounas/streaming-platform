import axios from 'axios';

// Main API instance
const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:3001/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Chat/WebSocket API instance
const chatApi = axios.create({
  baseURL: process.env.NEXT_PUBLIC_CHAT_API_URL || 'http://localhost:3002/chat',
  timeout: 5000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// CDN/Media API instance
const mediaApi = axios.create({
  baseURL: process.env.NEXT_PUBLIC_MEDIA_API_URL || 'https://cdn.example.com',
  timeout: 30000, // Longer timeout for media uploads
  headers: {
    'Content-Type': 'multipart/form-data',
  },
});

// Analytics API instance
const analyticsApi = axios.create({
  baseURL: process.env.NEXT_PUBLIC_ANALYTICS_API_URL || 'https://analytics.example.com/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

export default api;
export { chatApi, mediaApi, analyticsApi };
