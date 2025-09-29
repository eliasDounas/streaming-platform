import { create } from 'zustand';
import { devtools, persist } from 'zustand/middleware';

interface UserInfo {
  id: string;
  username?: string;
  email?: string;
  name?: string;
  roles: string[];
}

interface UserState {
  userInfo: UserInfo | null;
  setUserInfo: (userInfo: UserInfo | null) => void;
  clearUserInfo: () => void;
  hasRole: (role: string) => boolean;
}

export const useUserStore = create<UserState>()(
  devtools(
    persist(
      (set, get) => ({
        userInfo: null,

        setUserInfo: (userInfo: UserInfo | null) => {
          set({ userInfo });
        },

        clearUserInfo: () => {
          set({ userInfo: null });
        },

        hasRole: (role: string) => {
          const { userInfo } = get();
          return userInfo?.roles.includes(role) || false;
        },
      }),
      {
        name: 'user-storage',
        partialize: (state) => ({ 
          userInfo: state.userInfo 
        }),
      }
    ),
    { name: 'user-store' }
  )
);
