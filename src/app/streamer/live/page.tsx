"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";
import Chat from "@/components/chat-ui/Chat";
import { PanelRightClose, PanelLeftOpen } from "lucide-react";
import IvsPlayer from "@/components/IvsPlayer";
import LiveStreamWithChannelInfo from "@/components/LiveStreamWithChannelInfo";

export default function LiveLayout() {
  const [chatOpen, setChatOpen] = useState(true);

  return (
    <div
      className={cn("flex flex-col xl:flex-row")}
      style={{
        height: "calc(100vh - 56px)", // 56px = h-14 navbar
      }}
    >
      {/* Video Player Section */}
        <div className="w-full h-full flex items-center justify-center text-white text-xl">
          <LiveStreamWithChannelInfo playbackUrl="https://ivs-streams-archives.s3.eu-west-1.amazonaws.com/ivs/v1/971528320784/5OIWeVEPKd7j/2025/6/4/22/12/FXoRUQs7YtAE/media/hls/master.m3u8" />
      </div>

      {/* Chat Section */}
      {chatOpen && (
        <div className="border-t xl:border-t-0 xl:border-l flex flex-col 2xl:min-w-[450px] xl:max-w-[500px] xl:min-w-[350px] h-[55vh] lg:h-full">
          <div className="flex items-center justify-between p-2 border-b">
            <Button
              variant="ghost"
              size="sm"
              onClick={() => setChatOpen(false)}
              className="flex items-center gap-2"
            >
              <PanelRightClose className="w-4 h-4" />
            </Button>
            
              <span className="text-sm mx-auto pr-4 font-semibold font-stretch-ultra-condensed">Live Chat</span>
          </div>
          <div className="flex-1 overflow-y-auto">
            <Chat />
          </div>
        </div>
      )}

      {/* Toggle button when chat is hidden */}
      {!chatOpen && (
        <div className="absolute top-20 right-4 z-10">
          <Button
            variant="default"
            size="sm"
            onClick={() => setChatOpen(true)}
            className="flex items-center gap-2"
          >
            <PanelLeftOpen className="w-4 h-4 rotate-180" />
            <span className="text-sm">Show Chat</span>
          </Button>
        </div>
      )}
    </div>
  );
}
