// Example component showing how to use the stored user ID
"use client"

import { useAuth } from '@/hooks/useAuth'
import { useUserStore } from '@/store/userStore'

export function UserProfile() {
  const { isAuthenticated, getUserId, getUserInfo } = useAuth()
  const { userInfo } = useUserStore()

  if (!isAuthenticated) {
    return <div>Please log in to view your profile</div>
  }

  const userId = getUserId()
  const fullUserInfo = getUserInfo()

  return (
    <div className="p-4 border rounded-lg">
      <h2 className="text-xl font-bold mb-4">User Profile</h2>
      
      {/* Access user ID directly */}
      <p><strong>User ID:</strong> {userId}</p>
      
      {/* Access user info from store */}
      {userInfo && (
        <div className="mt-4">
          <p><strong>Username:</strong> {userInfo.username}</p>
          <p><strong>Email:</strong> {userInfo.email}</p>
          <p><strong>Name:</strong> {userInfo.name}</p>
          <p><strong>Roles:</strong> {userInfo.roles.join(', ')}</p>
        </div>
      )}
      
      {/* Access user info from function */}
      {fullUserInfo && (
        <div className="mt-4">
          <h3 className="font-semibold">Full User Info:</h3>
          <pre className="bg-gray-100 p-2 rounded mt-2">
            {JSON.stringify(fullUserInfo, null, 2)}
          </pre>
        </div>
      )}
    </div>
  )
}
