"use client";

import { useState, useEffect } from "react";
import { use } from "react";
import { useRouter } from "next/navigation";
import VodWithChannelInfo from "@/components/VodWithChannelInfo";
import { usePastStreams, useStream } from "@/hooks/useSWR";

interface VodPageProps {
  params: Promise<{
    id: string;
  }>;
}

export default function VodPage({ params }: VodPageProps) {
  const { id } = use(params);
  const router = useRouter();
  
  // Try to get from cached past streams data first (check all pages)
  const { pastStreams, isLoading: allLoading, error: allError } = usePastStreams(0, 50); // Get more items to increase cache hit chance
  const cachedVod = pastStreams?.find(vod => vod.streamId === id);
    // Only fetch individual VOD if not found in cache and not currently loading all VODs
  const shouldFetchIndividual = !cachedVod && !allLoading;
  const { stream: individualVod, isLoading: individualLoading, error: individualError } = useStream(
    shouldFetchIndividual ? id : null
  );
    // Use cached VOD if available, otherwise use individually fetched VOD
  const currentVod = cachedVod || individualVod;
  const isLoading = allLoading || (shouldFetchIndividual && individualLoading);  const error = allError || individualError;

  // Redirect to stream page if VOD exists but is actually live
  useEffect(() => {
    if (!isLoading && currentVod && currentVod.isLive) {
      router.push(`/stream/${id}`);
    }
  }, [isLoading, currentVod, id, router]);

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="text-center">
          <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-primary"></div>
          <p className="mt-4 text-lg">Loading VOD...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="text-center">
          <h2 className="text-2xl font-bold text-red-600 mb-4">Error Loading VOD</h2>
          <p className="text-gray-600">Failed to load VOD data. Please try again later.</p>
          <p className="text-sm text-gray-500 mt-2">VOD ID: {id}</p>
        </div>
      </div>
    );
  }
  if (!isLoading && !currentVod) {
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="text-center">
          <h2 className="text-2xl font-bold mb-4">VOD Not Found</h2>
          <p className="text-gray-600">The VOD with ID "{id}" could not be found.</p>
          <p className="text-sm text-gray-500 mt-2">
            {cachedVod === undefined && !shouldFetchIndividual 
              ? "Checked in cached VODs" 
              : "Checked individual VOD endpoint"}
          </p>
        </div>
      </div>
    );  }

  // Show loading while redirecting if stream is actually live
  if (!isLoading && currentVod && currentVod.isLive) {
    return (
      <div className="flex items-center justify-center h-screen">
        <div className="text-center">
          <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-primary"></div>
          <p className="mt-4 text-lg">Stream is currently live. Redirecting to live stream...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background">
      <div className="max-w-7xl mx-auto px-4 py-6">
        {currentVod && <VodWithChannelInfo vod={currentVod} />}
      </div>
    </div>
  );
}
