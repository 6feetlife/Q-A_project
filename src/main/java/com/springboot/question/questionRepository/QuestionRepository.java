package com.springboot.question.questionRepository;

import com.springboot.question.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Optional<Question> findByTitle(String title);
    Optional<Question> findByQuestionId(long questionId);
}
