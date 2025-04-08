package com.buddy.studybuddy.services;

import com.buddy.studybuddy.entities.Document;
import com.buddy.studybuddy.repositories.DocumentRepository;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
//The Document service java file
@Service
public class DocumentService {

    private final Tika tika;
    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.tika = new Tika();
        this.documentRepository = documentRepository;
    }

    public String extractText(MultipartFile file) throws IOException, TikaException {
        return tika.parseToString(file.getInputStream());
    }

    public Document saveDocument(int userId, MultipartFile file, String extractedText, String fileUrl, int totalQuestion) {
        Document document = new Document();
        document.setUserId(userId);
        document.setFilename(file.getOriginalFilename());
        document.setFileUrl(fileUrl);
        document.setExtractedText(extractedText);
        document.setFileType(getFileType(file));
        document.setTotalQuestion(totalQuestion);

        return documentRepository.save(document);
    }

    private String getFileType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null) return "TEXT";
        if (contentType.contains("pdf")) return "PDF";
        if (contentType.contains("word")) return "DOCX";
        return "TEXT";
    }
}
