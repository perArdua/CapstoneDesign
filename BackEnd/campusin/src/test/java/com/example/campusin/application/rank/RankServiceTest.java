package com.example.campusin.application.rank;

import com.example.campusin.application.statistics.exception.StatisticsNotFoundException;
import com.example.campusin.application.studygroup.exception.StudyGroupNotFoundException;
import com.example.campusin.application.user.exception.UserNotFoundException;
import com.example.campusin.domain.rank.Ranks;
import com.example.campusin.domain.rank.dto.request.RankCreateRequest;
import com.example.campusin.domain.rank.dto.response.RankIdResponse;
import com.example.campusin.domain.rank.dto.response.RankListResponse;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.example.campusin.common.redis.RedisKeyFactory.studyTimeRankKey;
import static com.example.campusin.common.utils.WeekUtil.getWeekOfMonth;
import static com.example.campusin.common.utils.WeekUtil.getWeekStartDate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static java.util.stream.Collectors.toMap;

@ExtendWith(MockitoExtension.class)
@DisplayName("RankService")
class RankServiceTest {

    @Mock
    RankRepository rankRepository;
    @Mock
    StatisticsRepository statisticsRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    StudyGroupRepository studyGroupRepository;
    @Mock
    TimerRepository timerRepository;
    @Mock
    RedisTemplate<String, String> redisTemplate;
    @Mock
    ObjectMapper objectMapper;
    @Mock
    RankCacheService rankCacheService;

    @InjectMocks
    RankService rankService;

    private User newUser(Long id, String nickname) {
        User user = new User();
        user.setId(id);
        user.setNickname(nickname);
        user.setLoginId("login-" + id);
        return user;
    }

    private Statistics stats(User user, LocalDate date) {
        Statistics s = Statistics.builder()
                .user(user)
                .date(date)
                .elapsedTime(0L)
                .numberOfQuestions(0L)
                .numberOfAnswers(0L)
                .numberOfAdoptedAnswers(0L)
                .build();
        return s;
    }

    @Nested
    @DisplayName("createRank 메서드는")
    class Describe_createRank {

