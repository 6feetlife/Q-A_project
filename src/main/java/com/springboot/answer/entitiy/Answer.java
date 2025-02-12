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

    @Column(nullable = false, length = 800)
    private String content;

    @Enumerated(value = EnumType.STRING)
    private AnserStatus anserStatus = AnserStatus.DONE_ANSWER;

    public enum AnserStatus {
        NOT_ANSWER,
        DONE_ANSWER
    }
}
