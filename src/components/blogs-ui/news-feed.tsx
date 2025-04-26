'use client';

import { BlogCard } from './blog-card';

import Globe from "../../../public/KUTtrV3.png"; // Replace with your own thumbnail
interface Blog {
  id: number;
  title: string;
  hook: string;
  writer: string;
  createdAt: string;
  readingTime: string;
  coverUrl: string;
}

// Fake static blogs
const blogs: Blog[] = [
  {
    id: 1,
    title: "Exploring the Future of AI",
    hook: "How AI is changing the world of technology and what lies ahead.",
    writer: "Alice Johnson",
    createdAt: "2024-07-01T10:00:00Z",
    readingTime: "6",
    coverUrl: Globe.src,
  },
  {
    id: 2,
    title: "Mastering React Server Components",
    hook: "Learn how Server Components can make your apps faster and cleaner.",
    writer: "Bob Smith",
    createdAt: "2024-06-20T14:30:00Z",
    readingTime: "8",
    coverUrl: "/covers/react-server.jpg",
  },
  {
    id: 3,
    title: "A Journey into Web3 Development",
    hook: "Building decentralized apps with Ethereum, Solidity, and more.",
    writer: "Charlie Adams",
    createdAt: "2024-06-10T08:15:00Z",
    readingTime: "5",
    coverUrl: "/covers/web3-journey.jpg",
  },
];

export function NewsFeed() {
  return (
    <div className="flex flex-col gap-8 p-6">
      {blogs.map((blog) => (
        <BlogCard
          key={blog.id}
          id={blog.id}
          title={blog.title}
          hook={blog.hook}
          writer={blog.writer}
          createdAt={blog.createdAt}
          readingTime={blog.readingTime}
          coverUrl={blog.coverUrl}
        />
      ))}
    </div>
  );
}
