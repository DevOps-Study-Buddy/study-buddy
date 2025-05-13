package com.buddy.studybuddy.controllers;

import com.buddy.studybuddy.entities.Answer;
import com.buddy.studybuddy.entities.Document;
import com.buddy.studybuddy.entities.Question;
import com.buddy.studybuddy.entities.User;
import com.buddy.studybuddy.repositories.AnswerRepository;
import com.buddy.studybuddy.repositories.DocumentRepository;
import com.buddy.studybuddy.repositories.UserRepository;
import com.buddy.studybuddy.services.ChatGptService;
import com.buddy.studybuddy.services.DocumentService;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final DocumentRepository documentRepository;
    private final ChatGptService chatGptService;
    private final AnswerRepository answerRepository;
    @Autowired
    UserRepository userRepository;
    private int UserId;

    public DocumentController(DocumentService documentService, DocumentRepository documentRepository, ChatGptService chatGptService, AnswerRepository answerRepository) {
        this.documentService = documentService;
        this.documentRepository = documentRepository;
        this.chatGptService = chatGptService;
        this.answerRepository = answerRepository;
    }




    @PostMapping("/upload")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("totalQuestion") Integer totalQuestion) {

        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Please upload a valid file."));
        }

        try {
            // Extract text from file
            String extractedText = documentService.extractText(file);
            String fileUrl = "s3://your-bucket/" + file.getOriginalFilename();

            // Get authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            Object principal = authentication.getPrincipal();

            if (principal instanceof User) {
                // If the user is authenticated via JWT and is a custom User entity
                User currentUser = (User) authentication.getPrincipal();
                UserId=currentUser.getId();
            } else if (principal instanceof DefaultOAuth2User) {

            //
                        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

            // Get the email from the OAuth2 attributes
                        String email = oAuth2User.getAttribute("email");

            // Use the email to find your app's User entity

            User currentUser = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            UserId=currentUser.getId();

            }
            // Save extracted document
            Document savedDocument = documentService.saveDocument(UserId, file, extractedText, fileUrl, totalQuestion);

            // Generate and save quiz
            List<Question> generatedQuestions = chatGptService.generateQuiz(savedDocument, totalQuestion);

            // Construct response JSON with quiz data
            List<Map<String, Object>> quizResponse = new ArrayList<>();
            for (Question question : generatedQuestions) {
                Map<String, Object> questionData = new HashMap<>();
                questionData.put("question", question.getQuestionText());

                // Fetch answers for this question
                List<Answer> answers = answerRepository.findByQuestion(question);
                List<Map<String, Object>> optionsList = new ArrayList<>();
                for (Answer answer : answers) {
                    Map<String, Object> optionData = new HashMap<>();
                    optionData.put("option", answer.getAnswerText());
                    optionData.put("isCorrect", answer.isCorrect());
                    optionsList.add(optionData);
                }

                questionData.put("options", optionsList);
                quizResponse.add(questionData);
            }

            return ResponseEntity.ok(Map.of(
                    "documentId", savedDocument.getId(),
//                    "extractedText", extractedText,
                    "quiz", quizResponse
            ));
        } catch (IOException | TikaException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error processing file: " + e.getMessage()));
        }
    }


    @GetMapping("/all")
    public ResponseEntity<List<Document>> getAllExtractedFiles() {
        return ResponseEntity.ok(documentRepository.findAll());
    }
}
