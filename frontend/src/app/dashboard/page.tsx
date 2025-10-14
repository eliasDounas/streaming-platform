import Dashboard from '@/components/dashboard-ui/Dashboard';
import ProtectedRoute from '@/components/auth/ProtectedRoute';

// should be only accessible if user has a channel
export default function DashboardPage() {
  return (
    // <ProtectedRoute>
      <div className="min-h-screen bg-background">
        <div className="max-w-7xl mx-auto px-4 py-8">
          <Dashboard />
        </div>
      </div>
    // </ProtectedRoute> //
  );
}