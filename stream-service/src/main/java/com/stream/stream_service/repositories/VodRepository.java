package com.stream.stream_service.repositories;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stream.stream_service.entities.Vod;

import java.util.List;

@Repository
public interface VodRepository extends JpaRepository<Vod, Long> {
    
    List<Vod> findByChannelId(String channelId, Sort sort);
    List<Vod> findByChannelId(String channelId, Pageable pageable);
}