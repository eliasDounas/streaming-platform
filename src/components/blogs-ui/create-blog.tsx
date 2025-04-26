'use client'; // Only needed if you're in Next.js 13/14 app/ directory

import React, { useState } from "react";
import dynamic from "next/dynamic";
import rehypeSanitize from "rehype-sanitize";

// Dynamically import because @uiw/react-md-editor uses `window`
const MDEditor = dynamic(() => import('@uiw/react-md-editor'), { ssr: false });

export default function CreateBlog() {
  const [value, setValue] = useState<string>(
    `**Hello world!!!** <IFRAME SRC="javascript:javascript:alert(window.origin);"></IFRAME>`
  );

  return (
    <div className="container p-4">
      <MDEditor
        height={500}
        value={value}
        onChange={(val) => setValue(val || "")}
        previewOptions={{
          rehypePlugins: [[rehypeSanitize]],
        }}
      />
    </div>
  );
}
