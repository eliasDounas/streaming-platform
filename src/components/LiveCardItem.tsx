import Image from "next/image";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Eye } from "lucide-react";
import Globe from "../../public/KUTtrV3.png"; // Replace with your own thumbnail

const LiveCard = () => {
  return (
    <div className="w-full max-w-[400px] rounded overflow-hidden dark:bg-black/80 shadow hover:shadow-lg transition-shadow duration-300">
      {/* Thumbnail */}
      <div className="relative w-full h-[140px] overflow-hidden">
        <Image
          src={Globe}
          alt="Stream thumbnail"
          className="object-cover w-full h-full transition-transform duration-300 group-hover:scale-105"
        />
        {/* Live badge */}
        <div className="absolute top-2 left-2 bg-red-600 text-white text-sm font-bold px-2 py-0.5 rounded">
          LIVE
        </div>
        {/* Viewer count */}
        <div className="absolute bottom-2 right-2 bg-black bg-opacity-60 text-white text-xs px-2 py-0.5 rounded-md flex items-center gap-1">
          <Eye className="w-3 h-3" />
          <span>1.2K</span>
        </div>
        
      </div>

      {/* Stream Info */}
      <div className="flex items-start gap-3 px-4 py-3">
        <Avatar className="w-10 h-10">
          <AvatarImage src="https://github.com/shadcn.png" alt="Streamer avatar" />
          <AvatarFallback>CN</AvatarFallback>
        </Avatar>
        <div className="flex flex-col overflow-hidden">
          <span className="font-semibold truncate">Bouz Bouz</span>
          <span className="text-sm truncate">
            🔥 Insane 1v5 Comeback Stream
          </span>
          <span className="text-xs text-muted-foreground mt-1 truncate">
            Just Chatting
          </span>
        </div>
      </div>
    </div>
  );
};

export default LiveCard;