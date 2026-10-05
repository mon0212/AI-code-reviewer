package com.codereviewer.model;

import java.util.List;

/**
 * Structured AI feedback returned to the caller.
 */
public class CodeReviewResponse {

    private String summary;
    private List<String> bugs;
    private List<String> codeQualityIssues;
    private List<String> improvements;
    private String overallRating; // e.g. "Good", "Needs Work", "Excellent"

    public CodeReviewResponse() {}

    public CodeReviewResponse(String summary, List<String> bugs,
                               List<String> codeQualityIssues,
                               List<String> improvements,
                               String overallRating) {
        this.summary = summary;
        this.bugs = bugs;
        this.codeQualityIssues = codeQualityIssues;
        this.improvements = improvements;
        this.overallRating = overallRating;
    }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public List<String> getBugs() { return bugs; }
    public void setBugs(List<String> bugs) { this.bugs = bugs; }

    public List<String> getCodeQualityIssues() { return codeQualityIssues; }
    public void setCodeQualityIssues(List<String> issues) { this.codeQualityIssues = issues; }

    public List<String> getImprovements() { return improvements; }
    public void setImprovements(List<String> improvements) { this.improvements = improvements; }

    public String getOverallRating() { return overallRating; }
    public void setOverallRating(String overallRating) { this.overallRating = overallRating; }
}
