package com.stream.stream_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stream.stream_service.entities.Vod;

import java.util.List;

@Repository
public interface VodRepository extends JpaRepository<Vod, Long> {
    
    List<Vod> findByChannelId(String channelId);
    
    List<Vod> findByStreamId(Long streamId);
    
    @Query("SELECT v FROM Vod v WHERE v.channelId = :channelId ORDER BY v.createdAt DESC")
    List<Vod> findByChannelIdOrderByCreatedAtDesc(@Param("channelId") String channelId);
    
    @Query("SELECT v FROM Vod v ORDER BY v.createdAt DESC")
    List<Vod> findAllOrderByCreatedAtDesc();
}