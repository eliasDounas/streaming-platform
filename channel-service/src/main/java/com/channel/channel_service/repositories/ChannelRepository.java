package com.channel.channel_service.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.channel.channel_service.entities.Channel;

public interface ChannelRepository extends JpaRepository<Channel, String> {
    Optional<Channel> findByUserId(Long userId);
    Optional<Channel> findByArn(String channelArn);
    List<Channel> findTop10ByIsLiveTrue();
    @Query("SELECT c.channelId FROM Channel c")
    List<String> findAllChannelIds();
}

