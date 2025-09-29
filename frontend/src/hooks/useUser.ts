"use client"

import { useUserStore } from '@/store/userStore'
import { useAuth } from './useAuth'

/**
 * Custom hook to get user data from the store
 * This provides easy access to user information without going through Keycloak
 */
export function useUser() {
  const { userInfo } = useUserStore()
  const { isAuthenticated, isLoading } = useAuth()

  return {
    user: userInfo,
    username: userInfo?.username || null,
    userId: userInfo?.id || null,
    email: userInfo?.email || null,
    name: userInfo?.name || null,
    roles: userInfo?.roles || [],
    isAuthenticated,
    isLoading,
    hasRole: (role: string) => userInfo?.roles.includes(role) || false,
  }
}
