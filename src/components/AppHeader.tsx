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
import { ThemeSwitch } from "@/components/theme-switch/ThemeSwitch";
import SearchBar from "@/components/SearchBar";
import { usePathname } from "next/navigation";

export function AppHeader() {
  const pathname = usePathname();
    const getPageName = () => {
    const path = pathname.split('/')[1]; // Get the first segment after '/'
    
    switch (path) {
      case 'blogs':
        return 'Blogs';
      case 'channel':
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
      </div>
      <div className="flex gap-2 md:gap-4 mx-2 md:mx-4">
        {/* TO DO - if not auth, we render this block, otherwise we rended ChannelStatusButton */}
        <LoginsDialog />
        <SignupDialog />
        <ThemeSwitch />
      </div>
    </header>
  );
}
