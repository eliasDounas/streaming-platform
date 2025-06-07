'use client'
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { DefaultStreamInfoForm } from './dashboard-ui/DefaultStreamInfoForm';
import { StreamConnectionInfo } from './dashboard-ui/StreamConnectionInfo';

const Dashboard = () => {
  return (
    // </ul>    <Tabs defaultValue="account" className="w-[400px]">
    <Tabs defaultValue="account" className="w-full">
  <TabsList>
    <TabsTrigger value="account">Stream Settings</TabsTrigger>
    <TabsTrigger value="password">Connection Info</TabsTrigger>
  </TabsList>
  <TabsContent value="account">
    <DefaultStreamInfoForm 
      userId="user123" 
      onSuccess={() => console.log('Stream info saved successfully!')}
      onError={(error) => console.error('Error saving stream info:', error)}
    />
  </TabsContent>
  <TabsContent value="password">
    <StreamConnectionInfo userId="user123" />
  </TabsContent>
  </Tabs>);
};

export default Dashboard;
