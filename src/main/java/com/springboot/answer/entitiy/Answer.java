package com.springboot.answer.entitiy;

import com.springboot.audit.BaseEntity;
import com.springboot.question.entity.Question;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Answer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long answerId;

    @OneToOne
    @JoinColumn(name = "question_id")
    private Question question;

    public void setQuestion(Question question) {
        this.question = question;
        if(question.getAnswer() != this) {
            question.setAnswer(this);
        }
    }

    @Column(nullable = false, length = 800)
    private String content;

    @Enumerated(value = EnumType.STRING)
    private AnswerStatus answerStatus = AnswerStatus.DONE_ANSWER;

    public enum AnswerStatus {
        NOT_ANSWER,
        DONE_ANSWER
    }
}
