"use client"

import { useKeycloak } from '@react-keycloak/web'
import { useCallback, useEffect } from 'react'
import { useUserStore } from '@/store/userStore'

export function useAuth() {
  const { keycloak, initialized } = useKeycloak()
  const { userInfo, setUserInfo, clearUserInfo } = useUserStore()

  // Sync user info with store when authentication state changes
  useEffect(() => {
    if (keycloak.authenticated && keycloak.tokenParsed) {
      const userInfo = {
        id: keycloak.tokenParsed.sub!,
        username: keycloak.tokenParsed.preferred_username,
        email: keycloak.tokenParsed.email,
        name: keycloak.tokenParsed.name,
        roles: keycloak.realmAccess?.roles || [],
      }
      
      // Log user ID when successfully authenticated
      console.log('🎉 User successfully authenticated! User ID:', userInfo.id)
      console.log('📋 Full user info:', userInfo)
      
      setUserInfo(userInfo)
    } else {
      clearUserInfo()
      console.log('🚪 User logged out or not authenticated')
    }
  }, [keycloak.authenticated, keycloak.tokenParsed, keycloak.realmAccess, setUserInfo, clearUserInfo])

  const login = useCallback(() => {
    keycloak.login()
  }, [keycloak])

  const logout = useCallback(() => {
    keycloak.logout()
  }, [keycloak])

  const register = useCallback(() => {
    keycloak.register()
  }, [keycloak])

  const getToken = useCallback(() => {
    return keycloak.token
  }, [keycloak.token])

  const getUserInfo = useCallback(() => {
    if (!keycloak.authenticated) return null
    
    // Return from store if available, otherwise parse from token
    if (userInfo) return userInfo
    
    return {
      id: keycloak.tokenParsed?.sub || '',
      username: keycloak.tokenParsed?.preferred_username,
      email: keycloak.tokenParsed?.email,
      name: keycloak.tokenParsed?.name,
      roles: keycloak.realmAccess?.roles || [],
    }
  }, [keycloak.authenticated, keycloak.tokenParsed, keycloak.realmAccess, userInfo])

  const getUserId = useCallback(() => {
    return userInfo?.id || keycloak.tokenParsed?.sub || null
  }, [userInfo, keycloak.tokenParsed])

  const hasRole = useCallback((role: string) => {
    return keycloak.hasRealmRole(role)
  }, [keycloak])

  return {
    isAuthenticated: keycloak.authenticated || false,
    isLoading: !initialized,
    userInfo, // Add userInfo from store
    login,
    logout,
    register,
    getToken,
    getUserInfo,
    getUserId, // Add convenience method for user ID
    hasRole,
    keycloak,
  }
}
