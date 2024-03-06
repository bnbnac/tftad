package com.tftad.domain;

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
    public Question(String startTime, String endTime, Post post) {
        this.startTime = startTime;
        this.endTime = endTime;
        if (post != null) {
            changePost(post);
        }
    }

    private void changePost(Post post) {
        if (this.post != null) {
            this.post.getQuestions().remove(this);
        }
        this.post = post;
        post.getQuestions().add(this);
    }

    public String generateQuestionUrl() {
        String prefix = "http://192.168.1.7:54321/questions/";
        return prefix + startTime + "_" + endTime;
    }
}
