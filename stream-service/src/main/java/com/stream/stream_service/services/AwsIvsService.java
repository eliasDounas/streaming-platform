package com.stream.stream_service.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AwsIvsService {
    
    @Value("${aws.s3.thumbnail-bucket}")
    private String thumbnailBucket;
    
    @Value("${aws.s3.region:eu-west-1}")
    private String s3Region;
}
