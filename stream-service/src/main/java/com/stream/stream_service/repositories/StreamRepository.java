package com.stream.stream_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import com.stream.stream_service.entities.Stream;
import java.util.List;
import java.util.Optional;

@Repository
public interface StreamRepository extends JpaRepository<Stream, String> {
    
    List<Stream> findByChannelId(String channelId);
 
    Optional<Stream> findByChannelIdAndIsLiveTrue(String channelId);
 
    List<Stream> findByIsLive(Boolean isLive);
    
    // Find finished streams for a channel, ordered by start date descending (latest first)
    List<Stream> findByChannelIdAndIsLiveFalseOrderByStartedAtDesc(String channelId, Pageable pageable);
    
    // Count finished streams for a channel
    long countByChannelIdAndIsLiveFalse(String channelId);
    
    // Find all finished streams ordered by views descending (most viewed first)
    List<Stream> findByIsLiveFalseOrderByViewersDesc(Pageable pageable);
    
    // Count all finished streams
    long countByIsLiveFalse();
      // Find the most recent stream for a channel (for updating VOD URL)
    Optional<Stream> findTopByChannelIdOrderByStartedAtDesc(String channelId);
      // Find the most recent finished stream for a channel (for updating VOD URL)
    Optional<Stream> findTopByChannelIdAndIsLiveFalseOrderByEndedAtDesc(String channelId);
    
    // Find stream by AWS stream ID
    Optional<Stream> findByAwsStreamId(String awsStreamId);
}