        @Test
        @DisplayName("사용자가 없으면 UserNotFoundException을 던진다")
        void 사용자없음() {
            // given
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            // when // then
            assertThatThrownBy(() -> rankService.createRank(1L, new RankCreateRequest(LocalDate.now())))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        @DisplayName("통계가 없으면 StatisticsNotFoundException을 던진다")
        void 통계없음() {
            // given
            User user = newUser(1L, "nick");
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(statisticsRepository.findByUserAndDate(eq(user), anyString())).thenReturn(null);

            // when // then
            assertThatThrownBy(() -> rankService.createRank(1L, new RankCreateRequest(LocalDate.now())))
                    .isInstanceOf(StatisticsNotFoundException.class);
        }

        @Test
        @DisplayName("기존 Rank가 있으면 기존 ID를 반환한다")
        void 기존랭크반환() {
            // given
            User user = newUser(1L, "nick");
            LocalDate date = LocalDate.now();
            Statistics statistics = stats(user, date);
            Ranks existing = Ranks.builder().userName("nick").statistics(statistics).weekStartDate(date).build();
            ReflectionTestUtils.setField(existing, "id", 9L);

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(statisticsRepository.findByUserAndDate(eq(user), anyString())).thenReturn(statistics);
            when(rankRepository.findByUserNameAndStatisticsAndStudyGroupIsNull("nick", statistics)).thenReturn(existing);

            // when
            RankIdResponse response = rankService.createRank(1L, new RankCreateRequest(date));

            // then
            assertThat(response.getId()).isEqualTo(9L);
            verify(rankRepository, never()).save(any(Ranks.class));
        }

        @Test
        @DisplayName("통계/타이머/질문수로 새 Rank를 저장하고 ID를 반환한다")
        void 랭크저장() {
            // given
            User user = newUser(1L, "nick");
            LocalDate date = LocalDate.of(2024, 1, 10);
            LocalDate startDate = date.minusDays(date.getDayOfWeek().getValue());
            LocalDate endDate = startDate.plusDays(6);
            Statistics statistics = stats(user, date);

            Timer timer1 = Timer.builder().user(user).elapsedTime(10L).build();
            Timer timer2 = Timer.builder().user(user).elapsedTime(20L).build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(statisticsRepository.findByUserAndDate(eq(user), eq(date.toString()))).thenReturn(statistics);
            when(rankRepository.findByUserNameAndStatisticsAndStudyGroupIsNull("nick", statistics)).thenReturn(null);
            when(timerRepository.findAllByUserAndModifiedAtBetween(eq(1L), eq(startDate.toString()), eq(endDate.toString())))
                    .thenReturn(List.of(timer1, timer2));
            when(statisticsRepository.countQuestionsByUserAndModifiedAtBetween(eq(user), eq(startDate.toString()), eq(endDate.toString())))
                    .thenReturn(7L);
            when(rankRepository.save(any(Ranks.class))).thenAnswer(invocation -> {
                Ranks r = invocation.getArgument(0);
                ReflectionTestUtils.setField(r, "id", 123L);
                return r;
            });

            // when
            RankIdResponse response = rankService.createRank(1L, new RankCreateRequest(date));

            // then
            assertThat(response.getId()).isEqualTo(123L);
            ArgumentCaptor<Ranks> captor = ArgumentCaptor.forClass(Ranks.class);
            verify(rankRepository).save(captor.capture());
            Ranks saved = captor.getValue();
            assertThat(saved.getUserName()).isEqualTo("nick");
            assertThat(saved.getTotalElapsedTime()).isEqualTo(30L);
            assertThat(saved.getTotalNumberOfQuestions()).isEqualTo(7L);
            assertThat(saved.getWeekStartDate()).isEqualTo(startDate);
        }
    }

    @Nested
    @DisplayName("createStudyRank 메서드는")
    class Describe_createStudyRank {

        @Test
        @DisplayName("스터디 그룹이 없으면 StudyGroupNotFoundException을 던진다")
        void 스터디없음() {
            // given
            when(studyGroupRepository.findById(1L)).thenReturn(Optional.empty());

            // when // then
            assertThatThrownBy(() -> rankService.createStudyRank(1L, new RankCreateRequest(LocalDate.now())))
                    .isInstanceOf(StudyGroupNotFoundException.class);
        }

        @Test
        @DisplayName("스터디 리더 통계가 없으면 StatisticsNotFoundException을 던진다")
        void 스터디통계없음() {
            // given
            StudyGroup group = new StudyGroup();
            group.setId(1L);
            User leader = newUser(2L, "leader");
            group.setUser(leader);
            when(studyGroupRepository.findById(1L)).thenReturn(Optional.of(group));
            when(statisticsRepository.findByUserAndDate(eq(leader), anyString())).thenReturn(null);

            // when // then
            assertThatThrownBy(() -> rankService.createStudyRank(1L, new RankCreateRequest(LocalDate.now())))
                    .isInstanceOf(StatisticsNotFoundException.class);
        }

        @Test
        @DisplayName("기존 스터디 Rank가 있으면 저장하지 않고 기존 ID를 반환한다")
        void 기존_스터디랭크_반환() {
            // given
            StudyGroup group = new StudyGroup();
            User leader = newUser(2L, "leader");
            group.setId(11L);
            group.setUser(leader);
            Statistics statistics = stats(leader, LocalDate.now());
            Ranks existing = Ranks.builder().userName("leader").statistics(statistics).studyGroup(group).build();
            ReflectionTestUtils.setField(existing, "id", 55L);

            when(studyGroupRepository.findById(11L)).thenReturn(Optional.of(group));
            when(statisticsRepository.findByUserAndDate(eq(leader), anyString())).thenReturn(statistics);
            when(rankRepository.findByUserNameAndStatisticsAndStudyGroupId("leader", statistics, 11L))
                    .thenReturn(existing);

            // when
            RankIdResponse response = rankService.createStudyRank(11L, new RankCreateRequest(LocalDate.now()));

            // then
            assertThat(response.getId()).isEqualTo(55L);
            verify(rankRepository, never()).save(any(Ranks.class));
        }

