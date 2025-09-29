'use client';

import { useQuery } from '@apollo/client';
import { BlogCard } from './blog-card';
import { GET_GAMING_NEWS } from '@/lib/graphql/queries';
import { NewsBlogOutput } from '@/types/api';
import { Skeleton } from '@/components/ui/skeleton';
import { AlertCircle, Plus } from 'lucide-react';
import { Button } from '@/components/ui/button';
import Link from 'next/link';

import Globe from "../../../public/KUTtrV3.png"; // Fallback image

interface GetGamingNewsResponse {
  getNewsBlogsByCategory: NewsBlogOutput[];
}

export function NewsFeed() {
  const { data, loading, error } = useQuery<GetGamingNewsResponse>(GET_GAMING_NEWS);

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
          Failed to load news
        </h3>
        <p className="text-gray-600 dark:text-gray-400 mb-4">
          There was an error loading the news feed. Please try again later.
        </p>
        <p className="text-sm text-gray-500 dark:text-gray-500">
          Error: {error.message}
        </p>
      </div>
    );
  }
  // No data state
  if (!data?.getNewsBlogsByCategory || data.getNewsBlogsByCategory.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center p-12 text-center">
        <h3 className="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-2">
          No news articles yet
        </h3>
        <p className="text-gray-600 dark:text-gray-400">
          Check back later for the latest gaming news and updates.
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
            Create New Article
          </Button>
        </Link>
      </div>

      {/* Articles */}
      <div className="flex flex-col gap-8">
        {data.getNewsBlogsByCategory.map((blogOutput) => {
          const { newsBlog, channel } = blogOutput;
          
          return (
            <BlogCard
              key={newsBlog.id}
              id={newsBlog.id}
              title={newsBlog.title}
              hook={newsBlog.hook || ''}
              writer={channel.name}
              createdAt={formatDate(newsBlog.createdAt || '')}
              readingTime={newsBlog.readingTime?.toString() || '5'}
              coverUrl={newsBlog.coverImg || Globe.src}
              category='news'
            />
          );
        })}
      </div>
    </div>
  );
}