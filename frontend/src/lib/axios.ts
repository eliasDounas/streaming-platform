import axios, { AxiosInstance, InternalAxiosRequestConfig, AxiosResponse } from 'axios';
import Keycloak from 'keycloak-js';

// Get Keycloak instance (we'll need to import this from the provider)
let keycloakInstance: Keycloak.KeycloakInstance | null = null;

// Function to set Keycloak instance
export const setKeycloakInstance = (keycloak: Keycloak.KeycloakInstance) => {
  keycloakInstance = keycloak;
};

// Function to get the current token
const getAuthToken = (): string | null => {
  return keycloakInstance?.token || null;
};

// Function to get the current user ID
const getUserId = (): string | null => {
  // Try to get user ID from token parsed data
  return keycloakInstance?.tokenParsed?.sub || null;
};

// Channel API instance
const channelApi: AxiosInstance = axios.create({
  baseURL: process.env.NEXT_PUBLIC_CHANNEL_API_URL || 'http://localhost:8080/channel-service',
  timeout: 5000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Stream API instance
const streamApi: AxiosInstance = axios.create({
  baseURL: process.env.NEXT_PUBLIC_STREAM_API_URL || 'http://localhost:8081/stream-service',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add Bearer token to all requests
const addAuthInterceptor = (apiInstance: AxiosInstance) => {
  apiInstance.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
      const token = getAuthToken();
      const userId = getUserId();
      
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
        console.log('🔐 Adding Bearer token to request:', config.url);
      } else {
        console.log('⚠️ No token available for request:', config.url);
      }
      
      if (userId) {
        config.headers['X-User-Id'] = userId;
        console.log('👤 Adding User ID to request:', userId, 'for URL:', config.url);
      } else {
        console.log('⚠️ No user ID available for request:', config.url);
      }
      
      return config;
    },
    (error) => {
      return Promise.reject(error);
    }
  );

  // Response interceptor to handle token refresh if needed
  apiInstance.interceptors.response.use(
    (response: AxiosResponse) => response,
    async (error) => {
      if (error.response?.status === 401 && keycloakInstance) {
        console.log('🔄 Token expired, attempting refresh...');
        try {
          const refreshed = await keycloakInstance.updateToken(30);
          if (refreshed) {
            console.log('✅ Token refreshed successfully');
            // Retry the original request with new token
            error.config.headers.Authorization = `Bearer ${keycloakInstance.token}`;
            return axios.request(error.config);
          }
        } catch {
          console.log('❌ Token refresh failed, redirecting to login');
          keycloakInstance.login();
        }
      }
      return Promise.reject(error);
    }
  );
};

// Add interceptors to both API instances
addAuthInterceptor(channelApi);
addAuthInterceptor(streamApi);

// Export utility functions for manual use if needed
export { getUserId };
export { channelApi, streamApi };
