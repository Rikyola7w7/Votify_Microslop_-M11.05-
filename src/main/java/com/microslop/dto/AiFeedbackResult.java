package com.microslop.dto;

import java.util.List;

/**
 * DTO representing the result of AI feedback generation for a project.
 * Contains all synthesized feedback data returned by the Gemini API.
 */
public class AiFeedbackResult {

    private String summary;
    private List<String> positivePoints;
    private List<String> negativePoints;
    private double sentimentScore; // 0.0 - 5.0
    private int positiveCount;
    private int neutralCount;
    private int negativeCount;
    private List<String> frequentWords;

    private AiFeedbackResult() {
    }

    public String getSummary() {
        return summary;
    }

    public List<String> getPositivePoints() {
        return positivePoints;
    }

    public List<String> getNegativePoints() {
        return negativePoints;
    }

    public double getSentimentScore() {
        return sentimentScore;
    }

    public int getPositiveCount() {
        return positiveCount;
    }

    public int getNeutralCount() {
        return neutralCount;
    }

    public int getNegativeCount() {
        return negativeCount;
    }

    public List<String> getFrequentWords() {
        return frequentWords;
    }

    public int getTotalComments() {
        return positiveCount + neutralCount + negativeCount;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final AiFeedbackResult result = new AiFeedbackResult();

        public Builder summary(String summary) {
            result.summary = summary;
            return this;
        }

        public Builder positivePoints(List<String> positivePoints) {
            result.positivePoints = positivePoints;
            return this;
        }

        public Builder negativePoints(List<String> negativePoints) {
            result.negativePoints = negativePoints;
            return this;
        }

        public Builder sentimentScore(double sentimentScore) {
            result.sentimentScore = sentimentScore;
            return this;
        }

        public Builder positiveCount(int positiveCount) {
            result.positiveCount = positiveCount;
            return this;
        }

        public Builder neutralCount(int neutralCount) {
            result.neutralCount = neutralCount;
            return this;
        }

        public Builder negativeCount(int negativeCount) {
            result.negativeCount = negativeCount;
            return this;
        }

        public Builder frequentWords(List<String> frequentWords) {
            result.frequentWords = frequentWords;
            return this;
        }

        public AiFeedbackResult build() {
            return result;
        }
    }
}
