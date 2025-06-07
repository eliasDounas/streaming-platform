"use client";

import React, { useState } from 'react';
import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Loader2, Plus } from 'lucide-react';
import { channelApi } from '@/lib/api';

interface CreateChannelDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onChannelCreated?: () => void;
}

interface ChannelCreateRequest {
  name: string;
  description: string;
  avatarUrl: string;
}

interface ChannelCreateResponse {
  channelId: string;
  message: string;
}

export function CreateChannelDialog({ open, onOpenChange, onChannelCreated }: CreateChannelDialogProps) {
  const [formData, setFormData] = useState<ChannelCreateRequest>({
    name: '',
    description: '',
    avatarUrl: '',
  });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const handleInputChange = (field: keyof ChannelCreateRequest, value: string) => {
    // For channel name, enforce AWS IVS naming conventions
    if (field === 'name') {
      // Remove spaces and invalid characters, keep only alphanumeric, hyphens, and underscores
      value = value.replace(/[^a-zA-Z0-9\-_]/g, '');
    }
    
    setFormData(prev => ({
      ...prev,
      [field]: value,
    }));
    // Clear error when user starts typing
    if (error) setError(null);
  };
  const validateChannelName = (name: string): string | null => {
    if (!name.trim()) {
      return 'Channel name is required';
    }
    if (name.length < 3) {
      return 'Channel name must be at least 3 characters long';
    }
    if (name.length > 20) {
      return 'Channel name must be 20 characters or less';
    }
    // Must start with a letter
    if (!/^[a-zA-Z]/.test(name)) {
      return 'Channel name must start with a letter';
    }
    // AWS IVS channel name validation
    if (!/^[a-zA-Z0-9\-_]+$/.test(name)) {
      return 'Channel name can only contain letters, numbers, hyphens, and underscores';
    }
    // Prevent consecutive special characters
    if (/[-_]{2,}/.test(name)) {
      return 'Channel name cannot have consecutive hyphens or underscores';
    }
    // Cannot start or end with special characters
    if (name.startsWith('-') || name.endsWith('-') || name.startsWith('_') || name.endsWith('_')) {
      return 'Channel name cannot start or end with hyphens or underscores';
    }
    return null;
  };

  const validateForm = (): boolean => {
    const nameError = validateChannelName(formData.name);
    if (nameError) {
      setError(nameError);
      return false;
    }
    
    if (!formData.description.trim()) {
      setError('Channel description is required');
      return false;
    }
    if (formData.description.trim().length < 10) {
      setError('Channel description must be at least 10 characters long');
      return false;
    }
    return true;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validateForm()) return;

    setIsSubmitting(true);
    setError(null);

    try {
      const response = await channelApi.post<ChannelCreateResponse>('/channels', {
        name: formData.name.trim(),
        description: formData.description.trim(),
        avatarUrl: formData.avatarUrl.trim() || undefined, // Send undefined if empty
      });

      if (response.data) {
        // Reset form
        setFormData({
          name: '',
          description: '',
          avatarUrl: '',
        });
        
        // Close dialog
        onOpenChange(false);
        
        // Notify parent component
        onChannelCreated?.();
      }
    } catch (err: any) {
      console.error('Channel creation error:', err);
      if (err.response?.data?.message) {
        setError(err.response.data.message);
      } else if (err.response?.status === 409) {
        setError('A channel with this name already exists');
      } else {
        setError('Failed to create channel. Please try again.');
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleClose = () => {
    if (!isSubmitting) {
      setFormData({
        name: '',
        description: '',
        avatarUrl: '',
      });
      setError(null);
      onOpenChange(false);
    }
  };

  return (
    <Dialog open={open} onOpenChange={handleClose}>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2">
            <Plus className="w-5 h-5" />
            Create Your Channel
          </DialogTitle>
          <DialogDescription>
            Set up your streaming channel. You can always update these details later.
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-4">          {/* Channel Name */}
          <div className="space-y-2">
            <Label htmlFor="name">Channel Name *</Label>            <Input
              id="name"
              type="text"
              placeholder="gaming-channel-2024"
              value={formData.name}
              onChange={(e) => handleInputChange('name', e.target.value)}
              disabled={isSubmitting}
              maxLength={20}
              className="w-full"
            />            <p className="text-xs text-muted-foreground">
              {formData.name.length}/20 characters • Must start with a letter, no consecutive special characters
            </p>
          </div>

          {/* Channel Description */}
          <div className="space-y-2">
            <Label htmlFor="description">Description *</Label>
            <Textarea
              id="description"
              placeholder="Describe what your channel is about..."
              value={formData.description}
              onChange={(e) => handleInputChange('description', e.target.value)}
              disabled={isSubmitting}
              maxLength={500}
              rows={3}
              className="w-full resize-none"
            />
            <p className="text-xs text-muted-foreground">
              {formData.description.length}/500 characters
            </p>
          </div>

          {/* Avatar URL */}
          <div className="space-y-2">
            <Label htmlFor="avatarUrl">Avatar URL (optional)</Label>
            <Input
              id="avatarUrl"
              type="url"
              placeholder="https://example.com/your-avatar.jpg"
              value={formData.avatarUrl}
              onChange={(e) => handleInputChange('avatarUrl', e.target.value)}
              disabled={isSubmitting}
              className="w-full"
            />
            <p className="text-xs text-muted-foreground">
              Leave empty to use a default avatar
            </p>
          </div>

          {/* Error Message */}
          {error && (
            <div className="bg-destructive/10 border border-destructive/20 rounded-md p-3">
              <p className="text-sm text-destructive">{error}</p>
            </div>
          )}

          <DialogFooter>
            <Button
              type="button"
              variant="outline"
              onClick={handleClose}
              disabled={isSubmitting}
            >
              Cancel
            </Button>
            <Button
              type="submit"
              disabled={isSubmitting || !formData.name.trim() || !formData.description.trim()}
            >
              {isSubmitting ? (
                <>
                  <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                  Creating...
                </>
              ) : (
                <>
                  Create Channel
                </>
              )}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
