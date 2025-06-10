"use client"

import { useKeycloak } from '@react-keycloak/web'
import { useCallback } from 'react'
import { useUserStore } from '@/store/userStore'

export function useAuth() {
  const { keycloak, initialized } = useKeycloak()
  const { userInfo, clearUserInfo } = useUserStore()

  const login = useCallback(() => {
    keycloak.login()
  }, [keycloak])

  const logout = useCallback(() => {
    clearUserInfo()
    keycloak.logout()
  }, [keycloak, clearUserInfo])

  const register = useCallback(() => {
    keycloak.register()
  }, [keycloak])

  const getToken = useCallback(() => {
    return keycloak.token
  }, [keycloak.token])

  const getUserInfo = useCallback(() => {
    // Return userInfo from the store if available, otherwise fall back to keycloak data
    if (userInfo) {
      return userInfo
    }
    
    if (!keycloak.authenticated) return null
    
    return {
      id: keycloak.tokenParsed?.sub || '',
      username: keycloak.tokenParsed?.preferred_username,
      email: keycloak.tokenParsed?.email,
      name: keycloak.tokenParsed?.name,
      roles: keycloak.realmAccess?.roles || [],
    }
  }, [keycloak.authenticated, keycloak.tokenParsed, keycloak.realmAccess, userInfo])

  const hasRole = useCallback((role: string) => {
    // Check from store first, then fall back to keycloak
    if (userInfo) {
      return userInfo.roles.includes(role)
    }
    return keycloak.hasRealmRole(role)
  }, [keycloak, userInfo])

  const getUsername = useCallback(() => {
    return userInfo?.username || keycloak.tokenParsed?.preferred_username || null
  }, [userInfo, keycloak.tokenParsed])

  const getUserId = useCallback(() => {
    return userInfo?.id || keycloak.tokenParsed?.sub || null
  }, [userInfo, keycloak.tokenParsed])

  return {
    isAuthenticated: keycloak.authenticated || false,
    isLoading: !initialized,
    login,
    logout,
    register,
    getToken,
    getUserInfo,
    getUsername,
    getUserId,
    hasRole,
    keycloak,
    userInfo, // Direct access to user store data
  }
}
