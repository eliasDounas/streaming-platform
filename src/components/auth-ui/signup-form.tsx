import type React from "react"
import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"

export function SignupForm({ className, ...props }: React.ComponentPropsWithoutRef<"div">) {
  // Generate arrays for days, months, and years for the date of birth dropdowns
  const days = Array.from({ length: 31 }, (_, i) => i + 1)
  const months = [
    "January",
    "February",
    "March",
    "April",
    "May",
    "June",
    "July",
    "August",
    "September",
    "October",
    "November",
    "December",
  ]
  const currentYear = new Date().getFullYear()
  const years = Array.from({ length: 100 }, (_, i) => currentYear - i - 13) // Start from 13 years ago

  return (
    <div className={cn("flex flex-col gap-4", className)} {...props}>
      <form>
        <div className="flex flex-col gap-4">
          <div className="grid gap-2">
            <Label htmlFor="username">Username</Label>
            <Input id="username" type="text" placeholder="johndoe" required />
          </div>
          <div className="grid gap-2">
            <Label htmlFor="email">Email</Label>
            <Input id="email" type="email" placeholder="john@example.com" required />
          </div>
          <div className="grid gap-2">
            <Label htmlFor="password">Password</Label>
            <Input id="password" type="password" required />
          </div>
          <div className="grid gap-2">
            <Label>Date of Birth</Label>
            <div className="grid grid-cols-3 gap-2">
              <Select>
                <SelectTrigger>
                  <SelectValue placeholder="Day" />
                </SelectTrigger>
                <SelectContent>
                  {days.map((day) => (
                    <SelectItem key={day} value={day.toString()}>
                      {day}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
              <Select>
                <SelectTrigger>
                  <SelectValue placeholder="Month" />
                </SelectTrigger>
                <SelectContent>
                  {months.map((month, index) => (
                    <SelectItem key={month} value={(index + 1).toString()}>
                      {month}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
              <Select>
                <SelectTrigger>
                  <SelectValue placeholder="Year" />
                </SelectTrigger>
                <SelectContent>
                  {years.map((year) => (
                    <SelectItem key={year} value={year.toString()}>
                      {year}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
          </div>
          <Button type="submit" className="w-full cursor-pointer">
            Sign Up
          </Button>
          <div className="after:border-border relative text-center text-sm after:absolute after:inset-0 after:top-1/2 after:z-0 after:flex after:items-center after:border-t">
            <span className="bg-card text-muted-foreground relative z-10 px-2">Or continue with</span>
          </div>
          <div className="flex justify-center">
            <Button variant="outline" className="w-full cursor-pointer">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" className="mr-2 h-4 w-4">
                <path
                  d="M12.48 10.92v3.28h7.84c-.24 1.84-.853 3.187-1.787 4.133-1.147 1.147-2.933 2.4-6.053 2.4-4.827 0-8.6-3.893-8.6-8.72s3.773-8.72 8.6-8.72c2.6 0 4.507 1.027 5.907 2.347l2.307-2.307C18.747 1.44 16.133 0 12.48 0 5.867 0 .307 5.387.307 12s5.56 12 12.173 12c3.573 0 6.267-1.173 8.373-3.36 2.16-2.16 2.84-5.213 2.84-7.667 0-.76-.053-1.467-.173-2.053H12.48z"
                  fill="currentColor"
                />
              </svg>
              Sign Up with Google
            </Button>
          </div>
        </div>
     </form>
      <div className="text-balance text-center text-xs text-muted-foreground [&_a]:underline [&_a]:underline-offset-4 hover:[&_a]:text-primary mt-2">By clicking continue, you agree to our <a href="#">Terms of Service</a> and <a href="#">Privacy Policy</a>.
       </div>
    </div>
  )
}






// import { cn } from "@/lib/utils"
// import { Button } from "@/components/ui/button"
// import { Card, CardContent } from "@/components/ui/card"
// import { Input } from "@/components/ui/input"
// import { Label } from "@/components/ui/label"

// export function SignUpForm({ className, ...props }: React.ComponentProps<"div">) {
//   return (
//     <div className={cn("flex flex-col gap-6", className)} {...props}>
//       <Card className="overflow-hidden">
//         <CardContent className="grid p-0 md:grid-cols-2">
//           <form className="p-6 md:p-8">
//             <div className="flex flex-col gap-6">
//               <div className="flex flex-col items-center text-center">
//                 <h1 className="text-2xl font-bold">Create an account</h1>
//                 <p className="text-balance text-muted-foreground">Sign up for your Acme Inc account</p>
//               </div>
//               <div className="grid gap-2">
//                 <Label htmlFor="username">Username</Label>
//                 <Input id="username" type="text" placeholder="johndoe" required />
//               </div>
//               <div className="grid gap-2">
//                 <Label htmlFor="email">Email</Label>
//                 <Input id="email" type="email" placeholder="m@example.com" required />
//               </div>
//               <div className="grid gap-2">
//                 <Label htmlFor="password">Password</Label>
//                 <Input id="password" type="password" required />
//               </div>
//               <div className="grid gap-2">
//                 <Label htmlFor="dob">Date of Birth</Label>
//                 <div className="relative">
//                   <Input id="dob" type="date" required />
//                 </div>
//               </div>
//               <Button type="submit" className="w-full">
//                 Sign Up
//               </Button>
//               <div className="relative text-center text-sm after:absolute after:inset-0 after:top-1/2 after:z-0 after:flex after:items-center after:border-t after:border-border">
//                 <span className="relative z-10 bg-background px-2 text-muted-foreground">Or sign up with</span>
//               </div>
              
//               <div className="flex justify-center">
//                 <Button variant="outline" className="w-full">
//                   <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" className="mr-2 h-4 w-4">
//                     <path
//                       d="M12.48 10.92v3.28h7.84c-.24 1.84-.853 3.187-1.787 4.133-1.147 1.147-2.933 2.4-6.053 2.4-4.827 0-8.6-3.893-8.6-8.72s3.773-8.72 8.6-8.72c2.6 0 4.507 1.027 5.907 2.347l2.307-2.307C18.747 1.44 16.133 0 12.48 0 5.867 0 .307 5.387.307 12s5.56 12 12.173 12c3.573 0 6.267-1.173 8.373-3.36 2.16-2.16 2.84-5.213 2.84-7.667 0-.76-.053-1.467-.173-2.053H12.48z"
//                       fill="currentColor"
//                     />
//                   </svg>
//                   Sign up with Google
//                 </Button>
//               </div>
//               <div className="text-center text-sm">
//                 Already have an account?{" "}
//                 <a href="#" className="underline underline-offset-4">
//                   Login
//                 </a>
//               </div>
//             </div>
//           </form>
//           <div className="relative hidden bg-muted md:block">
//             <img
//               src="/placeholder.svg"
//               alt="Image"
//               className="absolute inset-0 h-full w-full object-cover dark:brightness-[0.2] dark:grayscale"
//             />
//           </div>
//         </CardContent>
//       </Card>
//       <div className="text-balance text-center text-xs text-muted-foreground [&_a]:underline [&_a]:underline-offset-4 hover:[&_a]:text-primary">
//         By clicking continue, you agree to our <a href="#">Terms of Service</a> and <a href="#">Privacy Policy</a>.
//       </div>
//     </div>
//   )
// }
