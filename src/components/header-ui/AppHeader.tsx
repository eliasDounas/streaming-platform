"use client"

import { 
  Breadcrumb,
  BreadcrumbItem,
  BreadcrumbLink,
  BreadcrumbList,
  BreadcrumbPage,
  BreadcrumbSeparator,
} from "@/components/ui/breadcrumb";
import { Separator } from "@/components/ui/separator";
import { SidebarTrigger } from "@/components/ui/sidebar";
import { LoginsDialog } from "@/components/auth-ui/logins-dialog";
import { SignupDialog } from "@/components/auth-ui/signup-dialog";
import { ThemeSwitch } from "@/components/header-ui/theme-switch/ThemeSwitch";
import SearchBar from "@/components/header-ui/SearchBar";
import { usePathname } from "next/navigation";
import { ChannelStatusButton } from "./ChannelStatusButton";
import { useAuth } from "@/hooks/useAuth";
import { Button } from "@/components/ui/button";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";

export function AppHeader() {
  const pathname = usePathname();
  const { isAuthenticated, getUserInfo, logout } = useAuth();
  
  const getPageName = () => {
    const path = pathname.split('/')[1]; // Get the first segment after '/'
    
    switch (path) {
      case 'blogs':
        return 'Blogs';
      case 'channels':
        return 'Channel';
      case 'dashboard':
        return 'Streamer Dashboard';
      case 'stream':
        return 'Live Stream';
      case 'vods':
        return 'VOD';
      default:
        return 'Home';
    }
  };

  const userInfo = getUserInfo();

  const renderAuthSection = () => {
    if (isAuthenticated && userInfo) {
      return (
        <>
          <ChannelStatusButton />
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="ghost" className="relative h-8 w-8 rounded-full">
                <Avatar className="h-8 w-8">
                  <AvatarImage src={`https://avatar.vercel.sh/${userInfo.username}`} alt={userInfo.username} />
                  <AvatarFallback>{userInfo.username?.charAt(0).toUpperCase()}</AvatarFallback>
                </Avatar>
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent className="w-56" align="end" forceMount>
              <DropdownMenuLabel className="font-normal">
                <div className="flex flex-col space-y-1">
                  <p className="text-sm font-medium leading-none">{userInfo.name || userInfo.username}</p>
                  <p className="text-xs leading-none text-muted-foreground">
                    {userInfo.email}
                  </p>
                </div>
              </DropdownMenuLabel>
              <DropdownMenuSeparator />
              <DropdownMenuItem onClick={logout}>
                Log out
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </>
      );
    }

    return (
      <>
        <LoginsDialog />
        <SignupDialog />
      </>
    );
  };

  return (
    <header className="sticky top-0 z-10 flex h-14 shrink-0 items-center gap-2 transition-[width,height] ease-linear group-has-data-[collapsible=icon]/sidebar-wrapper:h-12 bg-background">
      <div className="flex items-center gap-2 px-4">
        <SidebarTrigger className="-ml-1" />
        <Separator
          orientation="vertical"
          className="hidden md:block mr-2 data-[orientation=vertical]:h-4"
        />
        <Breadcrumb className="hidden md:block">
          <BreadcrumbList>
            <BreadcrumbItem>
              <BreadcrumbLink href="/">
                UpStream
              </BreadcrumbLink>
            </BreadcrumbItem>            <BreadcrumbSeparator />
            <BreadcrumbItem>
              <BreadcrumbPage>{getPageName()}</BreadcrumbPage>
            </BreadcrumbItem>
          </BreadcrumbList>
        </Breadcrumb>
      </div>
      <div className="relative w-full max-w-xs 2xl:max-w-md inline-block md:mx-auto">
        <SearchBar />
      </div>      <div className="flex gap-2 md:gap-4 mx-2 md:mx-4">
        {renderAuthSection()}
        <ThemeSwitch />
      </div>
    </header>
  );
}
