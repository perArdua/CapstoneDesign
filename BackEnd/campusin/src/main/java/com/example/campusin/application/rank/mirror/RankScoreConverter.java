package com.example.campusin.application.rank.mirror;

public final class RankScoreConverter {

    // tie-breaker가 침범하지 않도록 충분히 큰 값
    public static final long SCALE = 1_000_000L;

    private RankScoreConverter() {
    }

    public static double toDeltaScore(long elapsedTimeDelta) {
        return (double) elapsedTimeDelta * SCALE;
    }

    public static double initialScore(long elapsedTimeDelta, long tieBreaker) {
        return toDeltaScore(elapsedTimeDelta) + tieBreakerValue(tieBreaker);
    }

    public static double tieBreakerValue(long tieBreaker) {
        long safeTieBreaker = Math.abs(tieBreaker) % SCALE;
        return (double) safeTieBreaker;
    }

    public static double fromLegacyTotal(long legacyTotal, long tieBreaker) {
        return toDeltaScore(legacyTotal) + tieBreakerValue(tieBreaker);
    }

    /**
     * composite score(elapsedTime × SCALE + tieBreaker)에서 응답·DB 저장용 정규화 값(elapsedTime)만 추출.
     * tieBreaker 성분은 floor로 제거.
     */
    public static double toNormalized(double compositeScore) {
        return Math.floor(compositeScore / SCALE);
    }
}