        @Test
        @DisplayName("스터디 멤버들의 평균 공부시간과 질문수로 Rank를 저장한다")
        void 스터디랭크_저장() {
            // given
            StudyGroup group = new StudyGroup();
            User leader = newUser(1L, "leader");
            User member1 = newUser(2L, "m1");
            User member2 = newUser(3L, "m2");
            group.setId(100L);
            group.setUser(leader);
            group.setMembers(List.of(
                    StudyGroupMember.builder().user(member1).studyGroupId(group).build(),
                    StudyGroupMember.builder().user(member2).studyGroupId(group).build()
            ));

            LocalDate date = LocalDate.of(2024, 1, 3);
            LocalDate startDate = date.minusDays(date.getDayOfWeek().getValue());
            LocalDate endDate = startDate.plusDays(6);
            Statistics statistics = stats(leader, date);

            when(studyGroupRepository.findById(100L)).thenReturn(Optional.of(group));
            when(statisticsRepository.findByUserAndDate(eq(leader), anyString())).thenReturn(statistics);
            when(rankRepository.findByUserNameAndStatisticsAndStudyGroupId("leader", statistics, 100L))
                    .thenReturn(null);
            when(timerRepository.findAllByUserAndModifiedAtBetween(eq(2L), eq(startDate.toString()), eq(endDate.toString())))
                    .thenReturn(List.of(Timer.builder().user(member1).elapsedTime(10L).build()));
            when(timerRepository.findAllByUserAndModifiedAtBetween(eq(3L), eq(startDate.toString()), eq(endDate.toString())))
                    .thenReturn(List.of(Timer.builder().user(member2).elapsedTime(30L).build()));
            when(statisticsRepository.countQuestionsByUserAndModifiedAtBetween(eq(member1), eq(startDate.toString()), eq(endDate.toString())))
                    .thenReturn(2L);
            when(statisticsRepository.countQuestionsByUserAndModifiedAtBetween(eq(member2), eq(startDate.toString()), eq(endDate.toString())))
                    .thenReturn(6L);
            when(rankRepository.save(any(Ranks.class))).thenAnswer(invocation -> {
                Ranks r = invocation.getArgument(0);
                ReflectionTestUtils.setField(r, "id", 77L);
                return r;
            });

            // when
            RankIdResponse response = rankService.createStudyRank(100L, new RankCreateRequest(date));

            // then
            assertThat(response.getId()).isEqualTo(77L);
            ArgumentCaptor<Ranks> captor = ArgumentCaptor.forClass(Ranks.class);
            verify(rankRepository).save(captor.capture());
            Ranks saved = captor.getValue();
            assertThat(saved.getTotalElapsedTime()).isEqualTo(20L);
            assertThat(saved.getTotalNumberOfQuestions()).isEqualTo(4L);
            assertThat(saved.getStudyGroup()).isEqualTo(group);
        }
    }

    @Nested
    @DisplayName("getAllStudyTimeRankList 메서드는")
    class Describe_getAllStudyTimeRankList {

