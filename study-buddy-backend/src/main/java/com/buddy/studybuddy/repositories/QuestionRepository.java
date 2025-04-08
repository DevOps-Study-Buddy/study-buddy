package com.buddy.studybuddy.repositories;

import com.buddy.studybuddy.entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
//The Question repository interface
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByDocumentId(Long documentId);
}
