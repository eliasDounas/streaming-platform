'use client';

import React, { useEffect, useRef } from 'react';
import Hls from 'hls.js';

interface IvsPlayerProps {
  playbackUrl: string;
}

const IvsPlayer: React.FC<IvsPlayerProps> = ({ playbackUrl }) => {
  const videoRef = useRef<HTMLVideoElement>(null);

  useEffect(() => {
    if (!playbackUrl || !videoRef.current) return;

    const video = videoRef.current;

    if (Hls.isSupported()) {
      const hls = new Hls();
      hls.loadSource(playbackUrl);
      hls.attachMedia(video);
      hls.on(Hls.Events.MANIFEST_PARSED, () => {
        video.play().catch(() => {
          // Handle autoplay block errors if needed
        });
      });

      return () => {
        hls.destroy();
      };
    } else if (video.canPlayType('application/vnd.apple.mpegurl')) {
      // Safari and some iOS browsers support HLS natively
      video.src = playbackUrl;
      video.addEventListener('loadedmetadata', () => {
        video.play().catch(() => {
          // Handle autoplay block errors if needed
        });
      });
    }
  }, [playbackUrl]);

  return (
    <video
      ref={videoRef}
      controls
      style={{ width: '100%', height: '100%' }}
      playsInline
    />
  );
};

export default IvsPlayer;
