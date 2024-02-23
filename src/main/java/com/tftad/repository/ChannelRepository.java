package com.tftad.repository;

import com.tftad.domain.Channel;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ChannelRepository extends CrudRepository<Channel, Long> {
    Optional<Channel> findByYoutubeChannelId(String youtubeChannelId);
}
