import Dashboard from '@/components/dashboard-ui/Dashboard';

// should be only accessible if user has a channel
export default function DashboardPage() {
  return (
    <div className="min-h-screen bg-background">
      <div className="max-w-7xl mx-auto px-4 py-8">
        <Dashboard />
      </div>
    </div>
  );
}