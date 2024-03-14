package com.tftad.domain;

import io.jsonwebtoken.lang.Assert;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    private String authorComment;

    private String startTime;

    private String endTime;

    @Builder
    public Question(String startTime, String endTime, Post post, String authorComment) {
        Assert.hasText(startTime, "startTime must not be null");
        Assert.hasText(endTime, "endTime must not be null");
        Assert.notNull(post, "post must not be null");

        this.startTime = startTime;
        this.endTime = endTime;
        this.authorComment = authorComment;
        changePost(post);
    }

    private void changePost(Post post) {
        if (this.post != null) {
            this.post.getQuestions().remove(this);
        }
        this.post = post;
        post.getQuestions().add(this);
    }

    public String generateFilename() {
        return startTime + "_" + endTime + ".mp4";
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
}
