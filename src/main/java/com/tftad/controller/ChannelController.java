package com.tftad.controller;

import com.tftad.config.data.OAuthedMember;
import com.tftad.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping("/oauth/add/channel")
    public void addChannel(OAuthedMember member) {
        channelService.addChannel(member);
    }
}
