package com.stream.stream_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stream.stream_service.entities.Stream;

import java.util.List;
import java.util.Optional;

@Repository
public interface StreamRepository extends JpaRepository<Stream, Long> {
    
    List<Stream> findByChannelId(String channelId);
 
    Optional<Stream> findByChannelIdAndIsLiveTrue(String channelId);
 
    List<Stream> findByIsLive(Boolean isLive);
    
    @Query("SELECT s FROM Stream s WHERE s.channelId = :channelId AND s.isLive = true")
    List<Stream> findLiveStreamsByChannelId(@Param("channelId") String channelId);
    
    @Query("SELECT s FROM Stream s ORDER BY s.viewCount DESC")
    List<Stream> findAllOrderByViewCountDesc();
}