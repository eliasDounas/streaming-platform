"use client"

import { useState } from "react"
import { useKeycloak } from '@react-keycloak/web'
import { Button } from "@/components/ui/button"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog"

export function LoginsDialog() {
  const [open, setOpen] = useState(false)
  const { keycloak } = useKeycloak()

  const handleLogin = () => {
    keycloak.login()
    setOpen(false)
  }

  const handleRegister = () => {
    keycloak.register()
    setOpen(false)
  }

  // If user is already authenticated, show logout option
  if (keycloak.authenticated) {
    return (
      <Button 
        size="sm" 
        className="font-stretch-semi-condensed cursor-pointer"
        onClick={() => keycloak.logout()}
      >
        Logout
      </Button>
    )
  }

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button size="sm" className="font-stretch-semi-condensed cursor-pointer">Login</Button>
      </DialogTrigger>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle className="text-2xl">Authentication</DialogTitle>
          <DialogDescription>Choose how you want to proceed</DialogDescription>
        </DialogHeader>
        <div className="flex flex-col gap-4 pt-2">
          <Button onClick={handleLogin} className="w-full">
            Login
          </Button>
          <Button onClick={handleRegister} variant="outline" className="w-full">
            Sign Up
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  )
}
