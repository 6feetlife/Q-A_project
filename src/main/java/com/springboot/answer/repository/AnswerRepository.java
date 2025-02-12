package com.springboot.answer.repository;

import com.springboot.answer.entitiy.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnswerRepository extends JpaRepository <Answer, Long>{
    Optional<Answer> findByAnswerId(long answerId);
    List<Answer> findByQuestion_QuestionId(Long questionId);
}
