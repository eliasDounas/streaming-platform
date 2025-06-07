// Zustand auth store with Axios integration
import { create } from 'zustand';
import { devtools, persist } from 'zustand/middleware';
import { User } from '@/types/api';

interface AuthState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  updateProfile: (updates: Partial<User>) => Promise<void>;
}

// // Auth Store
// export const useAuthStore = create<AuthState>()(
//   devtools(
//     persist(
//       (set, get) => ({
//         user: null,
//         token: null,
//         isAuthenticated: false,

//         login: async (email: string, password: string) => {
//           try {
//             const response = await api.post('/auth/login', { email, password });
//             const { user, token } = response.data;
            
//             // Set token in axios headers for future requests
//             api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
            
//             set({ user, token, isAuthenticated: true });
//           } catch (error) {
//             console.error('Login failed:', error);
//             throw error;
//           }
//         },

//         logout: () => {
//           // Remove token from axios headers
//           delete api.defaults.headers.common['Authorization'];
//           set({ user: null, token: null, isAuthenticated: false });
//         },

//         updateProfile: async (updates: Partial<User>) => {
//           const { user } = get();
//           if (!user) return;

//           try {
//             const response = await api.put(`/users/${user.id}`, updates);
//             set({ user: response.data });
//           } catch (error) {
//             console.error('Profile update failed:', error);
//             throw error;
//           }
//         },
//       }),
//       {
//         name: 'auth-storage',
//         partialize: (state) => ({ 
//           user: state.user, 
//           token: state.token, 
//           isAuthenticated: state.isAuthenticated 
//         }),
//       }
//     ),
//     { name: 'auth-store' }
//   )
// );

// // Initialize auth token on app start
// const initializeAuth = () => {
//   const token = useAuthStore.getState().token;
//   if (token) {
//     api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
//   }
// };

// Call this in your app initialization
// initializeAuth();