        @Test
        @DisplayName("현재 주차이면 Redis ZSET에서 랭킹을 조회한다")
        void 현재주차() {
            // given
            LocalDate today = LocalDate.now();
            LocalDate weekStart = getWeekStartDate(today);
            PageRequest pageable = PageRequest.of(0, 10);

            String weekKey = studyTimeRankKey(weekStart);
            ZSetOperations<String, String> zSetOps = mock(ZSetOperations.class);
            when(redisTemplate.opsForZSet()).thenReturn(zSetOps);
            when(zSetOps.zCard(weekKey)).thenReturn(2L);

            Set<ZSetOperations.TypedTuple<String>> tuples = Set.of(
                    new DefaultTypedTuple<>("alice", 60.0),
                    new DefaultTypedTuple<>("bob", 120.0)
            );
            when(zSetOps.reverseRangeWithScores(weekKey, 0, 19)).thenReturn(tuples);

            // when
            Page<RankListResponse> result = rankService.getAllStudyTimeRankList(today, pageable);

            // then
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getContent().get(0).getName()).isEqualTo("bob");
            assertThat(result.getContent().get(0).getRank()).isEqualTo(1L);
            verify(zSetOps).reverseRangeWithScores(weekKey, 0, 19);
            verify(rankCacheService, never()).getCachedWeeklyRankPage(any(), anyInt());
        }

        @Test
        @DisplayName("현재 주차에 점수가 없으면 빈 페이지를 반환한다")
        void 현재주차_데이터없음() {
            // given
            LocalDate today = LocalDate.now();
            LocalDate weekStart = getWeekStartDate(today);
            PageRequest pageable = PageRequest.of(1, 20);

            String weekKey = studyTimeRankKey(weekStart);
            ZSetOperations<String, String> zSetOps = mock(ZSetOperations.class);
            when(redisTemplate.opsForZSet()).thenReturn(zSetOps);
            when(zSetOps.zCard(weekKey)).thenReturn(null);
            when(zSetOps.reverseRangeWithScores(weekKey, 20, 39)).thenReturn(null);

            // when
            Page<RankListResponse> result = rankService.getAllStudyTimeRankList(today, pageable);

            // then
            assertThat(result.getTotalElements()).isZero();
            assertThat(result.getContent()).isEmpty();
        }

        @Test
        @DisplayName("과거 주차이고 캐시가 있으면 캐시에서 바로 반환한다")
        void 과거주차_캐시히트() throws Exception {
            // given
            LocalDate past = LocalDate.now().minusWeeks(1);
            LocalDate weekStart = getWeekStartDate(past);
            PageRequest pageable = PageRequest.of(0, 20);

            String cachedJson = "[{\"rank\":1,\"name\":\"cached\",\"week\":1,\"month\":1}]";
            when(rankCacheService.getCachedWeeklyRankPage(weekStart, pageable.getPageNumber()))
                    .thenReturn(cachedJson);
            when(objectMapper.readValue(eq(cachedJson), any(TypeReference.class)))
                    .thenReturn(List.of(RankListResponse.builder().rank(1L).name("cached").week(1).month(1).build()));

            // when
            Page<RankListResponse> result = rankService.getAllStudyTimeRankList(past, pageable);

            // then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getName()).isEqualTo("cached");
            verify(rankRepository, never()).findAllByWeekStartDateOrderByStudyRankingAsc(any(), any());
        }

        @Test
        @DisplayName("과거 주차이고 캐시가 없으면 DB에서 조회 후 캐시에 저장한다")
        void 과거주차_캐시미스() throws Exception {
            // given
            LocalDate past = LocalDate.now().minusWeeks(2);
            LocalDate weekStart = getWeekStartDate(past);
            PageRequest pageable = PageRequest.of(0, 20);

            when(rankCacheService.getCachedWeeklyRankPage(weekStart, pageable.getPageNumber()))
                    .thenReturn(null);

            Ranks ranks = Ranks.builder()
                    .userName("db-user")
                    .totalElapsedTime(100L)
                    .totalNumberOfQuestions(10L)
                    .weekStartDate(weekStart)
                    .studyRanking(1L)
                    .build();
            Page<Ranks> ranksPage = new PageImpl<>(List.of(ranks), pageable, 1);
            when(rankRepository.findAllByWeekStartDateOrderByStudyRankingAsc(weekStart, pageable))
                    .thenReturn(ranksPage);
            when(objectMapper.writeValueAsString(any())).thenReturn("[]");

            // when
            Page<RankListResponse> result = rankService.getAllStudyTimeRankList(past, pageable);

            // then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getName()).isEqualTo("db-user");
            verify(rankCacheService).cacheWeeklyRankPage(eq(weekStart), eq(pageable.getPageNumber()), anyString());
        }
    }

