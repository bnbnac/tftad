package com.tftad.domain;

import io.jsonwebtoken.lang.Assert;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QUESTION_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID")
    private Post post;

    private String authorIntention;

    private String startTime;

    private String endTime;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    public Question(String startTime, String endTime, Post post, String authorIntention) {
        Assert.hasText(startTime, "startTime must not be null");
        Assert.hasText(endTime, "endTime must not be null");
        Assert.notNull(post, "post must not be null");

        this.startTime = startTime;
        this.endTime = endTime;
        this.authorIntention = authorIntention;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.post = post;
    }

    public String generateFilename() {
        return post.getMemberId() + "/" + post.getId() + "/" + startTime + "_" + endTime + ".mp4";
    }

    public Integer getStartTimeOnSecond() {
        return hhmmssTimeToSecond(startTime);
    }

    public Integer getEndTimeOnSecond() {
        return hhmmssTimeToSecond(endTime);
    }

    private Integer hhmmssTimeToSecond(String time) {
        int t = Integer.parseInt(time);

        int s = t % 100;
        int m = (t / 100) % 100;
        int h = t / 10000;

        return h * 3600 + m * 60 + s;
    }

    public QuestionEditor.QuestionEditorBuilder toEditorBuilder() {
        return QuestionEditor.builder()
                .authorIntention(authorIntention);
    }

    public void edit(QuestionEditor questionEditor) {
        this.authorIntention = questionEditor.getAuthorIntention();
        this.updatedAt = LocalDateTime.now();
    }
}
