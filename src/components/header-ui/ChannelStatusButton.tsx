"use client"

import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Clapperboard, Plus, Loader2 } from "lucide-react";
import { useUserChannel } from "@/hooks/useSWR";
import { CreateChannelDialog } from "@/components/channel-ui/CreateChannelDialog";
import { useAuth } from "@/hooks/useAuth";
import Link from "next/link";

export function ChannelStatusButton(){
  const { isAuthenticated, isLoading: authLoading } = useAuth();
  const { userChannel, isLoading: channelLoading, refresh } = useUserChannel();
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);

  const handleChannelCreated = () => {
    // Refresh the user channel data to pick up the newly created channel
    refresh();
  };

  // Don't render anything if user is not authenticated
  if (!isAuthenticated) {
    return null;
  }

  // Show loading state while checking authentication or channel status
  if (authLoading || channelLoading) {
    return (
      <Button disabled variant="outline" className="gap-2">
        <Loader2 className="h-4 w-4 animate-spin" />
        Loading...
      </Button>
    );
  }

  // User has a channel - show Dashboard button
  if (userChannel) {
    return (
      <Button asChild variant="outline" className="gap-2">
        <Link href="/dashboard">
          <Clapperboard className="h-4 w-4" />
          Dashboard
        </Link>
      </Button>
    );
  }

  // User doesn't have a channel (error 404 or no data) - show Create Channel button
  return (
    <>
      <Button 
        onClick={() => setIsCreateDialogOpen(true)} 
        variant="default" 
        className="gap-2"
      >
        <Plus className="h-4 w-4" />
        Create Channel
      </Button>
      
      <CreateChannelDialog
        open={isCreateDialogOpen}
        onOpenChange={setIsCreateDialogOpen}
        onChannelCreated={handleChannelCreated}
      />
    </>
  );
}
