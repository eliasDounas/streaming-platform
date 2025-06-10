"use client"

import { ReactKeycloakProvider } from '@react-keycloak/web'
import Keycloak from 'keycloak-js'
import { useEffect } from 'react'
import { useUserStore } from '@/store/userStore'
import { setKeycloakInstance } from '@/lib/axios'

// Initialize Keycloak instance
const keycloak = new Keycloak({
  url: process.env.NEXT_PUBLIC_KEYCLOAK_URL || 'http://localhost:8086',
  realm: process.env.NEXT_PUBLIC_KEYCLOAK_REALM || 'upstream',
  clientId: process.env.NEXT_PUBLIC_KEYCLOAK_CLIENT_ID || 'streaming-keycloak',
})

// Keycloak init options
const initOptions = {
  onLoad: 'check-sso' as const,
  silentCheckSsoRedirectUri: typeof window !== 'undefined' ? window.location.origin + '/silent-check-sso.html' : undefined,
}

interface KeycloakProviderProps {
  children: React.ReactNode
}

function KeycloakEventHandler() {
  const { setUserInfo, clearUserInfo } = useUserStore()

  useEffect(() => {
    const handleTokenReady = () => {
      // Set the keycloak instance for axios interceptors
      setKeycloakInstance(keycloak)
      
      if (keycloak.authenticated && keycloak.tokenParsed) {
        const userInfo = {
          id: keycloak.tokenParsed.sub || '',
          username: keycloak.tokenParsed.preferred_username,
          email: keycloak.tokenParsed.email,
          name: keycloak.tokenParsed.name,
          roles: keycloak.realmAccess?.roles || [],
        }
        setUserInfo(userInfo)
        console.log('👤 User authenticated and stored:', userInfo)
      } else {
        clearUserInfo()
        console.log('🚪 User logged out, clearing user info')
      }
    }

    const handleAuthLogout = () => {
      clearUserInfo()
      console.log('🚪 User logged out, clearing user info')
    }

    // Handle initial authentication state
    if (keycloak.authenticated) {
      handleTokenReady()
    }

    // Listen to keycloak events
    keycloak.onTokenExpired = () => {
      console.log('🔄 Token expired, refreshing...')
      keycloak.updateToken(30)
    }

    keycloak.onAuthSuccess = handleTokenReady
    keycloak.onAuthRefreshSuccess = handleTokenReady
    keycloak.onAuthLogout = handleAuthLogout

    // Cleanup function
    return () => {
      keycloak.onTokenExpired = undefined
      keycloak.onAuthSuccess = undefined
      keycloak.onAuthRefreshSuccess = undefined
      keycloak.onAuthLogout = undefined
    }
  }, [setUserInfo, clearUserInfo])

  return null
}

export default function KeycloakProvider({ children }: KeycloakProviderProps) {
  return (
    <ReactKeycloakProvider authClient={keycloak} initOptions={initOptions}>
      <KeycloakEventHandler />
      {children}
    </ReactKeycloakProvider>
  )
}
