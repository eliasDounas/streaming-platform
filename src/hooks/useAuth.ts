"use client"

import { useKeycloak } from '@react-keycloak/web'
import { useCallback } from 'react'

export function useAuth() {
  const { keycloak, initialized } = useKeycloak()

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
    
    return {
      username: keycloak.tokenParsed?.preferred_username,
      email: keycloak.tokenParsed?.email,
      name: keycloak.tokenParsed?.name,
      roles: keycloak.realmAccess?.roles || [],
    }
  }, [keycloak.authenticated, keycloak.tokenParsed, keycloak.realmAccess])

  const hasRole = useCallback((role: string) => {
    return keycloak.hasRealmRole(role)
  }, [keycloak])

  return {
    isAuthenticated: keycloak.authenticated || false,
    isLoading: !initialized,
    login,
    logout,
    register,
    getToken,
    getUserInfo,
    hasRole,
    keycloak,
  }
}
