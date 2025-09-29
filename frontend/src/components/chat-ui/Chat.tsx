"use client";

import { useEffect, useRef, useState } from "react";
import { Button } from "../ui/button";
import { Input } from "../ui/input";
import { useChatToken } from "@/hooks/useSWR";
import { Loader2, AlertCircle } from "lucide-react";

interface ChatMessage {
  id: string;
  user: string;
  text: string;
  timestamp: number;
  userId?: string;
}

interface ChatProps {
  channelId: string;
  username?: string;
}

const Chat = ({ channelId, username = "Anonymous" }: ChatProps) => {
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [input, setInput] = useState("");
  const [chatRoom, setChatRoom] = useState<any>(null);
  const [isConnected, setIsConnected] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const containerRef = useRef<HTMLDivElement | null>(null);
  const bottomRef = useRef<HTMLDivElement | null>(null);

  // Fetch chat token
  const { chatToken, isLoading: tokenLoading, error: tokenError } = useChatToken(channelId);

  // Initialize AWS IVS Chat when token is available
  useEffect(() => {
    if (!chatToken) return;

    const initializeChat = async () => {
      try {
        // Import AWS IVS Chat SDK dynamically
        const { ChatRoom } = await import('amazon-ivs-chat-messaging');
        
        // Initialize chat room
        const room = new ChatRoom({
          regionOrUrl: 'us-west-1', // Adjust to your AWS region
          tokenProvider: () => Promise.resolve(chatToken),
        });        // Event listeners
        room.addListener('connect', () => {
          console.log('Connected to chat');
          setIsConnected(true);
          setError(null);
        });

        room.addListener('disconnect', () => {
          console.log('Disconnected from chat');
          setIsConnected(false);
        });        room.addListener('message', (message: any) => {
          console.log('Received message:', message);
          const chatMessage: ChatMessage = {
            id: message.id,
            user: message.sender?.displayName || message.sender?.userId || 'Unknown',
            text: message.content,
            timestamp: Date.now(),
            userId: message.sender?.userId,
          };
          setMessages(prev => [...prev, chatMessage]);
        });

        // Connect to the chat room
        await room.connect();
        setChatRoom(room);

      } catch (err) {
        console.error('Failed to initialize chat:', err);
        setError('Failed to initialize chat. Please try refreshing the page.');
      }
    };

    initializeChat();

    // Cleanup on unmount
    return () => {
      if (chatRoom) {
        chatRoom.disconnect();
      }
    };
  }, [chatToken]);

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

  const sendMessage = async () => {
    if (!input.trim() || !chatRoom || !isConnected) return;
    
    try {
      // Send message via AWS IVS Chat
      await chatRoom.sendMessage(input.trim());
      setInput("");
    } catch (err) {
      console.error('Failed to send message:', err);
      setError('Failed to send message. Please try again.');
    }
  };

  // Show loading state while fetching token
  if (tokenLoading) {
    return (
      <div className="flex flex-col h-full bg-white dark:bg-black p-1">
        <div className="flex-1 flex items-center justify-center">
          <div className="flex items-center gap-2 text-muted-foreground">
            <Loader2 className="w-4 h-4 animate-spin" />
            <span className="text-sm">Connecting to chat...</span>
          </div>
        </div>
      </div>
    );
  }

  // Show error state if token fetch failed
  if (tokenError) {
    return (
      <div className="flex flex-col h-full bg-white dark:bg-black p-1">
        <div className="flex-1 flex items-center justify-center">
          <div className="flex items-center gap-2 text-red-500">
            <AlertCircle className="w-4 h-4" />
            <span className="text-sm">Failed to connect to chat</span>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="flex flex-col h-full bg-white dark:bg-black p-1">
      {/* Connection status indicator */}
      {!isConnected && chatToken && (
        <div className="px-2 py-1 bg-yellow-100 dark:bg-yellow-900/30 border-b">
          <div className="flex items-center gap-2 text-yellow-700 dark:text-yellow-300">
            <Loader2 className="w-3 h-3 animate-spin" />
            <span className="text-xs">Connecting to chat...</span>
          </div>
        </div>
      )}
      
      {/* Chat error display */}
      {error && (
        <div className="px-2 py-1 bg-red-100 dark:bg-red-900/30 border-b">
          <div className="flex items-center gap-2 text-red-700 dark:text-red-300">
            <AlertCircle className="w-3 h-3" />
            <span className="text-xs">{error}</span>
          </div>
        </div>
      )}

      <div
        ref={containerRef}
        className="flex-1 p-2 overflow-y-scroll overflow-x-hidden space-y-2 overflow-auto" 
      >
        {messages.length === 0 && isConnected && (
          <div className="text-center text-muted-foreground text-sm py-8">
            <p>Welcome to the chat!</p>
            <p className="text-xs mt-1">Be the first to say something...</p>
          </div>
        )}
        {messages.map((msg) => {
            const color = getUserColor(msg.user);

            return (
                <div key={msg.id} className="text-sm">
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
          placeholder={isConnected ? "Send a message" : "Connecting..."}
          disabled={!isConnected || !chatRoom}
          className="mr-2"
        />
        <Button 
          onClick={sendMessage} 
          disabled={!isConnected || !chatRoom || !input.trim()}
        >
          Send
        </Button>
      </div>
    </div>
  );
};

export default Chat;

