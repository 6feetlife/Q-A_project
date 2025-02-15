package com.springboot.likes.repository;

import com.springboot.likes.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikesRepository extends JpaRepository<Likes, Long> {
    List<Likes> findByQuestion_QuestionId(Long questionId);

}
