import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Dot } from "lucide-react"

type LiveChannelItemProps = {
  avatarUrl: string
  username: string
  category: string
  viewers: string // formatted like "79.6K"
}

export function LiveChannelItem({
  avatarUrl,
  username,
  category,
  viewers,
}: LiveChannelItemProps) {
  return (
    <div className="flex items-center justify-between pr-3 py-1 hover:bg-muted transition rounded-md">
      <div className="flex items-center gap-3">
        <Avatar className="w-8 h-8 rounded-lg">
          <AvatarImage src={avatarUrl} alt={username} />
          <AvatarFallback>{username.charAt(0).toUpperCase()}</AvatarFallback>
        </Avatar>
        <div className="flex flex-col">
          <span className="text-sm font-medium">{username}</span>
          <span className="text-xs text-muted-foreground">{category}</span>
        </div>
      </div>
      <div className="flex items-center text-sm text-muted-foreground">
        <Dot className="text-red-500 fill-red-500 w-10 h-10 -mr-3" />
        {viewers}
      </div>
    </div>
  )
}
