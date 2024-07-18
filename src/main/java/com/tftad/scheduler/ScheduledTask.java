package com.tftad.scheduler;

import com.tftad.repository.CodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class ScheduledTask {

    private final CodeRepository codeRepository;

    @Scheduled(fixedRate = 3600000)
    public void deleteExpiredEntities() {
        LocalDateTime expiryTime = LocalDateTime.now().minusHours(1);
        codeRepository.deleteByCreatedAtBefore(expiryTime);
    }
}
