package com.channel.channel_service.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.channel.channel_service.entities.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByChannel_ChannelId(String channelId);
}