    @Nested
    @DisplayName("loadFromDbAndCache 메서드는")
    class Describe_loadFromDbAndCache {

        @Test
        @DisplayName("DB에서 조회한 페이지를 반환하고 캐시에 저장한다")
        void DB결과를_캐시에_저장한다() throws Exception {
            // given
            LocalDate weekStart = LocalDate.of(2024, 1, 1);
            PageRequest pageable = PageRequest.of(1, 20);

            Ranks ranks = Ranks.builder()
                    .userName("alice")
                    .weekStartDate(weekStart)
                    .studyRanking(3L)
                    .totalElapsedTime(200L)
                    .build();
            Page<Ranks> ranksPage = new PageImpl<>(List.of(ranks), pageable, 1);

            when(rankRepository.findAllByWeekStartDateOrderByStudyRankingAsc(weekStart, pageable))
                    .thenReturn(ranksPage);
            when(objectMapper.writeValueAsString(any())).thenReturn("[{\"name\":\"alice\"}]");

            // when
            Page<RankListResponse> result = rankService.loadFromDbAndCache(weekStart, pageable);

            // then
            assertThat(result.getTotalElements()).isEqualTo(21);
            assertThat(result.getContent().get(0).getName()).isEqualTo("alice");
            assertThat(result.getContent().get(0).getWeek()).isEqualTo(getWeekOfMonth(weekStart));
            assertThat(result.getContent().get(0).getMonth()).isEqualTo(1);
            verify(rankCacheService).cacheWeeklyRankPage(weekStart, pageable.getPageNumber(), "[{\"name\":\"alice\"}]");
        }

        @Test
        @DisplayName("직렬화에 실패해도 조회 결과를 반환한다")
        void 직렬화_실패시_캐시없이_반환() throws JsonProcessingException {
            // given
            LocalDate weekStart = LocalDate.of(2024, 2, 1);
            PageRequest pageable = PageRequest.of(0, 20);
            Ranks ranks = Ranks.builder()
                    .userName("bob")
                    .weekStartDate(weekStart)
                    .totalElapsedTime(50L)
                    .build();
            Page<Ranks> ranksPage = new PageImpl<>(List.of(ranks), pageable, 1);

            when(rankRepository.findAllByWeekStartDateOrderByStudyRankingAsc(weekStart, pageable))
                    .thenReturn(ranksPage);
            when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("fail") {});

            // when
            Page<RankListResponse> result = rankService.loadFromDbAndCache(weekStart, pageable);

