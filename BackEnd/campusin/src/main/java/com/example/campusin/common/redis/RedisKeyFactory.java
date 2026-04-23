package com.example.campusin.common.redis;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RedisKeyFactory {

    private static final String RANK_WEEKLY_PAGE_KEY = "rank:weekly:%s:page:%d";
    private static final String STUDY_TIME_ZSET_KEY = "studyTimeRank:%s";
    public static final String WEEKLY_RANK_CACHE_KEY_SET = "cache:rank:weekly:keys";
    public static final String FAILURE_KEY = "rank:archive:failures";


    // 고정된 형식으로 날짜 포맷: yyyy-MM-dd
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public static String weeklyRankPageKey(LocalDate weekStartDate, int page) {
        return String.format(RANK_WEEKLY_PAGE_KEY, formatDate(weekStartDate), page);
    }

    public static String studyTimeRankKey(LocalDate weekStartDate) {
        return String.format(STUDY_TIME_ZSET_KEY, formatDate(weekStartDate));
    }

    public static String weeklyRankPagePrefix(LocalDate weekStartDate) {
        return "rank:weekly:" + formatDate(weekStartDate);
    }

    private static String formatDate(LocalDate date) {
        return date.format(FORMATTER);  // yyyy-MM-dd
    }
}
