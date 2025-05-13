import Link from "next/link";
import Image from "next/image";

interface BlogCardProps {
  id: number;
  title: string;
  hook: string;
  writer: string;
  createdAt: string;
  readingTime: string;
  coverUrl: string;
  category: string;
}

export function BlogCard({
  id,
  title,
  hook,
  writer,
  createdAt,
  readingTime,
  coverUrl,
  category
}: BlogCardProps) {
  return (
    <Link href={`/blogs/${category}/${id}`} className="flex hover:shadow-lg dark:hover:bg-popover overflow-hidden transition w-full min-h-52">
      {/* Left side: Text */}
      <div className="flex flex-col justify-between lg:w-3/4 p-4">
        <div>
          <h2 className="text-2xl font-bold mb-2">{title}</h2>
          <p className="text-gray-600 dark:text-gray-400 mb-4">{hook}</p>
        </div>
        <div>
          <p className="text-gray-500 mb-2">By {writer}</p>
          <div className="flex items-center text-sm text-gray-500 gap-2">
            <span>{new Date(createdAt).toLocaleDateString()}</span>
            <span className="font-semibold">-</span>
            <span>{readingTime} min read</span>
          </div>
        </div>
      </div>

      {/* Right side: Image */}
      <div className="hidden md:block w-1/4 min-h-48 relative">
        <Image
          src={coverUrl}
          alt="Blog cover"
          fill
          className="object-cover"
        />
      </div>
    </Link>
  );
}
