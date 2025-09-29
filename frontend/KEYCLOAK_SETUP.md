# Keycloak Authentication Setup

This application uses Keycloak for authentication with the `@react-keycloak/web` library.

## Configuration

1. **Environment Variables**
   Copy `.env.local.example` to `.env.local` and update the values:
   ```bash
   NEXT_PUBLIC_KEYCLOAK_URL=http://localhost:8080
   NEXT_PUBLIC_KEYCLOAK_REALM=your-realm-name
   NEXT_PUBLIC_KEYCLOAK_CLIENT_ID=your-client-id
   ```

2. **Keycloak Server Setup**
   - Install and run Keycloak server
   - Create a realm (e.g., "streaming-platform")
   - Create a client with:
     - Client ID: "streaming-frontend"
     - Client Protocol: "openid-connect"
     - Access Type: "public"
     - Valid Redirect URIs: "http://localhost:3000/*"
     - Web Origins: "http://localhost:3000"

3. **Protected Routes**
   - All pages are publicly accessible except `/dashboard`
   - Dashboard requires authentication and will redirect to Keycloak login

## Authentication Flow

- **Login/Signup**: Buttons in header redirect to Keycloak
- **Logout**: Available in user dropdown menu
- **Auto-login**: Silent SSO check on page load
- **Token Management**: Automatic token refresh

## Components

- `KeycloakProvider`: Wraps the app with Keycloak context
- `ProtectedRoute`: Protects dashboard route
- `useAuth`: Custom hook for authentication state
- Updated login/signup dialogs to use Keycloak

## Usage

```tsx
import { useAuth } from '@/hooks/useAuth'

function MyComponent() {
  const { isAuthenticated, login, logout, getUserInfo } = useAuth()
  
  if (isAuthenticated) {
    const user = getUserInfo()
    return <div>Welcome {user?.username}</div>
  }
  
  return <button onClick={login}>Login</button>
}
```