            // then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getName()).isEqualTo("bob");
            verify(rankCacheService, never()).cacheWeeklyRankPage(any(), anyInt(), anyString());
        }
    }

    @Nested
    @DisplayName("getStudyGroupPersonalStudyTimeRank 메서드는")
    class Describe_getStudyGroupPersonalStudyTimeRank {

        @Test
        @DisplayName("조회 결과에 순위를 매기고 저장 호출을 한다")
        void 순위를_저장한다() {
            // given
            LocalDate date = LocalDate.of(2024, 2, 1);
            PageRequest pageable = PageRequest.of(0, 5);
            StudyGroup group = new StudyGroup();
            group.setStudygroupName("group");
            Ranks first = Ranks.builder()
                    .userName("a")
                    .studyGroup(group)
                    .weekStartDate(date)
                    .totalElapsedTime(200L)
                    .build();
            Ranks second = Ranks.builder()
                    .userName("b")
                    .studyGroup(group)
                    .weekStartDate(date)
                    .totalElapsedTime(100L)
                    .build();
            Page<Ranks> page = new PageImpl<>(List.of(first, second), pageable, 2);
            when(rankRepository.findByStatistics_DateAndStudyGroupIsNotNullOrderByTotalElapsedTimeDesc(date, pageable))
                    .thenReturn(page);

            // when
            Page<?> result = rankService.getStudyGroupPersonalStudyTimeRank(date, pageable);

            // then
            assertThat(result.getContent()).hasSize(2);
            ArgumentCaptor<Ranks> captor = ArgumentCaptor.forClass(Ranks.class);
            verify(rankRepository, times(2)).save(captor.capture());
            List<Ranks> saved = captor.getAllValues();
            Map<String, Long> rankings = saved.stream().collect(toMap(Ranks::getUserName, Ranks::getStudyRanking));
            assertThat(rankings.get("a")).isEqualTo(1L);
            assertThat(rankings.get("b")).isEqualTo(2L);
        }

        @Test
        @DisplayName("결과가 비어있으면 저장 호출하지 않는다")
        void 비어있으면_저장없음() {
            // given
            LocalDate date = LocalDate.of(2024, 2, 8);
            PageRequest pageable = PageRequest.of(0, 5);
            when(rankRepository.findByStatistics_DateAndStudyGroupIsNotNullOrderByTotalElapsedTimeDesc(date, pageable))
                    .thenReturn(Page.empty());

            // when
            Page<?> result = rankService.getStudyGroupPersonalStudyTimeRank(date, pageable);

            // then
            assertThat(result.getContent()).isEmpty();
            verify(rankRepository, never()).save(any(Ranks.class));
        }
    }

    @Nested
    @DisplayName("getAllQuestionRankList 메서드는")
    class Describe_getAllQuestionRankList {

        @Test
        @DisplayName("질문 횟수 순으로 순위를 업데이트하고 저장한다")
        void 질문순위를_저장한다() {
            // given
            LocalDate date = LocalDate.of(2024, 3, 1);
            PageRequest pageable = PageRequest.of(0, 3);
            Ranks first = Ranks.builder()
                    .userName("a")
                    .weekStartDate(date)
                    .totalNumberOfQuestions(10L)
                    .build();
            Ranks second = Ranks.builder()
                    .userName("b")
                    .weekStartDate(date)
                    .totalNumberOfQuestions(5L)
                    .build();
            Page<Ranks> page = new PageImpl<>(List.of(first, second), pageable, 2);
            when(rankRepository.findByStatistics_DateAndStudyGroupIsNullOrderByTotalNumberOfQuestionsDesc(date, pageable))
                    .thenReturn(page);

            // when
            Page<?> result = rankService.getAllQuestionRankList(date, pageable);

            // then
            assertThat(result.getContent()).hasSize(2);
            ArgumentCaptor<Ranks> captor = ArgumentCaptor.forClass(Ranks.class);
            verify(rankRepository, times(2)).save(captor.capture());
            List<Ranks> saved = captor.getAllValues();
            Map<String, Long> rankings = saved.stream().collect(toMap(Ranks::getUserName, Ranks::getQuestionRanking));
            assertThat(rankings.get("a")).isEqualTo(1L);
            assertThat(rankings.get("b")).isEqualTo(2L);
        }

        @Test
        @DisplayName("랭크가 없으면 저장 호출 없이 빈 페이지를 반환한다")
        void 질문랭크없음() {
            // given
            LocalDate date = LocalDate.of(2024, 3, 8);
            PageRequest pageable = PageRequest.of(0, 3);
            when(rankRepository.findByStatistics_DateAndStudyGroupIsNullOrderByTotalNumberOfQuestionsDesc(date, pageable))
                    .thenReturn(Page.empty());

            // when
            Page<?> result = rankService.getAllQuestionRankList(date, pageable);

            // then
            assertThat(result.getContent()).isEmpty();
            verify(rankRepository, never()).save(any(Ranks.class));
        }
    }
}
