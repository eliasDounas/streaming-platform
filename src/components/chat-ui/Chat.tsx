"use client";

import { useEffect, useRef, useState } from "react";
import { Button } from "../ui/button";
import { Input } from "../ui/input";

const Chat = () => {
  const [messages, setMessages] = useState([
    { user: "mod_jane", text: "Welcome to the stream!", role: "mod" },
    { user: "viewer123", text: "Let's gooo 🚀", role: "viewer" },
  ]);
  const [input, setInput] = useState("");

  const containerRef = useRef<HTMLDivElement | null>(null);
  const bottomRef = useRef<HTMLDivElement | null>(null);

  // Checks if user is near the bottom (with 50px tolerance)
  const isAtBottom = () => {
    const el = containerRef.current;
    if (!el) return false;
    return el.scrollHeight - el.scrollTop <= el.clientHeight + 50;
  };

  const getUserColor = (username: string) => {
    let hash = 0;
    for (let i = 0; i < username.length; i++) {
        hash = username.charCodeAt(i) + ((hash << 5) - hash);
    }
    const hue = hash % 360;
    return `hsl(${hue}, 70%, 60%)`;
  };

  // Scroll only if user was already at bottom
  useEffect(() => {
    if (isAtBottom()) {
      bottomRef.current?.scrollIntoView({ behavior: "smooth" });
    }
  }, [messages]);

  const sendMessage = () => {
    if (!input.trim()) return;
    setMessages((prev) => [
      ...prev,
      { user: "redguy9999", text: input, role: "viewer" },
    ]);
    setInput("");
    // Send to backend/socket here
  };

  return (
    <div className="flex flex-col h-[500px] bg-white dark:bg-black border-l m-2 rounded-2xl max-w-xs xl:max-w-sm p-2">
      <div
        ref={containerRef}
        className="flex-1 p-2 overflow-y-scroll overflow-x-hidden space-y-2"
      >
        {messages.map((msg, i) => {
            const color = getUserColor(msg.user);

            return (
                <div key={i} className="text-sm">
                <span className="font-bold mr-1" style={{ color }}>
                    {msg.user}
                </span>
                <span>{msg.text}</span>
                </div>
            );
        })}
        <div ref={bottomRef} />
      </div>
      <div className="p-2 border-t flex">
        <Input
          type="text"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && sendMessage()}
          placeholder="Send a message"
          className="mr-2"
        />
        <Button onClick={sendMessage}>Send</Button>
      </div>
    </div>
  );
};

export default Chat;

