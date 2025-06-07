"use client"

import { Button } from "@/components/ui/button";
import { Clapperboard, Plus, Loader2 } from "lucide-react";
import { useUserChannel } from "@/hooks/useSWR";
import Link from "next/link";

interface ChannelStatusButtonProps {
  userId: string;
}

export function ChannelStatusButton({ userId }: ChannelStatusButtonProps) {
  const { userChannel, isLoading } = useUserChannel(userId);
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
      <Button asChild variant="default" className="gap-2">
        <Link href="/dashboard">
          <Clapperboard className="h-4 w-4" />
          Dashboard
        </Link>
      </Button>
    );
  }

  // User doesn't have a channel (error 404 or no data) - show Create Channel button
  return (
    <Button asChild variant="outline" className="gap-2">
      <Link href="/channel/create">
        <Plus className="h-4 w-4" />
        Create Channel
      </Link>
    </Button>
  );
}
