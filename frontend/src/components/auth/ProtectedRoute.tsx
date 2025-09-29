"use client"

import { useKeycloak } from '@react-keycloak/web'
import { useEffect } from 'react'
import { useRouter } from 'next/navigation'

interface ProtectedRouteProps {
  children: React.ReactNode
}

export default function ProtectedRoute({ children }: ProtectedRouteProps) {
  const { keycloak, initialized } = useKeycloak()
  const router = useRouter()

  useEffect(() => {
    if (initialized && !keycloak.authenticated) {
      keycloak.login()
    }
  }, [initialized, keycloak])

  if (!initialized) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-lg">Loading...</div>
      </div>
    )
  }

  if (!keycloak.authenticated) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-lg">Redirecting to login...</div>
      </div>
    )
  }

  return <>{children}</>
}
