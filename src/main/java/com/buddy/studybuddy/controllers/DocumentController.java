package com.buddy.studybuddy.controllers;

import com.buddy.studybuddy.entities.Document;
import com.buddy.studybuddy.entities.User;
import com.buddy.studybuddy.repositories.DocumentRepository;
import com.buddy.studybuddy.services.DocumentService;
import org.apache.tika.exception.TikaException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final DocumentRepository documentRepository;

    public DocumentController(DocumentService documentService, DocumentRepository documentRepository) {
        this.documentService = documentService;
        this.documentRepository = documentRepository;
    }




    @PostMapping("/upload")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("totalQuestion") Integer totalQuestion) {
        System.out.println("Uploading file: " + file.getOriginalFilename());

        System.out.println("total Question: " + totalQuestion);

        if (file.isEmpty()) {
            System.out.println("File is empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please upload a valid file.");
        }

        try {
            // Extract text
            String extractedText = documentService.extractText(file);

            // Save file details to DB (Assuming fileUrl is handled elsewhere, e.g., AWS S3)
            String fileUrl = "s3://your-bucket/" + file.getOriginalFilename();

            //get userId from Spring secuirty
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            User currentUser = (User) authentication.getPrincipal();


            documentService.saveDocument(currentUser.getId(), file, extractedText, fileUrl,totalQuestion);

            return ResponseEntity.ok("File processed and saved successfully.");
        } catch (IOException | TikaException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing file: " + e.getMessage());
        }
    }
    @GetMapping("/all")
    public ResponseEntity<List<Document>> getAllExtractedFiles() {
        return ResponseEntity.ok(documentRepository.findAll());
    }
}
