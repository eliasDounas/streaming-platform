package com.stream.stream_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stream.stream_service.entities.Stream;

import java.util.List;
import java.util.Optional;

@Repository
public interface StreamRepository extends JpaRepository<Stream, Long> {
    
    List<Stream> findByChannelId(String channelId);
 
    Optional<Stream> findByChannelIdAndIsLiveTrue(String channelId);
 
    List<Stream> findByIsLive(Boolean isLive);
    
    
}