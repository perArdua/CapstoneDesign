package com.example.campusin.application.rank;


import com.example.campusin.common.exception.StatisticsNotFoundException;
import com.example.campusin.common.exception.StudyGroupNotFoundException;
import com.example.campusin.common.exception.UserNotFoundException;
import com.example.campusin.common.redis.RedisLockHelper;
import com.example.campusin.domain.rank.Ranks;
import com.example.campusin.domain.rank.dto.request.RankCreateRequest;
import com.example.campusin.domain.rank.dto.response.RankIdResponse;
import com.example.campusin.domain.rank.dto.response.RankListQuestResponse;
import com.example.campusin.domain.rank.dto.response.RankListResponse;
import com.example.campusin.domain.rank.dto.response.RankListStudyGroupResponse;
import com.example.campusin.domain.statistics.Statistics;
import com.example.campusin.domain.studygroup.StudyGroup;
import com.example.campusin.domain.studygroup.StudyGroupMember;
import com.example.campusin.domain.timer.Timer;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.rank.RankRepository;
import com.example.campusin.infra.statistics.StatisticsRepository;
import com.example.campusin.infra.studygroup.StudyGroupRepository;
import com.example.campusin.infra.timer.TimerRepository;
import com.example.campusin.infra.user.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

import static com.example.campusin.common.redis.RedisKeyFactory.studyTimeRankKey;
import static com.example.campusin.common.redis.RedisKeyFactory.weeklyRankPageLockKey;
import static com.example.campusin.common.utils.WeekUtil.getWeekOfMonth;
import static com.example.campusin.common.utils.WeekUtil.getWeekStartDate;

@Service
@AllArgsConstructor
public class RankService {

    private final RankRepository rankRepository;
    private final StatisticsRepository statisticsRepository;
    private final UserRepository userRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final TimerRepository timerRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final RankCacheService rankCacheService;
    private final RedisLockHelper redisLockHelper;

    @Transactional
    public RankIdResponse createRank(Long userId, RankCreateRequest request){
        User user = findUser(userId);
        Statistics statistics = statisticsRepository.findByUserAndDate(user, request.getLocalDate().toString());
        if(statistics == null){
            throw new StatisticsNotFoundException();
        }

        //이미 해당 날짜에 대한 Rank가 존재하면 해당 Rank의 Id를 반환
        if(rankRepository.findByUserNameAndStatisticsAndStudyGroupIsNull(user.getNickname(), statistics) != null){
            return new RankIdResponse(rankRepository.findByUserNameAndStatisticsAndStudyGroupIsNull(user.getNickname(), statistics).getId());
        }

        LocalDate localDate = statistics.getDate();
        LocalDate startDate = localDate.minusDays(localDate.getDayOfWeek().getValue());
        LocalDate endDate = startDate.plusDays(6);

        List<Timer> timerList = timerRepository.findAllByUserAndModifiedAtBetween(user.getId(), startDate.toString(), endDate.toString());
        Long totalStudyTime = timerList.stream().mapToLong(Timer::getElapsedTime).sum();
        Long totalQuestion = statisticsRepository.countQuestionsByUserAndModifiedAtBetween(user, startDate.toString(), endDate.toString());

        Ranks ranks = Ranks.builder()
                .userName(user.getNickname())
                .statistics(statistics)
                .totalNumberOfQuestions(totalQuestion)
                .totalElapsedTime(totalStudyTime)
                .userName(user.getNickname())
                .weekStartDate(startDate)
                .build();

        Long rankId = rankRepository.save(ranks).getId();
        return new RankIdResponse(rankId);
    }

