package com.buddy.studybuddy.repositories;

import com.buddy.studybuddy.entities.Answer;
import com.buddy.studybuddy.entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByQuestion(Question question);
}
