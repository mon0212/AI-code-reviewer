package com.codereviewer.controller;

import com.codereviewer.model.CodeReviewRequest;
import com.codereviewer.model.CodeReviewResponse;
import com.codereviewer.service.CodeReviewService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller exposing the AI code review endpoint.
 *
 * POST /api/review
 *   Body: { "code": "...", "language": "Java" }
 *   Returns: structured review feedback from GPT
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CodeReviewController {

    private final CodeReviewService codeReviewService;

    public CodeReviewController(CodeReviewService codeReviewService) {
        this.codeReviewService = codeReviewService;
    }

    /**
     * Submit a code snippet for AI review.
     */
    @PostMapping("/review")
    public ResponseEntity<?> review(@Valid @RequestBody CodeReviewRequest request) {
        try {
            CodeReviewResponse response = codeReviewService.review(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Health check — useful for demos and CI.
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("AI Code Reviewer is running!");
    }
}
