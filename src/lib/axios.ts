import axios from 'axios';


// Channel API instance
const channelApi = axios.create({
  baseURL: process.env.NEXT_PUBLIC_CHANNEL_API_URL || 'http://localhost:8080/channel-service',
  timeout: 5000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Stream API instance
const streamApi = axios.create({
  baseURL: process.env.NEXT_PUBLIC_STREAM_API_URL || 'http://localhost:8081/stream-service',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});



export { channelApi,  streamApi  };
