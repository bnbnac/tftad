package com.tftad.domain;

import lombok.Getter;

@Getter
public class QuestionEditor {
    private final String authorIntention;

    private QuestionEditor(String authorIntention) {
        this.authorIntention = authorIntention;
    }

    public static QuestionEditorBuilder builder() {
        return new QuestionEditorBuilder();
    }

    public static class QuestionEditorBuilder {
        private String authorIntention;

        QuestionEditorBuilder() {}

        public QuestionEditorBuilder authorIntention(String authorIntention) {
            if (authorIntention != null) {
                this.authorIntention = authorIntention;
            }
            return this;
        }

        public QuestionEditor build() {
            return new QuestionEditor(authorIntention);
        }
    }
}
