"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";
import Chat from "@/components/chat-ui/Chat";
import { PanelRightClose, PanelLeftOpen } from "lucide-react";

export default function LiveLayout() {
  const [chatOpen, setChatOpen] = useState(true);

  return (
    <div
      className={cn("flex flex-col lg:flex-row")}
      style={{
        height: "calc(100vh - 56px)", // 56px = h-14 navbar
      }}
    >
      {/* Video Player Section */}
      <div className="bg-black flex-1 w-full">
        <div className="w-full h-full flex items-center justify-center text-white text-xl">
          Video Player
        </div>
      </div>

      {/* Chat Section */}
      {chatOpen && (
        <div className="border-t lg:border-t-0 lg:border-l flex flex-col lg:max-w-[400px] 2xl:min-w-[450px] xl:max-w-[500px] xl:min-w-[350px] h-[55vh] lg:h-full">
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
