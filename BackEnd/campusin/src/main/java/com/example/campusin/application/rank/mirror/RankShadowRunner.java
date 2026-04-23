package com.example.campusin.application.rank.mirror;

import com.example.campusin.domain.rank.dto.response.RankListResponse;
import com.example.campusin.mirror.MirrorProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.example.campusin.common.redis.RedisKeyFactory.studyTimeRankKey;
import static com.example.campusin.common.utils.WeekUtil.getWeekOfMonth;

@Component
public class RankShadowRunner {

    private final RedisTemplate<String, String> redisTemplate;
    private final MirrorProperties mirrorProperties;

    public RankShadowRunner(RedisTemplate<String, String> redisTemplate, MirrorProperties mirrorProperties) {
        this.redisTemplate = redisTemplate;
        this.mirrorProperties = mirrorProperties;
    }

    public Page<RankListResponse> run(LocalDate weekStart, Pageable pageable) {
        String weekKey = mirrorProperties.getShadowPrefix() + studyTimeRankKey(weekStart);
        long totalUsers = Optional.ofNullable(redisTemplate.opsForZSet().zCard(weekKey)).orElse(0L);
        long start = pageable.getOffset();
        long end = start + pageable.getPageSize() - 1;

        Set<ZSetOperations.TypedTuple<String>> rangeWithScores =
                Optional.ofNullable(redisTemplate.opsForZSet().reverseRangeWithScores(weekKey, start, end))
                        .orElseGet(Set::of);

        List<RankListResponse> responses = new ArrayList<>();
        long rank = start + 1;
        int weekOfMonth = getWeekOfMonth(weekStart);
        int month = weekStart.getMonthValue();

        for (ZSetOperations.TypedTuple<String> tuple : rangeWithScores) {
            double rawScore = Optional.ofNullable(tuple.getScore()).orElse(0.0);
            double normalizedScore = RankScoreConverter.toNormalized(rawScore);
            responses.add(RankListResponse.builder()
                    .rank(rank++)
                    .name(tuple.getValue())
                    .week(weekOfMonth)
                    .month(month)
                    .score(normalizedScore)
                    .build());
        }
        return new PageImpl<>(responses, pageable, totalUsers);
    }
}
