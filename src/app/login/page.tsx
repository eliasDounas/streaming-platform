import { LoginsDialog } from "@/components/auth-ui/logins-dialog"
import { SignupDialog } from "@/components/auth-ui/signup-dialog"
import { ThemeSwitch } from "@/components/theme-switch/ThemeSwitch"

export default function LoginPage() {
  return (
    <div className="flex min-h-svh w-full flex-col items-center justify-center gap-4 p-6 md:p-10">
      <div className="flex gap-4">
        <LoginsDialog />
        <SignupDialog />
        <ThemeSwitch />
      </div>
      <p className="text-sm text-muted-foreground">Click on either button to open the respective dialog</p>
    </div>
  )
}
