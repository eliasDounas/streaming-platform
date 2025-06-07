package com.example.morphine.resolver;

import com.example.morphine.client.ChannelServiceClient;
import com.example.morphine.dto.ChannelDTO;
import com.example.morphine.dto.ChannelPreviewDTO;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ChannelResolver {

    private final ChannelServiceClient channelClient;

    public ChannelResolver(ChannelServiceClient channelClient) {
        this.channelClient = channelClient;
    }

    @QueryMapping
    public List<ChannelPreviewDTO> getChannelPreviews(@Argument List<String> channelIds) {
        return channelClient.getChannelPreviewsByIds(channelIds);
    }

    @QueryMapping
    public ChannelDTO getChannelByUserId(@Argument String userId) {
        return channelClient.getChannelByUserId(userId);
    }
}
