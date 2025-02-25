package com.springboot.question.entity;

import com.springboot.answer.entitiy.Answer;
import com.springboot.audit.BaseEntity;
import com.springboot.likes.entity.Likes;
import com.springboot.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Question extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 500)
    private String content;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public void setMember(Member member) {
        this.member = member;
        if(!member.getQuestions().contains(this)) {
            member.getQuestions().add(this);
        }
    }

    @OneToOne(mappedBy = "question")
    private Answer answer;

    public void setAnswer(Answer answer) {
        this.answer = answer;
        if(answer.getQuestion() != this) {
            answer.setQuestion(this);
        }
    }

    @Column
    private int viewCount = 0;

    @Column
    private int likeCount = 0;

    @Enumerated(value = EnumType.STRING)
    private QuestionStatus questionStatus = QuestionStatus.QUESTION_REGISTERED;


    @Enumerated(value = EnumType.STRING)
    private QuestionVisibilityScope questionVisibilityScope = QuestionVisibilityScope.PUBLIC_QUESTION;

    @Column
    private String imageUrl;

    public enum QuestionVisibilityScope {
        PRIVATE_QUESTION("비공개 게시물"),
        PUBLIC_QUESTION("공개 게시물");

        private final String message;

        QuestionVisibilityScope(String message) {
            this.message = message;
        }
        public String getMessage() {
            return message;
        }
    }

    public enum QuestionStatus {
        QUESTION_REGISTERED("질문 등록"),
        QUESTION_ANSWERED("답변 완료"),
        QUESTION_DELETED("질문 삭제"),
        QUESTION_DEACTIVED("질문 비활성화");

        private final String message;

        QuestionStatus(String message) {
            this.message = message;
        }
        public String getMessage() {
            return message;
        }
    }
}
