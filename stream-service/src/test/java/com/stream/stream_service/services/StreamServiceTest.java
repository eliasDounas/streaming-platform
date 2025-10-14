package com.stream.stream_service.services;

import com.stream.stream_service.DTO.ChannelDto;
import com.stream.stream_service.DTO.PaginatedStreamResponse;
import com.stream.stream_service.DTO.StreamWithChannelDto;
import com.stream.stream_service.entities.DefaultStreamInfo;
import com.stream.stream_service.entities.Stream;
import com.stream.stream_service.enums.StreamCategory;
import com.stream.stream_service.exceptions.ApiException;
import com.stream.stream_service.gRPC.ChannelGrpcClient;
import com.stream.stream_service.repositories.StreamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StreamServiceTest {

    @Mock
    private StreamRepository streamRepository;

    @Mock
    private DefaultStreamInfoService defaultStreamInfoService;

    @Mock
    private ChannelGrpcClient channelGrpcClient;

    @InjectMocks
    private StreamService streamService; 

    @InjectMocks
    private StreamQueryService streamQueryService;  // Real service with fake dependencies injected

    private Stream testStream;
    private ChannelDto testChannel;
    private DefaultStreamInfo testDefaultInfo;

    @BeforeEach
    void setUp() {
        testStream = new Stream();
        testStream.setId("stream-123");
        testStream.setChannelId("channel-123");
        testStream.setAwsStreamId("aws-stream-123");
        testStream.setTitle("Test Stream");
        testStream.setDescription("Test Description");
        testStream.setCategory(StreamCategory.GAMING);
        testStream.setIsLive(true);
        testStream.setViewers(100L);
        testStream.setStartedAt(LocalDateTime.now());

        testChannel = new ChannelDto("channel-123", "Test Channel", "rtmp://test", "avatar.jpg");

        testDefaultInfo = new DefaultStreamInfo();
        testDefaultInfo.setChannelId("channel-123");
        testDefaultInfo.setTitle("Default Title");
        testDefaultInfo.setDescription("Default Description");
        testDefaultInfo.setCategory(StreamCategory.GAMING);
    }

    /**
     * Test: createStream with valid ARN and AWS stream ID
     * 
     * This test verifies the successful creation of a new live stream when:
     * - A valid channel ARN is provided
     * - An AWS stream ID is specified
     * - The channel exists in the channel service
     * - Default stream info is available for the channel
     * 
     * Expected behavior:
     * - Fetches channel info via gRPC call using the provided ARN
     * - Retrieves default stream configuration (title, description, category)
     * - Creates a new Stream entity with correct initial state (live=true, viewers=0)
     * - Sets the current timestamp as startedAt, leaves endedAt as null
     * - Saves the stream to the database
     */
    @Test
    void createStream_WithValidArn_ShouldCreateStream() {
        // Given
        String arn = "test-arn";
        String awsStreamId = "aws-123";
        
        when(channelGrpcClient.getChannelByArn(arn)).thenReturn(testChannel);
        when(defaultStreamInfoService.findByChannelId("channel-123")).thenReturn(Optional.of(testDefaultInfo));

        //thenReturn is evaluated at setup not execution, and you can only return a pre-built object unlike thenAnswer
        when(streamRepository.save(any(Stream.class))).thenAnswer(invocation -> {
            Stream stream = invocation.getArgument(0); //invocation = actual argument
            stream.setId("stream-123");
            return stream;
        });

        // When
        Stream result = streamService.createStream(arn, awsStreamId);

        // Then
        assertNotNull(result);
        assertEquals("channel-123", result.getChannelId());
        assertEquals("aws-123", result.getAwsStreamId());
        assertEquals("Default Title", result.getTitle());
        assertEquals("Default Description", result.getDescription());
        assertEquals(StreamCategory.GAMING, result.getCategory());
        assertTrue(result.getIsLive());
        assertEquals(0L, result.getViewers());
        assertNotNull(result.getStartedAt());
        assertNull(result.getEndedAt());

        // verify checks if a mock method was called during test execution
        verify(channelGrpcClient).getChannelByArn(arn);
        verify(defaultStreamInfoService).findByChannelId("channel-123");
        verify(streamRepository).save(any(Stream.class));
    }

    /**
     * Test: createStream with invalid ARN should throw ApiException
     * 
     * This test verifies error handling when attempting to create a stream with:
     * - An ARN that doesn't correspond to any existing channel
     * 
     * Expected behavior:
     * - The gRPC call to get channel info returns null
     * - An ApiException is thrown with "Channel not found" message
     * - The exception has HTTP 404 NOT_FOUND status
     * - No stream is created or saved to the database
     */
    @Test
    void createStream_WithInvalidArn_ShouldThrowException() {
        // Given
        String arn = "invalid-arn";
        when(channelGrpcClient.getChannelByArn(arn)).thenReturn(null);

        // When & Then
        ApiException exception = assertThrows(ApiException.class, 
            () -> streamService.createStream(arn, "aws-123"));
        
        assertEquals("Channel not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    /**
     * Test: createStream without default stream info should use fallback defaults
     * 
     * This test verifies the fallback behavior when:
     * - A valid channel exists but has no default stream configuration
     * - The defaultStreamInfoService returns empty Optional
     * 
     * Expected behavior:
     * - Uses hardcoded fallback values for stream metadata:
     *   - Title: "Untitled-Stream"
     *   - Description: "No description available"  
     *   - Category: StreamCategory.OTHER
     * - Stream is still created successfully with these default values
     */
    @Test
    void createStream_WithoutDefaultInfo_ShouldUseDefaults() {
        // Given
        String arn = "test-arn";
        when(channelGrpcClient.getChannelByArn(arn)).thenReturn(testChannel);
        when(defaultStreamInfoService.findByChannelId("channel-123")).thenReturn(Optional.empty());
        when(streamRepository.save(any(Stream.class))).thenAnswer(invocation -> {
            Stream stream = invocation.getArgument(0);
            stream.setId("stream-123");
            return stream;
        });

        // When
        Stream result = streamService.createStream(arn, "aws-123");

        // Then
        assertEquals("Untitled-Stream", result.getTitle());
        assertEquals("No description available", result.getDescription());
        assertEquals(StreamCategory.OTHER, result.getCategory());
    }

    /**
     * Test: getLiveStreamByChannelId with existing live stream
     * 
     * This test verifies retrieval of a live stream for a specific channel when:
     * - The channel has an active live stream (isLive = true)
     * - The channel information is available from the channel service
     * 
     * Expected behavior:
     * - Queries the database for live streams by channel ID
     * - Fetches channel preview information via gRPC
     * - Returns a StreamWithChannelDto combining both stream and channel data
     * - The returned DTO contains the correct stream and channel objects
     */
    @Test
    void getLiveStreamByChannelId_WithExistingStream_ShouldReturnStreamWithChannel() {
        // Given
        String channelId = "channel-123";
        when(streamRepository.findByChannelIdAndIsLiveTrue(channelId)).thenReturn(Optional.of(testStream));
        when(channelGrpcClient.getChannelPreviewsByIds(List.of(channelId))).thenReturn(List.of(testChannel));

        // When
        Optional<StreamWithChannelDto> result = streamQueryService.getLiveStreamByChannelId(channelId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testStream, result.get().getStream());
        assertEquals(testChannel, result.get().getChannel());
    }

    /**
     * Test: getLiveStreamByChannelId when channel has no live stream
     * 
     * This test verifies the behavior when:
     * - A channel exists but currently has no active live stream
     * - The database query returns empty Optional
     * 
     * Expected behavior:
     * - Returns empty Optional indicating no live stream found
     * - No gRPC calls are made since there's no stream to enrich with channel data
     * - Gracefully handles the "no live stream" scenario without errors
     */
    @Test
    void getLiveStreamByChannelId_WithNoLiveStream_ShouldReturnEmpty() {
        // Given
        String channelId = "channel-123";
        when(streamRepository.findByChannelIdAndIsLiveTrue(channelId)).thenReturn(Optional.empty());

        // When
        Optional<StreamWithChannelDto> result = streamQueryService.getLiveStreamByChannelId(channelId);

        // Then
        assertFalse(result.isPresent());
    }

    /**
     * Test: getLiveStreams with multiple active streams
     * 
     * This test verifies retrieval of all currently live streams when:
     * - Multiple channels have active live streams
     * - Channel information is available for all streams
     * 
     * Expected behavior:
     * - Queries database for all streams where isLive = true
     * - Extracts unique channel IDs from the live streams
     * - Fetches channel preview data for all channels in a single gRPC call
     * - Maps channel data to streams and returns enriched StreamWithChannelDto list
     * - Maintains correct stream-to-channel relationships
     * - Returns all live streams with their associated channel information
     */
    @Test
    void getLiveStreams_WithMultipleStreams_ShouldReturnAllWithChannelInfo() {
        // Given
        Stream stream2 = new Stream();
        stream2.setId("stream-456");
        stream2.setChannelId("channel-456");
        
        ChannelDto channel2 = new ChannelDto("channel-456", "Channel 2", "rtmp://test2", "avatar2.jpg");
        
        when(streamRepository.findByIsLive(true)).thenReturn(List.of(testStream, stream2));
        when(channelGrpcClient.getChannelPreviewsByIds(List.of("channel-123", "channel-456")))
            .thenReturn(List.of(testChannel, channel2));

        // When
        List<StreamWithChannelDto> result = streamQueryService.getLiveStreams();

        // Then
        assertEquals(2, result.size());
        assertEquals(testStream, result.get(0).getStream());
        assertEquals(testChannel, result.get(0).getChannel());
        assertEquals(stream2, result.get(1).getStream());
        assertEquals(channel2, result.get(1).getChannel());
    }

    /**
     * Test: getLiveStreams when no streams are currently live
     * 
     * This test verifies the behavior when:
     * - No channels currently have active live streams
     * - The database query returns an empty list
     * 
     * Expected behavior:
     * - Returns an empty list immediately without processing
     * - Skips the gRPC call to fetch channel data (optimization)
     * - Efficiently handles the "no live streams" scenario
     * - Verifies that no unnecessary external service calls are made
     */
    @Test
    void getLiveStreams_WithNoStreams_ShouldReturnEmptyList() {
        // Given
        when(streamRepository.findByIsLive(true)).thenReturn(List.of());

        // When
        List<StreamWithChannelDto> result = streamQueryService.getLiveStreams();

        // Then
        assertTrue(result.isEmpty());
        verify(channelGrpcClient, never()).getChannelPreviewsByIds(any());
    }

    /**
     * Test: updateStream with valid data should update live stream metadata
     * 
     * This test verifies updating stream information while streaming is active:
     * - A channel has an active live stream
     * - Valid new title and description are provided
     * - The stream metadata can be updated in real-time
     * 
     * Expected behavior:
     * - Finds the currently live stream for the channel
     * - Updates the title and description fields
     * - Saves the modified stream entity to the database
     * - Returns the updated stream wrapped in Optional
     * - Only allows updating live streams (not finished ones)
     */
    @Test
    void updateStream_WithValidData_ShouldUpdateStream() {
        // Given
        String channelId = "channel-123";
        String newTitle = "Updated Title";
        String newDescription = "Updated Description";
        
        when(streamRepository.findByChannelIdAndIsLiveTrue(channelId)).thenReturn(Optional.of(testStream));
        when(streamRepository.save(any(Stream.class))).thenReturn(testStream);

        // When
        Optional<Stream> result = streamService.updateStream(channelId, newTitle, newDescription);

        // Then
        assertTrue(result.isPresent());
        assertEquals(newTitle, testStream.getTitle());
        assertEquals(newDescription, testStream.getDescription());
        verify(streamRepository).save(testStream);
    }

    /**
     * Test: updateStream when channel has no active live stream
     * 
     * This test verifies the behavior when attempting to update stream metadata:
     * - For a channel that currently has no live stream
     * - The update operation should fail gracefully
     * 
     * Expected behavior:
     * - Searches for live stream but finds none
     * - Returns empty Optional indicating no stream was updated
     * - No database save operation is performed
     * - Prevents updating metadata for non-existent or finished streams
     */
    @Test
    void updateStream_WithNoLiveStream_ShouldReturnEmpty() {
        // Given
        String channelId = "channel-123";
        when(streamRepository.findByChannelIdAndIsLiveTrue(channelId)).thenReturn(Optional.empty());

        // When
        Optional<Stream> result = streamService.updateStream(channelId, "New Title", "New Description");

        // Then
        assertFalse(result.isPresent());

        // verifies that the save() method was Never Called with any arg during test execution
        verify(streamRepository, never()).save(any());
    }

    /**
     * Test: incrementViewers with valid stream ID should increase viewer count
     * 
     * This test verifies the viewer tracking functionality when:
     * - A valid stream ID is provided
     * - The stream exists in the database
     * - A viewer joins the stream
     * 
     * Expected behavior:
     * - Finds the stream by ID
     * - Increments the current viewer count by exactly 1
     * - Saves the updated viewer count to the database
     * - Returns the updated stream entity
     * - Maintains accurate real-time viewer statistics
     */
    @Test
    void incrementViewers_WithValidStreamId_ShouldIncrementCount() {
        // Given
        String streamId = "stream-123";
        testStream.setViewers(50L);
        
        when(streamRepository.findById(streamId)).thenReturn(Optional.of(testStream));
        when(streamRepository.save(any(Stream.class))).thenReturn(testStream);

        // When
        Optional<Stream> result = streamService.incrementViewers(streamId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(51L, testStream.getViewers());
        verify(streamRepository).save(testStream);
    }

    /**
     * Test: incrementViewers with invalid stream ID should return empty
     * 
     * This test verifies error handling when:
     * - An invalid or non-existent stream ID is provided
     * - No stream exists in the database with that ID
     * 
     * Expected behavior:
     * - Attempts to find stream but gets empty result
     * - Returns empty Optional indicating operation failed
     * - No database save operation is performed
     * - Gracefully handles invalid stream ID without throwing exceptions
     * - Prevents data corruption from invalid viewer increment attempts
     */
    @Test
    void incrementViewers_WithInvalidStreamId_ShouldReturnEmpty() {
        // Given
        String streamId = "invalid-id";
        when(streamRepository.findById(streamId)).thenReturn(Optional.empty());

        // When
        Optional<Stream> result = streamService.incrementViewers(streamId);

        // Then
        assertFalse(result.isPresent());
        verify(streamRepository, never()).save(any());
    }

    /**
     * Test: getViewersCount with valid stream ID should return current count
     * 
     * This test verifies the viewer count retrieval functionality when:
     * - A valid stream ID is provided
     * - The stream exists and has viewer data
     * 
     * Expected behavior:
     * - Finds the stream by ID in the database  
     * - Extracts and returns the current viewer count
     * - Returns the count wrapped in Optional
     * - Provides read-only access to viewer statistics
     * - Used for displaying live viewer counts in UI
     */
    @Test
    void getViewersCount_WithValidStreamId_ShouldReturnCount() {
        // Given
        String streamId = "stream-123";
        testStream.setViewers(150L);
        when(streamRepository.findById(streamId)).thenReturn(Optional.of(testStream));

        // When
        Optional<Long> result = streamService.getViewersCount(streamId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(150L, result.get());
    }

    /**
     * Test: endStreamByAwsStreamId with valid AWS stream ID should end stream
     * 
     * This test verifies stream termination using AWS-specific identifiers when:
     * - A valid AWS stream ID is provided (from AWS IVS service)
     * - The stream exists and is currently live
     * - AWS IVS service notifies about stream ending
     * 
     * Expected behavior:
     * - Finds the stream using AWS stream ID (not regular stream ID)
     * - Sets isLive to false to mark stream as ended
     * - Records current timestamp as endedAt
     * - Saves the updated stream state
     * - Returns the updated stream wrapped in Optional
     * - Handles AWS IVS webhook notifications for stream lifecycle
     */
    @Test
    void endStreamByAwsStreamId_WithValidId_ShouldEndStream() {
        // Given
        String awsStreamId = "aws-stream-123";
        testStream.setIsLive(true);
        
        when(streamRepository.findByAwsStreamId(awsStreamId)).thenReturn(Optional.of(testStream));
        when(streamRepository.save(any(Stream.class))).thenReturn(testStream);

        // When
        Optional<Stream> result = streamService.endStreamByAwsStreamId(awsStreamId);

        // Then
        assertTrue(result.isPresent());
        assertFalse(result.get().getIsLive());
        assertNotNull(result.get().getEndedAt());
    }

    /**
     * Test: getFinishedStreamsWithMetadata should return paginated VOD history
     * 
     * This test verifies retrieval of a channel's past streams with pagination:
     * - A specific channel's stream history is requested
     * - Pagination parameters are provided (page 0, size 10)
     * - Complete pagination metadata is required
     * 
     * Expected behavior:
     * - Queries for finished streams (isLive = false) for the channel
     * - Orders results by startedAt descending (most recent first)
     * - Applies pagination using provided page and size
     * - Counts total finished streams for the channel
     * - Calculates total pages based on count and page size
     * - Returns PaginatedStreamResponse with streams and metadata
     * - Enables browsing channel's VOD history with proper pagination
     */
    @Test
    void getFinishedStreamsWithMetadata_ShouldReturnPaginatedResponse() {
        // Given
        String channelId = "channel-123";
        int page = 0;
        int size = 10;
        
        Stream finishedStream = new Stream();
        finishedStream.setIsLive(false);
        
        when(streamRepository.findByChannelIdAndIsLiveFalseOrderByStartedAtDesc(eq(channelId), any(Pageable.class)))
            .thenReturn(List.of(finishedStream));
        when(streamRepository.countByChannelIdAndIsLiveFalse(channelId)).thenReturn(1L);

        // When
        PaginatedStreamResponse<Stream> result = streamQueryService.getFinishedStreamsWithMetadata(channelId, page, size);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(page, result.getPage());
        assertEquals(size, result.getSize());
        assertEquals(1L, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        
        verify(streamRepository).findByChannelIdAndIsLiveFalseOrderByStartedAtDesc(eq(channelId), eq(PageRequest.of(page, size)));
    }

    /**
     * Test: getFinishedStreamsWithChannelInfo should return popular VODs with channel data
     * 
     * This test verifies retrieval of popular past streams across all channels:
     * - All finished streams from all channels are considered
     * - Results are ordered by viewer count (most popular first)
     * - Each stream is enriched with its channel information
     * - Pagination is supported for browsing popular content
     * 
     * Expected behavior:
     * - Queries all finished streams ordered by viewers descending
     * - Applies pagination to limit results per page
     * - Extracts unique channel IDs from the stream results
     * - Fetches channel preview data via gRPC for all unique channels
     * - Maps channel data to streams creating StreamWithChannelDto objects
     * - Returns paginated response with enriched stream-channel data
     * - Powers "Popular VODs" or "Trending Past Streams" features
     */
    @Test
    void getFinishedStreamsWithChannelInfo_ShouldReturnPaginatedResponseWithChannelData() {
        // Given
        int page = 0;
        int size = 10;
        
        Stream finishedStream = new Stream();
        finishedStream.setChannelId("channel-123");
        finishedStream.setIsLive(false);
        
        when(streamRepository.findByIsLiveFalseOrderByViewersDesc(any(Pageable.class)))
            .thenReturn(List.of(finishedStream));
        when(streamRepository.countByIsLiveFalse()).thenReturn(1L);
        when(channelGrpcClient.getChannelPreviewsByIds(List.of("channel-123")))
            .thenReturn(List.of(testChannel));

        // When
        PaginatedStreamResponse<StreamWithChannelDto> result = streamQueryService.getFinishedStreamsWithChannelInfo(page, size);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(finishedStream, result.getContent().get(0).getStream());
        assertEquals(testChannel, result.getContent().get(0).getChannel());
    }

    /**
     * Test: getStreamWithChannelById should return stream with enriched channel data
     * 
     * This test verifies retrieving a specific stream by ID with channel information:
     * - A stream ID is provided (could be live or finished stream)
     * - The stream exists in the database
     * - Channel information should be included in the response
     * 
     * Expected behavior:
     * - Finds the stream by its unique ID
     * - Extracts the channel ID from the stream
     * - Fetches channel preview data via gRPC
     * - Combines stream and channel data into StreamWithChannelDto
     * - Returns enriched data wrapped in Optional
     * - Used for stream detail pages that need both stream and channel info
     * - Works for both live and finished streams
     */
    @Test
    void getStreamWithChannelById_WithValidId_ShouldReturnStreamWithChannel() {
        // Given
        String streamId = "stream-123";
        when(streamRepository.findById(streamId)).thenReturn(Optional.of(testStream));
        when(channelGrpcClient.getChannelPreviewsByIds(List.of("channel-123")))
            .thenReturn(List.of(testChannel));

        // When
        Optional<StreamWithChannelDto> result = streamQueryService.getStreamWithChannelById(streamId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testStream, result.get().getStream());
        assertEquals(testChannel, result.get().getChannel());
    }

    /**
     * Test: updateStreamThumbnailByAwsStreamId should update thumbnail URL via AWS ID
     * 
     * This test verifies thumbnail updates triggered by AWS IVS service when:
     * - AWS generates a thumbnail for a live stream
     * - The thumbnail URL is provided via AWS webhook/notification
     * - The stream is identified by AWS stream ID (not regular ID)
     * 
     * Expected behavior:
     * - Finds the stream using AWS-specific stream ID
     * - Updates the thumbnailUrl field with the provided URL
     * - Saves the updated stream to the database
     * - Returns the updated stream wrapped in Optional
     * - Enables automatic thumbnail assignment from AWS IVS
     * - Supports thumbnail generation workflow integration with AWS
     */
    @Test
    void updateStreamThumbnailByAwsStreamId_WithValidId_ShouldUpdateThumbnail() {
        // Given
        String awsStreamId = "aws-stream-123";
        String thumbnailUrl = "http://example.com/thumbnail.jpg";
        
        when(streamRepository.findByAwsStreamId(awsStreamId)).thenReturn(Optional.of(testStream));
        when(streamRepository.save(any(Stream.class))).thenReturn(testStream);

        // When
        Optional<Stream> result = streamService.updateStreamThumbnailByAwsStreamId(awsStreamId, thumbnailUrl);

        // Then
        assertTrue(result.isPresent());
        assertEquals(thumbnailUrl, testStream.getThumbnailUrl());
        verify(streamRepository).save(testStream);
    }

    /**
     * Test: updateStreamVodUrlByAwsStreamId should update VOD URL for finished streams
     * 
     * This test verifies VOD (Video on Demand) URL assignment when:
     * - A live stream ends and AWS processes it into a recorded video
     * - AWS IVS generates a playable VOD URL for the recorded content
     * - The VOD URL is provided via AWS webhook/notification
     * - The stream is identified by AWS stream ID
     * 
     * Expected behavior:
     * - Finds the stream using AWS-specific stream ID
     * - Updates the vodUrl field with the provided playback URL
     * - Saves the updated stream to the database
     * - Returns the updated stream wrapped in Optional
     * - Enables viewers to watch past streams as VOD content
     * - Completes the stream lifecycle: Live → Finished → VOD Available
     * - Integrates with AWS IVS automatic recording and VOD generation
     */
    @Test
    void updateStreamVodUrlByAwsStreamId_WithValidId_ShouldUpdateVodUrl() {
        // Given
        String awsStreamId = "aws-stream-123";
        String vodUrl = "http://example.com/vod.m3u8";
        
        when(streamRepository.findByAwsStreamId(awsStreamId)).thenReturn(Optional.of(testStream));
        when(streamRepository.save(any(Stream.class))).thenReturn(testStream);

        // When
        Optional<Stream> result = streamService.updateStreamVodUrlByAwsStreamId(awsStreamId, vodUrl);

        // Then
        assertTrue(result.isPresent());
        assertEquals(vodUrl, testStream.getVodUrl());
        verify(streamRepository).save(testStream);
    }
}