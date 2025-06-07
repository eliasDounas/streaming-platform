"use client";

import { use } from "react";
import Channel from "@/components/channel-ui/Channel";

interface ChannelPageProps {
  params: Promise<{
    id: string;
  }>;
}

export default function ChannelPage({ params }: ChannelPageProps) {
  const { id } = use(params);
  
  return (
    <Channel channelId={id} />
  );
}