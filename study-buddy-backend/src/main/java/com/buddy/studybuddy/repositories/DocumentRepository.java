package com.buddy.studybuddy.repositories;

import com.buddy.studybuddy.entities.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findDocumentById(Long id);


}

