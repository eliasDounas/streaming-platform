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

export function SignupDialog() {
  const [open, setOpen] = useState(false)
  const { keycloak } = useKeycloak()

  const handleRegister = () => {
    keycloak.register()
    setOpen(false)
  }

  const handleLogin = () => {
    keycloak.login()
    setOpen(false)
  }

  // If user is already authenticated, show user info or logout
  if (keycloak.authenticated) {
    return (
      <Button 
        variant="outline" 
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
        <Button variant="outline" size="sm" className="font-stretch-semi-condensed cursor-pointer">
          Sign Up
        </Button>
      </DialogTrigger>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle className="text-2xl">Create an account</DialogTitle>
          <DialogDescription>Choose how you want to proceed</DialogDescription>
        </DialogHeader>
        <div className="flex flex-col gap-4 pt-2">
          <Button onClick={handleRegister} className="w-full">
            Sign Up
          </Button>
          <Button onClick={handleLogin} variant="outline" className="w-full">
            Already have an account? Login
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  )
}