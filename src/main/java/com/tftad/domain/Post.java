package com.tftad.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POST_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    private String title;

    private String videoId;

    private Boolean published = false;

    @Lob
    private String content;

    @OneToMany(mappedBy = "post")
    private List<Question> questions = new ArrayList<>();

    @Builder
    public Post(String title, String content, String videoId, Member member) {
        this.title = title;
        this.content = content;
        this.videoId = videoId;
        if (member != null) {
            changeMember(member);
        }
    }

    public PostEditor.PostEditorBuilder toEditorBuilder() {
        return PostEditor.builder()
                .title(title)
                .content(content);
    }

    public void edit(PostEditor postEditor) {
        this.title = postEditor.getTitle();
        this.content = postEditor.getContent();
    }

    private void changeMember(Member member) {
        if (this.member != null) {
            this.member.getPosts().remove(this);
        }
        this.member = member;
        member.getPosts().add(this);
    }

    public void show() {
        this.published = true;
    }

    public void hide() {
        this.published = false;
    }

    public String generateYoutubeVideoUrl() {
        String prefix = "https://www.youtube.com/watch?v=";
        return prefix + videoId;
    }
}
