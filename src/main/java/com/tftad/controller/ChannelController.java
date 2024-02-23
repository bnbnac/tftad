package com.tftad.controller;

import com.tftad.config.data.OAuthedMember;
import com.tftad.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    @GetMapping("/oauth/add/channel")
    public void addChannel(OAuthedMember member) {
        channelService.addChannel(member);
    }
}
