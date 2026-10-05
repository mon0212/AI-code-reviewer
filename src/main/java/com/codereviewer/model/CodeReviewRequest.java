package com.codereviewer.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Incoming request containing the code snippet to review.
 */
public class CodeReviewRequest {

    @NotBlank(message = "Code snippet must not be empty")
    @Size(max = 8000, message = "Code snippet must be under 8000 characters")
    private String code;

    private String language = "Java";

    public CodeReviewRequest() {}

    public CodeReviewRequest(String code, String language) {
        this.code = code;
        this.language = language;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
}
