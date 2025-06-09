'use client';

import { useQuery } from '@apollo/client';
import { BlogCard } from './blog-card';
import { GET_REVIEWS } from '@/lib/graphql/queries';
import { GamingBlogOutput } from '@/types/api';
import { Skeleton } from '@/components/ui/skeleton';
import { AlertCircle, Plus } from 'lucide-react';
import { Button } from '@/components/ui/button';
import Link from 'next/link';

import Globe from "../../../public/KUTtrV3.png"; // Fallback image

interface GetReviewsResponse {
  getGamingBlogsByCategory: GamingBlogOutput[];
}

export function ReviewsFeed() {
  const { data, loading, error } = useQuery<GetReviewsResponse>(GET_REVIEWS);

  // Helper function to format date
  const formatDate = (dateString: string) => {
    try {
      return new Date(dateString).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      });
    } catch {
      return 'Unknown date';
    }
  };

  // Loading state
  if (loading) {
    return (
      <div className="flex flex-col gap-8 p-6">
        {[...Array(3)].map((_, i) => (
          <div key={i} className="space-y-4">
            <Skeleton className="h-48 w-full rounded-lg" />
            <Skeleton className="h-6 w-3/4" />
            <Skeleton className="h-4 w-1/2" />
          </div>
        ))}
      </div>
    );
  }

  // Error state
  if (error) {
    return (
      <div className="flex flex-col items-center justify-center p-12 text-center">
        <AlertCircle className="w-12 h-12 text-red-500 mb-4" />
        <h3 className="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-2">
          Failed to load reviews
        </h3>
        <p className="text-gray-600 dark:text-gray-400 mb-4">
          There was an error loading the game reviews. Please try again later.
        </p>
        <p className="text-sm text-gray-500 dark:text-gray-500">
          Error: {error.message}
        </p>
      </div>
    );
  }
  // No data state
  if (!data?.getGamingBlogsByCategory || data.getGamingBlogsByCategory.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center p-12 text-center">
        <h3 className="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-2">
          No game reviews yet
        </h3>
        <p className="text-gray-600 dark:text-gray-400">
          Check back later for the latest game reviews and recommendations.
        </p>
      </div>
    );
  }
  return (
    <div className="flex flex-col gap-6 p-6">
      {/* Create New Button */}
      <div className="flex justify-end">
        <Link href="/blogs/create">
          <Button className="flex items-center gap-2">
            <Plus className="w-4 h-4" />
            Create New Review
          </Button>
        </Link>
      </div>

      {/* Reviews */}
      <div className="flex flex-col gap-8">
        {data.getGamingBlogsByCategory.map((blogOutput) => {
          const { gamingBlog, channel } = blogOutput;
          
          return (
            <BlogCard
              key={gamingBlog.id}
              id={gamingBlog.id}
              title={gamingBlog.title}
              hook={gamingBlog.hook || ''}
              writer={channel.name}
              createdAt={formatDate(gamingBlog.createdAt || '')}
              readingTime={gamingBlog.readingTime?.toString() || '5'}
              coverUrl={gamingBlog.coverImg || Globe.src}
              category='review'
            />
          );
        })}
      </div>
    </div>
  );
}