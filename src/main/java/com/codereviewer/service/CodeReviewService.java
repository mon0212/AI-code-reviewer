package com.codereviewer.service;

import com.codereviewer.model.CodeReviewRequest;
import com.codereviewer.model.CodeReviewResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Calls the OpenAI Chat Completions API and parses structured code review feedback.
 * Uses Java's built-in HttpClient — no extra dependencies required.
 */
@Service
public class CodeReviewService {

    private static final String OPENAI_API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String MODEL = "openai/gpt-oss-20b";

    @Value("${groq.api.key}")
    private String apiKey;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Sends the code snippet to OpenAI and returns structured review feedback.
     */
    public CodeReviewResponse review(CodeReviewRequest request) {
        String prompt = buildPrompt(request.getCode(), request.getLanguage());
        String rawResponse = callOpenAI(prompt);
        return parseResponse(rawResponse);
    }

    // -------------------------------------------------------------------------
    // Prompt engineering
    // -------------------------------------------------------------------------

    private String buildPrompt(String code, String language) {
        return """
                You are an expert %s code reviewer. Analyse the following code and respond ONLY with valid JSON.
                Use exactly this structure (arrays may be empty if nothing applies):

                {
                  "summary": "<one sentence overview>",
                  "bugs": ["<bug description>", ...],
                  "codeQualityIssues": ["<issue description>", ...],
                  "improvements": ["<improvement suggestion>", ...],
                  "overallRating": "<Excellent | Good | Needs Work | Poor>"
                }

                When analysing, check for ALL of the following:
                - Syntax errors: flag any code that is not valid %s syntax (e.g. if the code appears to be a different language entirely, say so explicitly in bugs)
                - Logic bugs (wrong operators, off-by-one errors, incorrect conditions)
                - Runtime errors (null pointer risks, array out of bounds, unchecked exceptions)
                - Code quality issues (naming, readability, missing documentation)
                - Improvements (best practices, performance, testability)

                The selected language is %s. If the code is written in a different language, flag it as a syntax error bug.

                Code to review:
                ```%s
                %s
                ```
                """.formatted(language, language, language, language.toLowerCase(), code);
    }

    // -------------------------------------------------------------------------
    // HTTP call to OpenAI
    // -------------------------------------------------------------------------

    private String callOpenAI(String prompt) {
        try {
            String requestBody = buildRequestBody(prompt);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(OPENAI_API_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("OpenAI API error [" + response.statusCode() + "]: " + response.body());
            }

            JsonNode root = objectMapper.readTree(response.body());
            return root.path("choices").get(0).path("message").path("content").asText();

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to call OpenAI API: " + e.getMessage(), e);
        }
    }

    private String buildRequestBody(String prompt) throws Exception {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("model", MODEL);
        body.put("temperature", 0.3);

        ArrayNode messages = objectMapper.createArrayNode();
        ObjectNode userMessage = objectMapper.createObjectNode();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messages.add(userMessage);

        body.set("messages", messages);
        return objectMapper.writeValueAsString(body);
    }

    // -------------------------------------------------------------------------
    // Parse the JSON response from the model
    // -------------------------------------------------------------------------

    private CodeReviewResponse parseResponse(String rawJson) {
        try {
            // Strip markdown code fences if the model wraps the JSON
            String json = rawJson.trim();
            if (json.startsWith("```")) {
                json = json.replaceAll("^```[a-zA-Z]*\\n?", "").replaceAll("```$", "").trim();
            }

            JsonNode node = objectMapper.readTree(json);

            String summary        = node.path("summary").asText("No summary provided.");
            String overallRating  = node.path("overallRating").asText("Unknown");
            List<String> bugs     = toStringList(node.path("bugs"));
            List<String> quality  = toStringList(node.path("codeQualityIssues"));
            List<String> improve  = toStringList(node.path("improvements"));

            return new CodeReviewResponse(summary, bugs, quality, improve, overallRating);

        } catch (Exception e) {
            // Graceful degradation: surface raw text in summary if JSON parsing fails
            return new CodeReviewResponse(rawJson, List.of(), List.of(), List.of(), "Unknown");
        }
    }

    private List<String> toStringList(JsonNode arrayNode) {
        List<String> result = new ArrayList<>();
        if (arrayNode.isArray()) {
            arrayNode.forEach(item -> result.add(item.asText()));
        }
        return result;
    }
}
