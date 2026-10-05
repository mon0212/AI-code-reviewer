package com.codereviewer.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.codereviewer.model.CodeReviewResponse;
import com.codereviewer.service.CodeReviewService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CodeReviewController.class)
class CodeReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CodeReviewService codeReviewService;

    @Test
    void healthCheck_returns200() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("AI Code Reviewer is running!"));
    }

    @Test
    void review_validRequest_returnsReviewResponse() throws Exception {
        CodeReviewResponse mockResponse = new CodeReviewResponse(
                "Simple method with no issues.",
                List.of(),
                List.of("Missing Javadoc"),
                List.of("Consider using StringBuilder for string concatenation"),
                "Good"
        );

        when(codeReviewService.review(any())).thenReturn(mockResponse);

        String requestJson = """
                {
                  "code": "public String greet(String name) { return \\"Hello, \\" + name; }",
                  "language": "Java"
                }
                """;

        mockMvc.perform(post("/api/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.overallRating").value("Good"))
                .andExpect(jsonPath("$.summary").value("Simple method with no issues."))
                .andExpect(jsonPath("$.codeQualityIssues[0]").value("Missing Javadoc"));
    }

    @Test
    void review_emptyCode_returns400() throws Exception {
        String requestJson = """
                {
                  "code": "",
                  "language": "Java"
                }
                """;

        mockMvc.perform(post("/api/review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }
}
