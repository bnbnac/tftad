package com.tftad.domain;

import io.jsonwebtoken.lang.Assert;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long id;

    private String email;

    private String name;

    private String password;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    public Member(String email, String name, String password) {
        Assert.hasText(email, "email must not be null");
        Assert.hasText(name, "name must not be null");
        Assert.hasText(password, "password must not be null");

        this.email = email;
        this.name = name;
        this.password = password;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public MemberEditor.MemberEditorBuilder toEditorBuilder() {
        return MemberEditor.builder()
                .name(name)
                .password(password);
    }

    public void edit(MemberEditor memberEditor) {
        this.name = memberEditor.getName();
        this.password = memberEditor.getPassword();
        this.updatedAt = LocalDateTime.now();
    }
}
