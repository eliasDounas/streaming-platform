package com.stream.stream_service.repositories;

import com.stream.stream_service.entities.DefaultStreamInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DefaultStreamInfoRepository extends JpaRepository<DefaultStreamInfo, String> {
    // Additional query methods if needed
}
