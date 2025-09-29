"use client";

import { useState, useEffect } from "react";
import { useParams, useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";
import Chat from "@/components/chat-ui/Chat";
import { PanelRightClose, PanelLeftOpen } from "lucide-react";
import LiveStreamWithChannelInfo from "@/components/LiveStreamWithChannelInfo";
import { useLiveStreams, useStream } from "@/hooks/useSWR";

export default function StreamPage() {
  const [chatOpen, setChatOpen] = useState(true);
  const params = useParams();
  const router = useRouter();
  const streamId = params.id as string;
  
  // Try to get from cached all-streams data first
  const { liveStreams, isLoading: allLoading, error: allError } = useLiveStreams();
  const cachedStream = liveStreams?.find(stream => stream.streamId === streamId);
  
  // Only fetch individual stream if not found in cache and not currently loading all streams
  const shouldFetchIndividual = !cachedStream && !allLoading;
  const { stream: individualStream, isLoading: individualLoading, error: individualError } = useStream(
    shouldFetchIndividual ? streamId : null
  );
    // Use cached stream if available, otherwise use individually fetched stream
  const currentStream = cachedStream || individualStream;
  const isLoading = allLoading || (shouldFetchIndividual && individualLoading);  const error = allError || individualError;

  // Redirect to VOD page if stream exists but is not live
  useEffect(() => {
    if (!isLoading && currentStream && !currentStream.isLive) {
      router.push(`/vods/${streamId}`);
    }
  }, [isLoading, currentStream, streamId, router]);

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="text-center">
          <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-primary"></div>
          <p className="mt-4 text-lg">Loading stream...</p>
        </div>
      </div>
    );
  }
  if (error) {
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="text-center">
          <h2 className="text-2xl font-bold text-red-600 mb-4">Error Loading Stream</h2>
          <p className="text-gray-600">Failed to load stream data. Please try again later.</p>
        </div>
      </div>
    );
  }
  if (!isLoading && !currentStream) {
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="text-center">
          <h2 className="text-2xl font-bold mb-4">Stream Not Found</h2>
          <p className="text-gray-600">The stream with ID "{streamId}" could not be found or is no longer live.</p>
          <p className="text-sm text-gray-500 mt-2">
            {cachedStream === undefined && !shouldFetchIndividual 
              ? "Checked in cached streams" 
              : "Checked individual stream endpoint"}
          </p>
        </div>
      </div>
    );  }

  // Show loading while redirecting if stream is not live
  if (!isLoading && currentStream && !currentStream.isLive) {
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="text-center">
          <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-primary"></div>
          <p className="mt-4 text-lg">Stream is not live. Redirecting to VOD...</p>
        </div>
      </div>
    );
  }

  return (
    <div
      className={cn("flex flex-col xl:flex-row")}
      style={{
        height: "calc(100vh - 56px)", // 56px = h-14 navbar
      }}
    >      {/* Video Player Section */}
        <div className="w-full h-full flex items-center justify-center text-white text-xl">
          {currentStream && <LiveStreamWithChannelInfo stream={currentStream} />}
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
          </div>          <div className="flex-1 overflow-y-auto">
            <Chat channelId={currentStream?.channelId || ''} />
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
