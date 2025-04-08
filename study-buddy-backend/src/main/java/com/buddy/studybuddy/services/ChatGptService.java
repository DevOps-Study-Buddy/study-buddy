package com.buddy.studybuddy.services;

import com.buddy.studybuddy.entities.Question;
import com.buddy.studybuddy.entities.Answer;
import com.buddy.studybuddy.entities.Document;
import com.buddy.studybuddy.repositories.QuestionRepository;
import com.buddy.studybuddy.repositories.AnswerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
//The ChatGPT service java file
@Service
public class ChatGptService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.api.model}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    public ChatGptService(QuestionRepository questionRepository, AnswerRepository answerRepository) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
    }

    public List<Question> generateQuiz(Document document, Integer totalQuestions) {
        String prompt = "Generate " + totalQuestions + " multiple-choice quiz questions with answers from the following text:\n"
                + document.getExtractedText()
                + "\n\n"
                + "Format response strictly in JSON format with a 'questions' array like this:\n"
                + "{ \"questions\": [ {\"question\": \"What is ...?\", \"options\": [\"A\", \"B\", \"C\", \"D\"], \"answer\": \"A\"} ] }";

        // Set HTTP Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        // Define JSON Request Body
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "response_format", Map.of(
                        "type", "json_schema",
                        "json_schema", Map.of(
                                "name", "quiz_schema",
                                "schema", Map.of(
                                        "type", "object",
                                        "properties", Map.of(
                                                "questions", Map.of(
                                                        "type", "array",
                                                        "items", Map.of(
                                                                "type", "object",
                                                                "properties", Map.of(
                                                                        "question", Map.of("type", "string"),
                                                                        "options", Map.of("type", "array", "items", Map.of("type", "string")),
                                                                        "answer", Map.of("type", "string")
                                                                ),
                                                                "required", List.of("question", "options", "answer")
                                                        )
                                                )
                                        ),
                                        "required", List.of("questions")
                                )
                        )
                )
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, request, String.class);

        // Debugging: Log API Response
        System.out.println("ChatGPT Response: " + response.getBody());

        return saveQuizToDatabase(document, response.getBody());
    }


    private List<Question> saveQuizToDatabase(Document document, String quizJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(quizJson);

            JsonNode choices = rootNode.path("choices");
            if (!choices.isArray() || choices.isEmpty()) {
                throw new RuntimeException("Invalid ChatGPT response: missing 'choices'");
            }

            JsonNode messageNode = choices.get(0).path("message");
            JsonNode contentNode = messageNode.path("content");

            // Convert "content" from a string to JSON
            JsonNode contentJson = objectMapper.readTree(contentNode.asText());
            JsonNode quizArray = contentJson.path("questions");

            if (!quizArray.isArray() || quizArray.isEmpty()) {
                throw new RuntimeException("Invalid ChatGPT response: 'questions' array is empty");
            }

            List<Question> savedQuestions = new ArrayList<>();

            for (JsonNode node : quizArray) {
                String questionText = node.get("question").asText();
                JsonNode options = node.get("options");
                String correctAnswer = node.get("answer").asText().trim(); // Ensure no extra spaces

                System.out.println("Saving Question: " + questionText);
                System.out.println("Correct Answer: " + correctAnswer);

                Question question = new Question();
                question.setQuestionText(questionText);
                question.setDocument(document);
                question = questionRepository.save(question);

                for (JsonNode option : options) {
                    String optionText = option.asText().trim(); // Trim spaces
                    boolean isCorrect = optionText.equalsIgnoreCase(correctAnswer); // Case-insensitive check

                    System.out.println("  → Option: " + optionText + " | isCorrect: " + isCorrect);

                    Answer answer = new Answer();
                    answer.setAnswerText(optionText);
                    answer.setCorrect(isCorrect);
                    answer.setQuestion(question);
                    answerRepository.save(answer);
                }

                savedQuestions.add(question);
            }

            return savedQuestions;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }



}