    // 스터디그룹내에서의 랭킹 생성
    @Transactional
    public RankIdResponse createStudyRank(Long StudyGroupId, RankCreateRequest request){
        StudyGroup studyGroup = findStudyGroup(StudyGroupId);
        Statistics statistics = statisticsRepository.findByUserAndDate(studyGroup.getUser(), request.getLocalDate().toString());
        if(statistics == null){
            throw new StatisticsNotFoundException();
        }

        // 이미 해당 날짜에 대한 Rank가 존재하면서 스터디그룹도 존재하면 해당 Rank의 Id를 반환
        if(rankRepository.findByUserNameAndStatisticsAndStudyGroupId(studyGroup.getUser().getNickname(), statistics, studyGroup.getId()) != null){
            return new RankIdResponse(rankRepository.findByUserNameAndStatisticsAndStudyGroupId(studyGroup.getUser().getNickname(), statistics, studyGroup.getId()).getId());
        }

        // 해당 날짜 기준 전주의 statistics totalElapsedTime, totalNumberOfQuestions를 구한다.
        LocalDate localDate = statistics.getDate();
        LocalDate startDate = localDate.minusDays(localDate.getDayOfWeek().getValue());
        LocalDate endDate = startDate.plusDays(6);

        Long totalStudyTime = 0L;
        Long totalQuestion = 0L;

        for(StudyGroupMember studyGroupMember : studyGroup.getMembers()){
            User studyGroupMemberUser = studyGroupMember.getUser();

            List<Timer> timerList = timerRepository.findAllByUserAndModifiedAtBetween(studyGroupMemberUser.getId(), startDate.toString(), endDate.toString());
            Long StudyTime = timerList.stream().mapToLong(Timer::getElapsedTime).sum();
            Long Question = statisticsRepository.countQuestionsByUserAndModifiedAtBetween(studyGroupMemberUser, startDate.toString(), endDate.toString());

            totalStudyTime += StudyTime;
            totalQuestion += Question;

        }

        totalStudyTime = totalStudyTime / studyGroup.getMembers().size();
        totalQuestion = totalQuestion / studyGroup.getMembers().size();

        Ranks ranks = Ranks.builder()
                .userName(studyGroup.getUser().getNickname()) // 스터디 그룹장
                .statistics(statistics)
                .totalNumberOfQuestions(totalQuestion)
                .totalElapsedTime(totalStudyTime)
                .studyGroup(studyGroup)
                .build();

        Long rankId = rankRepository.save(ranks).getId();

        return new RankIdResponse(rankId);
    }

    // 주차별 스터디 그룹 간 개인 공부시간 rank 조회
    @Transactional
    public Page<RankListStudyGroupResponse> getStudyGroupPersonalStudyTimeRank(LocalDate localDate, Pageable pageable){

        Page<Ranks> ranks = rankRepository.findByStatistics_DateAndStudyGroupIsNotNullOrderByTotalElapsedTimeDesc(localDate, pageable);

        for(Long i = 0L; i < ranks.getContent().size(); i++){
            ranks.getContent().get(i.intValue()).updateStudyRanking(i+1);
            rankRepository.save(ranks.getContent().get(i.intValue()));
        }
        return ranks.map(RankListStudyGroupResponse::new);
    }

    // 개인 질의응답 rank 조회
    @Transactional
    public Page<RankListQuestResponse> getAllQuestionRankList(LocalDate localDate, Pageable pageable) {

        Page<Ranks> ranks = rankRepository.findByStatistics_DateAndStudyGroupIsNullOrderByTotalNumberOfQuestionsDesc(localDate, pageable);

        for(Long i = 0L; i < ranks.getContent().size(); i++){
            ranks.getContent().get(i.intValue()).updateQuestionRanking(i+1);
            rankRepository.save(ranks.getContent().get(i.intValue()));
        }
        return ranks.map(RankListQuestResponse::new);
    }

    public Page<RankListResponse> getAllStudyTimeRankList(LocalDate localDate, Pageable pageable) {
        pageable = fixPageSize(pageable);

        LocalDate weekStart = getWeekStartDate(localDate);
        LocalDate currentWeekStart = getWeekStartDate(LocalDate.now());

        if (weekStart.equals(currentWeekStart)) {
            return loadCurrentWeekFromRedis(weekStart, pageable);
        } else {
            return loadPastWeekFromCacheOrDb(weekStart, pageable);
        }
    }

