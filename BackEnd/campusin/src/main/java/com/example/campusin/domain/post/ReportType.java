package com.example.campusin.domain.post;

public enum ReportType {
    ABUSE(3),
    SPAM(2),
    ETC(1);

    private final int score;

    ReportType(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }
}

