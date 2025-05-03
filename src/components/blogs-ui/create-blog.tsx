'use client';

import React, { useState } from "react";
import dynamic from "next/dynamic";
import rehypeSanitize from "rehype-sanitize";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";

// Markdown editor
const MDEditor = dynamic(() => import('@uiw/react-md-editor'), { ssr: false });

export default function CreateBlog() {
  const [title, setTitle] = useState("");
  const [hook, setHook] = useState("");
  const [category, setCategory] = useState("Gaming News");
  const [tags, setTags] = useState("");
  const [coverImage, setCoverImage] = useState<File | null>(null);
  const [readingTime, setReadingTime] = useState("");
  const [content, setContent] = useState<string>("");

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) setCoverImage(file);
  };

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const formData = new FormData();
    formData.append("title", title);
    formData.append("hook", hook);
    formData.append("tags", tags);
    formData.append("category", category);
    formData.append("readingTime", readingTime);
    formData.append("content", content);
    if (coverImage) {
      formData.append("coverImage", coverImage);
    }

    // Example: send formData to an API
    // fetch('/api/blogs', {
    //   method: 'POST',
    //   body: formData,
    // });

    console.log("FormData submitted:");
    console.log(formData)
  };

  return (
    <form onSubmit={handleSubmit} className="flex-grow bg-neutral-50 dark:bg-neutral-900/70 shadow-md rounded lg:p-8">
      <div className="container p-4 space-y-6">
        <div>
          <Label htmlFor="title" className="mb-2">Title<span className="text-red-500 -ml-1">*</span></Label>
          <Input id="title" value={title} onChange={(e) => setTitle(e.target.value)} required />
        </div>

        <div>
          <Label htmlFor="hook" className="mb-2">Hook<span className="text-red-500 -ml-1">*</span></Label>
          <Textarea id="hook" value={hook} onChange={(e) => setHook(e.target.value)} required />
        </div>

        <div>
          <Label htmlFor="tags" className="mb-2">Tags (comma-separated)<span className="text-red-500 -ml-1">*</span></Label>
          <Input id="tags" value={tags} onChange={(e) => setTags(e.target.value)} required />
        </div>

        <div>
          <Label htmlFor="coverImage" className="mb-2">Cover Image</Label>
          <Input id="coverImage" type="file" accept="image/jpeg,image/png,image/webp" onChange={handleImageChange} className="cursor-pointer max-w-xs"/>
        </div>

        <div>
          <Label htmlFor="readingTime" className="mb-2">Reading Time Estimate (in minutes)<span className="text-red-500 -ml-1">*</span></Label>
          <Input id="readingTime" type="number" value={readingTime} onChange={(e) => setReadingTime(e.target.value)} className="max-w-xs" required />
        </div>

        <div> 
          <Label className="mb-4">Category<span className="text-red-500 -ml-1">*</span></Label>
          <RadioGroup defaultValue={category} onValueChange={setCategory} className="flex flex-col gap-4 mt-2">
            <div className="flex items-center space-x-2">
              <RadioGroupItem value="Gaming News" id="gaming-news" />
              <Label htmlFor="gaming-news" className="font-normal">Gaming News</Label>
            </div>
            <div className="flex items-center space-x-2">
              <RadioGroupItem value="Game Review" id="game-review" />
              <Label htmlFor="game-review" className="font-normal">Game Review</Label>
            </div>
          </RadioGroup>
        </div>

        <div>
          <Label className="mb-2 block">Content<span className="text-red-500 ml-1">*</span></Label>
          <div data-color-mode="dark">
          <MDEditor
            height={500}
            value={content}
            onChange={(val) => setContent(val || "")}
            previewOptions={{
              rehypePlugins: [[rehypeSanitize]],
            }}
          />
          </div>
        </div>

        <div className="mt-6 text-right">
          <Button type="submit" className="w-full sm:w-auto">
            Publish
          </Button>
        </div>
      </div>
    </form>
  );
}
