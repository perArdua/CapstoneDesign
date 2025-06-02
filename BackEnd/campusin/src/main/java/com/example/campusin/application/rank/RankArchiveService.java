package com.example.campusin.application.rank;

import com.example.campusin.domain.rank.Ranks;
import com.example.campusin.infra.rank.RankRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.example.campusin.common.redis.RedisKeyFactory.FAILURE_KEY;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankArchiveService {

    private final RedisTemplate<String, String> redisTemplate;
    private final RankRepository rankRepository;

    private static final int PAGE_SIZE = 500;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void archiveRanksInPages(String weekKey, LocalDate weekStartDate) {
        int page = 0;
        long globalRank = 1;

        while (true) {
            long start = page * PAGE_SIZE;
            long end = start + PAGE_SIZE - 1;

            Set<ZSetOperations.TypedTuple<String>> batch =
                    redisTemplate.opsForZSet().reverseRangeWithScores(weekKey, start, end);

            if (batch == null || batch.isEmpty()) break;

            List<Ranks> ranksToSave = new ArrayList<>();
            for (ZSetOperations.TypedTuple<String> tuple : batch) {
                String userName = tuple.getValue();
                Double score = tuple.getScore();

                if (userName == null || score == null) continue;

                ranksToSave.add(Ranks.builder()
                        .userName(userName)
                        .weekStartDate(weekStartDate)
                        .totalElapsedTime(score.longValue())
                        .studyRanking(globalRank++)
                        .build());
            }

            try {
                saveBatch(ranksToSave);
            } catch (Exception e) {
                log.error("[RankArchiveService] Failed to save batch (page={}): {}", page, e.getMessage(), e);
                saveToFailureQueue(ranksToSave);
            }

            if (batch.size() < PAGE_SIZE) break;
            page++;
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveBatch(List<Ranks> batch) {
        rankRepository.saveAll(batch);
    }

    public void saveToFailureQueue(List<Ranks> failedBatch) {
        try {
            String json = objectMapper.writeValueAsString(failedBatch);
            redisTemplate.opsForList().rightPush(FAILURE_KEY, json);
        } catch (JsonProcessingException e) {
            log.error("[RankArchiveService] Failed to serialize failed batch. Error: {}", e.getMessage(), e);
        }
    }
}
