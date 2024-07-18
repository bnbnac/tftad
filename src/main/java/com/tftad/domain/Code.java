package com.tftad.domain;

import io.jsonwebtoken.lang.Assert;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Code {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CODE_ID")
    private Long id;

    private String code;

    private String email;

    private boolean authed = false;

    private LocalDateTime createdAt;

    private LocalDateTime expiryDate;


    @Builder
    public Code(String code, String email, Long durationMinutes) {
        Assert.hasText(code, "code must not be null");
        Assert.hasText(email, "email must not be null");
        Assert.notNull(durationMinutes, "durationMinutes must not be null");

        LocalDateTime cur = LocalDateTime.now();

        this.code = code;
        this.email = email;
        this.createdAt = cur;
        this.expiryDate = cur.plus(durationMinutes, ChronoUnit.MINUTES);
    }

    public void auth() {
        this.authed = true;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
}