    private Page<RankListResponse> loadCurrentWeekFromRedis(LocalDate weekStart, Pageable pageable) {
        String weekKey = studyTimeRankKey(weekStart);
        long totalUsers = Optional.ofNullable(redisTemplate.opsForZSet().zCard(weekKey)).orElse(0L);
        long start = pageable.getOffset();
        long end = start + pageable.getPageSize() - 1;

        Set<ZSetOperations.TypedTuple<String>> rangeWithScores =
                Optional.ofNullable(redisTemplate.opsForZSet().reverseRangeWithScores(weekKey, start, end))
                        .orElseGet(Set::of);

        List<RankListResponse> rankResponseList = buildRankListFromZSet(rangeWithScores, weekStart, start);
        return new PageImpl<>(rankResponseList, pageable, totalUsers);
    }

    private Page<RankListResponse> loadPastWeekFromCacheOrDb(LocalDate weekStart, Pageable pageable) {
        String lockKey = weeklyRankPageLockKey(weekStart, pageable.getPageNumber());
        String lockValue = UUID.randomUUID().toString();

        Page<RankListResponse> cachedPage = getCachedPage(weekStart, pageable);
        if (cachedPage != null) return cachedPage;

        if (redisLockHelper.tryLock(lockKey, lockValue, Duration.ofSeconds(5))) {
            try {
                cachedPage = getCachedPage(weekStart, pageable);
                return cachedPage != null ? cachedPage : loadFromDbAndCache(weekStart, pageable);
            } finally {
                redisLockHelper.unlock(lockKey, lockValue);
            }
        } else {
            waitBriefly();
            cachedPage = getCachedPage(weekStart, pageable);
            return cachedPage != null ? cachedPage : Page.empty();
        }
    }

    @Transactional(readOnly = true)
    public Page<RankListResponse> loadFromDbAndCache(LocalDate weekStart, Pageable pageable) {
        Page<Ranks> ranksPage = rankRepository.findAllByWeekStartDateOrderByStudyRankingAsc(weekStart, pageable);
        List<RankListResponse> responses = ranksPage.getContent().stream()
                .map(RankListResponse::new)
                .toList();

        try {
            String toCache = objectMapper.writeValueAsString(responses);
            rankCacheService.cacheWeeklyRankPage(weekStart, pageable.getPageNumber(), toCache);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new PageImpl<>(responses, pageable, responses.size());
    }

    private Page<RankListResponse> getCachedPage(LocalDate weekStart, Pageable pageable) {
        String cached = rankCacheService.getCachedWeeklyRankPage(weekStart, pageable.getPageNumber());
        if (cached != null) {
            try {
                List<RankListResponse> cachedList = objectMapper.readValue(
                        cached, new TypeReference<List<RankListResponse>>() {});
                return new PageImpl<>(cachedList, pageable, cachedList.size());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private List<RankListResponse> buildRankListFromZSet(Set<ZSetOperations.TypedTuple<String>> rangeWithScores,
                                                         LocalDate weekStart, long startOffset) {
        List<RankListResponse> result = new ArrayList<>();
        long rank = startOffset + 1;
        int weekOfMonth = getWeekOfMonth(weekStart);
        int month = weekStart.getMonthValue();

        for (ZSetOperations.TypedTuple<String> tuple : rangeWithScores) {
            result.add(RankListResponse.builder()
                    .rank(rank++)
                    .name(tuple.getValue())
                    .week(weekOfMonth)
                    .month(month)
                    .build());
        }
        return result;
    }

    private Pageable fixPageSize(Pageable pageable) {
        return pageable.getPageSize() != 20
                ? PageRequest.of(pageable.getPageNumber(), 20, pageable.getSort())
                : pageable;
    }

    private void waitBriefly() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {}
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    private StudyGroup findStudyGroup(Long studyGroupId) {
        return studyGroupRepository.findById(studyGroupId)
                .orElseThrow(StudyGroupNotFoundException::new);
    }
}
