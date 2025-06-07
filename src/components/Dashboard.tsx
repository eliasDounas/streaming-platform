'use client'
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { DefaultStreamInfoForm } from './DefaultStreamInfoForm';
import { StreamConnectionInfo } from './StreamConnectionInfo';
import Channel from './channel-ui/Channel';
import { Settings, Key, User, Radio } from 'lucide-react';

const Dashboard = () => { 
  return (
    <div className="space-y-8">
      {/* Welcome Header */}
      <div className="space-y-2">
        <h1 className="text-3xl font-bold tracking-tight bg-gradient-to-r from-primary via-primary/80 to-primary/60 bg-clip-text text-transparent">
          Creator Dashboard
        </h1>
        <p className="text-muted-foreground text-lg">
          Manage your channel, stream settings, and connection details
        </p>
      </div>

      {/* Dashboard Tabs */}
      <Tabs defaultValue="account" className="w-full">
        <TabsList className="grid grid-cols-3 w-full max-w-2xl mx-auto h-14 bg-card/50 backdrop-blur-sm border border-border/40 shadow-lg">
          <TabsTrigger 
            value="channel" 
            className="flex items-center gap-3 h-10 data-[state=active]:bg-primary data-[state=active]:text-primary-foreground data-[state=active]:shadow-md transition-all duration-300"
          >
            <User className="w-4 h-4" />
            <span className="hidden sm:inline">My Channel</span>
          </TabsTrigger>
          <TabsTrigger 
            value="account" 
            className="flex items-center gap-3 h-10 data-[state=active]:bg-primary data-[state=active]:text-primary-foreground data-[state=active]:shadow-md transition-all duration-300"
          >
            <Settings className="w-4 h-4" />
            <span className="hidden sm:inline">Stream Settings</span>
          </TabsTrigger>
          <TabsTrigger 
            value="password" 
            className="flex items-center gap-3 h-10 data-[state=active]:bg-primary data-[state=active]:text-primary-foreground data-[state=active]:shadow-md transition-all duration-300"
          >
            <Key className="w-4 h-4" />
            <span className="hidden sm:inline">Connection Keys</span>
          </TabsTrigger>
        </TabsList>
        
        <div className="mt-8">
          <TabsContent value="account" className="space-y-0">
            <Card className="bg-card/50 backdrop-blur-sm border-border/40 shadow-xl">
              <CardHeader className="border-b border-border/20 bg-gradient-to-r from-primary/5 to-transparent">
                <div className="flex items-center gap-3">
                  <div className="p-2 bg-primary/10 rounded-lg">
                    <Settings className="w-5 h-5 text-primary" />
                  </div>
                  <div>
                    <CardTitle className="text-xl">Stream Configuration</CardTitle>
                    <CardDescription className="text-base">
                      Configure your default stream title, description, and category
                    </CardDescription>
                  </div>
                </div>
              </CardHeader>
              <CardContent className="p-6">
                <DefaultStreamInfoForm 
                  userId="user123" 
                  onSuccess={() => console.log('Stream info saved successfully!')}
                  onError={(error) => console.error('Error saving stream info:', error)}
                />
              </CardContent>
            </Card>
          </TabsContent>
          
          <TabsContent value="password" className="space-y-0">
            <Card className="bg-card/50 backdrop-blur-sm border-border/40 shadow-xl">
              <CardHeader className="border-b border-border/20 bg-gradient-to-r from-primary/5 to-transparent">
                <div className="flex items-center gap-3">
                  <div className="p-2 bg-primary/10 rounded-lg">
                    <Key className="w-5 h-5 text-primary" />
                  </div>
                  <div>
                    <CardTitle className="text-xl">Streaming Connection</CardTitle>
                    <CardDescription className="text-base">
                      Get your stream keys and RTMP settings for broadcasting
                    </CardDescription>
                  </div>
                </div>
              </CardHeader>
              <CardContent className="p-6">
                <StreamConnectionInfo userId="154566523" />
              </CardContent>
            </Card>
          </TabsContent>
          
          <TabsContent value="channel" className="space-y-0">
            <Card className="bg-card/50 backdrop-blur-sm border-border/40 shadow-xl">
              <CardHeader className="border-b border-border/20 bg-gradient-to-r from-primary/5 to-transparent">
                <div className="flex items-center gap-3">
                  <div className="p-2 bg-primary/10 rounded-lg">
                    <User className="w-5 h-5 text-primary" />
                  </div>
                  <div>
                    <CardTitle className="text-xl">Channel Management</CardTitle>
                    <CardDescription className="text-base">
                      View and manage your channel profile and settings
                    </CardDescription>
                  </div>
                </div>
              </CardHeader>
              <CardContent className="p-6">
                <Channel />
              </CardContent>
            </Card>
          </TabsContent>
        </div>
      </Tabs>
    </div>
  );
};

export default Dashboard;
