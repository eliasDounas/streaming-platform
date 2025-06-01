package com.stream.stream_service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import com.stream.stream_service.entities.Stream;
import com.stream.stream_service.entities.Vod;
import com.stream.stream_service.exceptions.ApiException;
import com.stream.stream_service.repositories.StreamRepository;
import com.stream.stream_service.repositories.VodRepository;

import jakarta.transaction.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class VodService {
       @Autowired
       private VodRepository vodRepository;
   
       @Autowired
       private StreamRepository streamRepository;
   
       // 1. Create Vod from streamId
       @Transactional
       public Vod createVod(Long streamId) {
           Stream stream = streamRepository.findById(streamId)
                   .orElseThrow(() -> new ApiException("Stream not found", HttpStatus.NOT_FOUND));
   
           if (stream.getStartedAt() == null || stream.getEndedAt() == null) {
               throw new ApiException("Stream didn't end yet", HttpStatus.BAD_REQUEST);
           }
   
           long duration = Duration.between(stream.getStartedAt(), stream.getEndedAt()).getSeconds();
   
           Vod vod = new Vod();
           vod.setStream(stream);
           vod.setChannelId(stream.getChannelId());
           vod.setTitle(stream.getTitle());
           vod.setDescription(stream.getDescription());
           vod.setViews(0L);
           vod.setDuration(duration);
           vod.setCreatedAt(LocalDateTime.now());
           vod.setThumbnailUrl(stream.getThumbnailUrl());
            // Set vod_url
           return vodRepository.save(vod);
       }
   
       // 2. Update Vod Title and/or Description
       @Transactional
       public Vod updateVod(Long vodId, String title, String description) {
           Vod vod = vodRepository.findById(vodId)
                   .orElseThrow(() -> new ApiException("Vod not found" , HttpStatus.NOT_FOUND));
   
           if (title != null && !title.isBlank()) {
               vod.setTitle(title);
           }
   
           if (description != null) {
               vod.setDescription(description);
           }
   
           return vodRepository.save(vod);
       }
   
       // 3. Increment Views by 1
        @Transactional
        public Vod incrementViews(Long vodId) {
            Vod vod = vodRepository.findById(vodId)
                .orElseThrow(() -> new ApiException("Vod not found" , HttpStatus.NOT_FOUND));
   
            vod.setViews(vod.getViews() + 1);
            return vodRepository.save(vod);
       }
   
       // 4. Delete Vod with authorization check
        @Transactional
        public void deleteVod(Long vodId, String userId) {
            Vod vod = vodRepository.findById(vodId)
                .orElseThrow(() -> new ApiException("Vod not found with id: " + vodId, HttpStatus.NOT_FOUND));
   
           if (!checkAuthorization(vod, userId)) {
               throw new ApiException("Not authorized to delete this Vod", HttpStatus.FORBIDDEN);
           }
   
           vodRepository.delete(vod);
       }
   
       // Stub authorization check: implement your logic here
       private boolean checkAuthorization(Vod vod, String user) {
           // Example: allow only if user owns the channelId or is admin
           // For now just allow all:
           return true;
       }
   
       // 5a. Get all Vods, optionally limit by max and order by createdAt desc
       public List<Vod> getVods(Integer max) {
           List<Vod> vods;
           if (max != null && max > 0) {
               Pageable pageable = PageRequest.of(0, max, Sort.by(Sort.Direction.DESC, "createdAt"));
               vods = vodRepository.findAll(pageable).getContent();
           } else {
               vods = vodRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
           }
           return vods;
       }
   
       // 5b. Get Vods by channelId, optionally limit by max and order by createdAt desc
       public List<Vod> getVodsByChannelId(String channelId, Integer max) {
           List<Vod> vods;
           if (max != null && max > 0) {
               Pageable pageable = PageRequest.of(0, max, Sort.by(Sort.Direction.DESC, "createdAt"));
               vods = vodRepository.findByChannelId(channelId, pageable);
           } else {
               vods = vodRepository.findByChannelId(channelId, Sort.by(Sort.Direction.DESC, "createdAt"));
           }
           return vods;
       }
}
