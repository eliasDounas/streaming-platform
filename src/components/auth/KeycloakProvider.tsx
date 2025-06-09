"use client"

import { ReactKeycloakProvider } from '@react-keycloak/web'
import Keycloak from 'keycloak-js'

// Initialize Keycloak instance
const keycloak = new Keycloak({
  url: process.env.NEXT_PUBLIC_KEYCLOAK_URL || 'http://localhost:8080',
  realm: process.env.NEXT_PUBLIC_KEYCLOAK_REALM || 'upstream',
  clientId: process.env.NEXT_PUBLIC_KEYCLOAK_CLIENT_ID || 'streaming-platform-frontend',
})

// Keycloak init options
const initOptions = {
  onLoad: 'check-sso' as const,
  silentCheckSsoRedirectUri: typeof window !== 'undefined' ? window.location.origin + '/silent-check-sso.html' : undefined,
}

interface KeycloakProviderProps {
  children: React.ReactNode
}

export default function KeycloakProvider({ children }: KeycloakProviderProps) {
  return (
    <ReactKeycloakProvider authClient={keycloak} initOptions={initOptions}>
      {children}
    </ReactKeycloakProvider>
  )
}
