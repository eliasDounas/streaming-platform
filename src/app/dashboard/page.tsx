import { AppSidebar } from "../../components/sidebar/app-sidebar"
import {
  Breadcrumb,
  BreadcrumbItem,
  BreadcrumbLink,
  BreadcrumbList,
  BreadcrumbPage,
  BreadcrumbSeparator,
} from "@/components/ui/breadcrumb"
import { Separator } from "@/components/ui/separator"
import {
  SidebarInset,
  SidebarProvider,
  SidebarTrigger,
} from "@/components/ui/sidebar"

import { LoginsDialog } from "@/components/auth-ui/logins-dialog"
import { SignupDialog } from "@/components/auth-ui/signup-dialog"
import { ThemeSwitch } from "@/components/theme-switch/ThemeSwitch"
import SearchBar from "@/components/SearchBar"


export default function Page() {
  return (
    <SidebarProvider>
      <AppSidebar />
      <SidebarInset>
        <header className="flex h-14 shrink-0 items-center gap-2 transition-[width,height] ease-linear group-has-data-[collapsible=icon]/sidebar-wrapper:h-12">
          <div className="flex items-center gap-2 px-4">
            <SidebarTrigger className="-ml-1" />
            <Separator
              orientation="vertical"
              className="hidden md:block mr-2 data-[orientation=vertical]:h-4"
            />
            <Breadcrumb className="hidden md:block">
              <BreadcrumbList>
                <BreadcrumbItem>
                  <BreadcrumbLink href="#">
                    Building Your Application
                  </BreadcrumbLink>
                </BreadcrumbItem>
                <BreadcrumbSeparator/>
                <BreadcrumbItem>
                  <BreadcrumbPage>Data Fetching</BreadcrumbPage>
                </BreadcrumbItem>
              </BreadcrumbList>
            </Breadcrumb>
          </div>
          <div className="relative w-full max-w-xs 2xl:max-w-md inline-block md:mx-auto">
            <SearchBar />
          </div>
          <div className="flex gap-2 md:gap-4 mx-2 md:mx-4">
                  <LoginsDialog />
                  <SignupDialog />
                  <ThemeSwitch />
                </div>
          
        </header>
        <div className="flex flex-1 flex-col gap-4 p-4 pt-0 bg-green-600">
          <div className="grid auto-rows-min gap-4 md:grid-cols-3">
            <div className="bg-red-600 aspect-video rounded-xl" />
            <div className="bg-muted/50 aspect-video rounded-xl" />
            <div className="bg-muted/50 aspect-video rounded-xl" />
            <p> kjbbkjddkzja </p>
          </div>
          <div className="bg-muted/50 min-h-[100vh] flex-1 rounded-xl md:min-h-min" /> 
        </div>
      </SidebarInset>
    </SidebarProvider>
  )
}
