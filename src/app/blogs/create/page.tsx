
import CreateBlog from "@/components/blogs-ui/create-blog";
import { Button } from "@/components/ui/button";
import Link from "next/link";

export default function Page() {
  return (
    <div className="min-h-screen flex flex-col p-6">
      {/* Header and Guide Link */}
      <div className="mb-6">
        <h1 className="text-4xl font-bold mb-2 -ml-2">📝 Create Your Blog</h1>
        <p className="text-gray-700 dark:text-gray-300">
          Need help with Markdown?{" "}
          <Link
            href="https://www.markdownguide.org/basic-syntax/"
            target="_blank"
            rel="noopener noreferrer"
            className="text-pink-600 underline hover:text-blue-800 dark:text-pink-400 dark:hover:text-pink-600"
          >
            Read the Markdown Guide
          </Link>
        </p>
      </div>

      {/* Create Blog Area */}
        <CreateBlog />
    
    </div>
  );
}