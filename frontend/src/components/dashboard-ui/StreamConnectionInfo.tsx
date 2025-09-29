"use client"

import { useState } from 'react';
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { useStreamConnectionInfo } from '@/hooks/useSWR';
import { Copy, Eye, EyeOff, Loader2, RefreshCw } from 'lucide-react';



export function StreamConnectionInfo() {
  const { connectionInfo, isLoading, error, refresh } = useStreamConnectionInfo();
  const [showStreamKey, setShowStreamKey] = useState(false);
  const [copiedField, setCopiedField] = useState<string | null>(null);

  // Format the ingest endpoint to proper RTMPS URL
  const formatIngestEndpoint = (endpoint: string): string => {
    // If it already starts with rtmps://, return as is
    if (endpoint.startsWith('rtmps://')) {
      return endpoint;
    }
    // Otherwise, format it properly
    return `rtmps://${endpoint}:443/app/`;
  };
  const copyToClipboard = async (text: string, field: string) => {
    try {
      await navigator.clipboard.writeText(text);
      setCopiedField(field);
      // Clear the copied state after 2 seconds
      setTimeout(() => setCopiedField(null), 2000);
      console.log(`${field} copied to clipboard`);
    } catch (err) {
      console.error('Failed to copy to clipboard:', err);
    }
  };

  if (isLoading) {
    return (
      <Card className="w-full max-w-2xl mx-auto">
        <CardHeader>
          <CardTitle>Stream Connection Info</CardTitle>
          <CardDescription>
            Loading your streaming configuration...
          </CardDescription>
        </CardHeader>
        <CardContent className="space-y-6">
          <div className="flex items-center justify-center py-8">
            <Loader2 className="h-8 w-8 animate-spin" />
          </div>
        </CardContent>
      </Card>
    );
  }

  if (error) {
    return (
      <Card className="w-full max-w-2xl mx-auto">
        <CardHeader>
          <CardTitle>Stream Connection Info</CardTitle>
          <CardDescription>
            Failed to load streaming configuration
          </CardDescription>
        </CardHeader>
        <CardContent className="space-y-6">
          <div className="text-center py-8">
            <p className="text-red-500 mb-4">
              {error.message || 'Failed to load stream connection info'}
            </p>
            <Button onClick={() => refresh()} variant="outline" className="gap-2">
              <RefreshCw className="h-4 w-4" />
              Retry
            </Button>
          </div>
        </CardContent>
      </Card>
    );
  }

  if (!connectionInfo) {
    return (
      <Card className="w-full max-w-2xl mx-auto">
        <CardHeader>
          <CardTitle>Stream Connection Info</CardTitle>
          <CardDescription>
            No streaming configuration found
          </CardDescription>
        </CardHeader>
        <CardContent className="space-y-6">
          <div className="text-center py-8">
            <p className="text-muted-foreground mb-4">
              No stream connection information available for this user.
            </p>
            <Button onClick={() => refresh()} variant="outline" className="gap-2">
              <RefreshCw className="h-4 w-4" />
              Refresh
            </Button>
          </div>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card className="w-full max-w-2xl mx-auto">
      <CardHeader>
        <CardTitle>Stream Connection Info</CardTitle>
        <CardDescription>
          Use these settings to configure your streaming software (OBS, Streamlabs, etc.)
        </CardDescription>
      </CardHeader>
      <CardContent className="space-y-6">        {/* Ingest Endpoint */}
        <div className="space-y-2">
          <Label htmlFor="ingestEndpoint">Ingest Endpoint (Server URL)</Label>
          <div className="flex gap-2">
            <Input
              id="ingestEndpoint"
              type="text"
              value={formatIngestEndpoint(connectionInfo.ingestEndpoint)}
              readOnly
              className="font-mono"
            />            <Button
              size="sm"
              variant="outline"
              onClick={() => copyToClipboard(formatIngestEndpoint(connectionInfo.ingestEndpoint), 'Ingest Endpoint')}
              className="shrink-0"
            >
              {copiedField === 'Ingest Endpoint' ? (
                <span className="text-green-600 font-medium">Copied!</span>
              ) : (
                <Copy className="h-4 w-4" />
              )}
            </Button>
          </div>
        </div>

        {/* Stream Key */}
        <div className="space-y-2">
          <Label htmlFor="streamKey">Stream Key</Label>
          <div className="flex gap-2">
            <div className="relative flex-1">
              <Input
                id="streamKey"
                type={showStreamKey ? "text" : "password"}
                value={connectionInfo.streamKey}
                readOnly
                className="font-mono pr-10"
              />
              <Button
                size="sm"
                variant="ghost"
                onClick={() => setShowStreamKey(!showStreamKey)}
                className="absolute right-0 top-0 h-full px-3"
              >
                {showStreamKey ? (
                  <EyeOff className="h-4 w-4" />
                ) : (
                  <Eye className="h-4 w-4" />
                )}
              </Button>
            </div>            <Button
              size="sm"
              variant="outline"
              onClick={() => copyToClipboard(connectionInfo.streamKey, 'Stream Key')}
              className="shrink-0"
            >
              {copiedField === 'Stream Key' ? (
                <span className="text-green-600 font-medium">Copied!</span>
              ) : (
                <Copy className="h-4 w-4" />
              )}
            </Button>
          </div>
          <p className="text-sm text-muted-foreground">
            Keep your stream key private. Anyone with this key can stream to your channel.
          </p>
        </div>

        {/* Channel ID (for reference) */}
        <div className="space-y-2">
          <Label htmlFor="channelId">Channel ID</Label>
          <div className="flex gap-2">
            <Input
              id="channelId"
              type="text"
              value={connectionInfo.channelId}
              readOnly
              className="font-mono"
            />            <Button
              size="sm"
              variant="outline"
              onClick={() => copyToClipboard(connectionInfo.channelId, 'Channel ID')}
              className="shrink-0"
            >
              {copiedField === 'Channel ID' ? (
                <span className="text-green-600 font-medium">Copied!</span>
              ) : (
                <Copy className="h-4 w-4" />
              )}
            </Button>
          </div>
        </div>

        {/* Instructions */}
        <div className="bg-muted p-4 rounded-lg">
          <h4 className="font-medium mb-2">How to use:</h4>
          <ol className="text-sm text-muted-foreground space-y-1 list-decimal list-inside">
            <li>Copy the <strong>Ingest Endpoint</strong> and paste it as your "Server URL" in OBS</li>
            <li>Copy the <strong>Stream Key</strong> and paste it as your "Stream Key" in OBS</li>
            <li>Configure your video and audio settings</li>
            <li>Click "Start Streaming" in OBS</li>
          </ol>
        </div>

        {/* Refresh Button */}
        <div className="flex justify-center pt-4">
          <Button onClick={() => refresh()} variant="outline" className="gap-2 cursor-pointer">
            <RefreshCw className="h-4 w-4" />
            Refresh Connection Info
          </Button>
        </div>
      </CardContent>
    </Card>
  );
}
