"use client"

import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Clapperboard, Plus, Loader2 } from "lucide-react";
import { useUserChannel } from "@/hooks/useSWR";
import { CreateChannelDialog } from "@/components/CreateChannelDialog";
import Link from "next/link";

interface ChannelStatusButtonProps {
  userId: string;
}

export function ChannelStatusButton({ userId }: ChannelStatusButtonProps) {  const { userChannel, isLoading, refresh } = useUserChannel(userId);
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);

  const handleChannelCreated = () => {
    // Refresh the user channel data to pick up the newly created channel
    refresh();
  };
  
  if (isLoading) {
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
