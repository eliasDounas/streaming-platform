"use client"

import LiveCardItem from "./LiveCardItem";
import { useLiveStreams } from "@/hooks/useSWR";
import { Button } from "@/components/ui/button";

const LiveCardList = () => {
  const { liveStreams, isLoading, error } = useLiveStreams();

  // Debug logging for thumbnail issue
  console.log('LiveCardList - Live streams data:', liveStreams);
  console.log('LiveCardList - Number of streams:', liveStreams?.length);
  console.log('LiveCardList - Is loading:', isLoading);
  console.log('LiveCardList - Error:', error);
  
  if (liveStreams && liveStreams.length > 0) {
    console.log('LiveCardList - First stream sample:', liveStreams[0]);
    console.log('LiveCardList - First stream thumbnail:', liveStreams[0]?.thumbnailUrl);
  }

  if (isLoading) {
    return (
      <section className="px-4 py-6">
        <h2 className="text-2xl font-semibold mb-6">Live Now</h2>        <div className="grid gap-6" style={{ gridTemplateColumns: "repeat(auto-fill, minmax(320px, 1fr))" }}>
          {/* Loading skeletons */}
          {Array.from({ length: 8 }).map((_, i) => (
            <div key={i} className="w-full rounded overflow-hidden bg-muted/50 animate-pulse">
              <div className="w-full h-[200px] bg-muted"></div>
              <div className="flex items-start gap-3 px-4 py-3">
                <div className="w-10 h-10 bg-muted rounded-full"></div>
                <div className="flex flex-col gap-2 flex-1">
                  <div className="h-4 bg-muted rounded w-3/4"></div>
                  <div className="h-3 bg-muted rounded w-1/2"></div>
                  <div className="h-3 bg-muted rounded w-1/3"></div>
                </div>
              </div>
            </div>
          ))}
        </div>
      </section>
    );
  }

  if (error) {
    return (
      <section className="px-4 py-6">
        <h2 className="text-2xl font-semibold mb-6">Live Now</h2>        <div className="text-center py-8">
          <p className="text-red-500 mb-4">Failed to load live streams</p>
          <Button 
            onClick={() => window.location.reload()} 
            variant="outline"
          >
            Try Again
          </Button>
        </div>
      </section>
    );
  }

  if (liveStreams.length === 0) {
    return (
      <section className="px-4 py-6">
        <h2 className="text-2xl font-semibold mb-6">Live Now</h2>
        <div className="text-center py-8">
          <p className="text-muted-foreground">No live streams at the moment</p>
          <p className="text-sm text-muted-foreground mt-2">Check back later for live content!</p>
        </div>
      </section>
    );
  }

  return (
    <section className="px-4 py-6">
      <h2 className="text-2xl font-semibold mb-6">Live Now ({liveStreams.length})</h2>      <div className="grid gap-6" style={{ gridTemplateColumns: "repeat(auto-fill, minmax(320px, 1fr))" }}>
        {liveStreams.map((stream, index) => (
          <LiveCardItem key={`${stream.streamId}-${index}`} stream={stream} />
        ))}
      </div>
    </section>
  );
};

export default LiveCardList;
