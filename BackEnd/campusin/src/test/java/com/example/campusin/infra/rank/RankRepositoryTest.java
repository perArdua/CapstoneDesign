package com.example.campusin.infra.rank;

import com.example.campusin.domain.rank.Ranks;
import com.example.campusin.domain.statistics.Statistics;
import com.example.campusin.domain.studygroup.StudyGroup;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.TestEntityFactory;
import com.example.campusin.infra.statistics.StatisticsRepository;
import com.example.campusin.support.DataJpaTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RankRepository")
class RankRepositoryTest extends DataJpaTestSupport {

    @Autowired
    RankRepository rankRepository;

    @Autowired
    StatisticsRepository statisticsRepository;

    @Test
    @DisplayName("랭킹을 저장하고 조회한다")
    void saveAndFind() {
        // given
        User user = TestEntityFactory.persistUser(em, "rank-user");
        Statistics statistics = statisticsRepository.save(
                Statistics.builder()
                        .elapsedTime(10L)
                        .numberOfQuestions(1L)
                        .numberOfAnswers(0L)
                        .numberOfAdoptedAnswers(0L)
                        .date(LocalDate.now())
                        .user(user)
                        .build());
        StudyGroup group = TestEntityFactory.persistStudyGroup(em, user, "rank-group");

        Ranks ranks = rankRepository.save(
                Ranks.builder()
                        .studyRanking(1L)
                        .questionRanking(2L)
                        .userName(user.getUsername())
                        .statistics(statistics)
                        .studyGroup(group)
                        .totalElapsedTime(10L)
                        .totalNumberOfQuestions(1L)
                        .weekStartDate(LocalDate.now())
                        .build());

        // when // then
        assertThat(rankRepository.findById(ranks.getId())).isPresent();
    }

    @Test
    @DisplayName("스터디그룹이 있는 랭크를 총 공부시간 순으로 조회한다")
    void 스터디그룹_랭크를_시간순으로_조회한다() {
        // given
        LocalDate date = LocalDate.of(2024, 1, 1);
        User alice = TestEntityFactory.persistUser(em, "alice-rank");
        User bob = TestEntityFactory.persistUser(em, "bob-rank");
        StudyGroup group = TestEntityFactory.persistStudyGroup(em, alice, "group");

        Statistics aliceStats = statisticsRepository.save(
                Statistics.builder()
                        .elapsedTime(10L)
                        .numberOfQuestions(1L)
                        .numberOfAnswers(0L)
                        .numberOfAdoptedAnswers(0L)
                        .date(date)
                        .user(alice)
                        .build());
        Statistics bobStats = statisticsRepository.save(
                Statistics.builder()
                        .elapsedTime(20L)
                        .numberOfQuestions(2L)
                        .numberOfAnswers(0L)
                        .numberOfAdoptedAnswers(0L)
                        .date(date)
                        .user(bob)
                        .build());

        rankRepository.save(Ranks.builder()
                .studyRanking(2L)
                .userName("alice-rank")
                .statistics(aliceStats)
                .studyGroup(group)
                .totalElapsedTime(120L)
                .totalNumberOfQuestions(1L)
                .weekStartDate(date)
                .build());
        rankRepository.save(Ranks.builder()
                .studyRanking(1L)
                .userName("bob-rank")
                .statistics(bobStats)
                .studyGroup(group)
                .totalElapsedTime(300L)
                .totalNumberOfQuestions(2L)
                .weekStartDate(date)
                .build());

        // when
        Page<Ranks> result = rankRepository.findByStatistics_DateAndStudyGroupIsNotNullOrderByTotalElapsedTimeDesc(
                date, PageRequest.of(0, 10));

        // then
        assertThat(result.getContent())
                .extracting(Ranks::getUserName)
                .containsExactly("bob-rank", "alice-rank");
    }

    @Test
    @DisplayName("스터디그룹이 없는 랭크를 질문 수 내림차순으로 조회한다")
    void 스터디그룹_없는_랭크를_질문수로_조회한다() {
        // given
        LocalDate date = LocalDate.of(2024, 2, 1);
        User charlie = TestEntityFactory.persistUser(em, "charlie-rank");
        User dave = TestEntityFactory.persistUser(em, "dave-rank");

        Statistics cStats = statisticsRepository.save(
                Statistics.builder()
                        .elapsedTime(0L)
                        .numberOfQuestions(5L)
                        .numberOfAnswers(0L)
                        .numberOfAdoptedAnswers(0L)
                        .date(date)
                        .user(charlie)
                        .build());
        Statistics dStats = statisticsRepository.save(
                Statistics.builder()
                        .elapsedTime(0L)
                        .numberOfQuestions(3L)
                        .numberOfAnswers(0L)
                        .numberOfAdoptedAnswers(0L)
                        .date(date)
                        .user(dave)
                        .build());

        rankRepository.save(Ranks.builder()
                .studyRanking(1L)
                .userName("charlie-rank")
                .statistics(cStats)
                .totalElapsedTime(0L)
                .totalNumberOfQuestions(5L)
                .weekStartDate(date)
                .build());
        rankRepository.save(Ranks.builder()
                .studyRanking(2L)
                .userName("dave-rank")
                .statistics(dStats)
                .totalElapsedTime(0L)
                .totalNumberOfQuestions(3L)
                .weekStartDate(date)
                .build());

        // when
        Page<Ranks> result = rankRepository.findByStatistics_DateAndStudyGroupIsNullOrderByTotalNumberOfQuestionsDesc(
                date, PageRequest.of(0, 10));

        // then
        assertThat(result.getContent())
                .extracting(Ranks::getUserName)
                .containsExactly("charlie-rank", "dave-rank");
    }

    @Test
    @DisplayName("사용자명과 통계, 스터디그룹 ID로 랭크를 찾는다")
    void 사용자명과_그룹으로_랭크를_찾는다() {
        // given
        LocalDate date = LocalDate.of(2024, 3, 1);
        User user = TestEntityFactory.persistUser(em, "target-rank");
        StudyGroup group = TestEntityFactory.persistStudyGroup(em, user, "target-group");
        Statistics statistics = statisticsRepository.save(
                Statistics.builder()
                        .elapsedTime(0L)
                        .numberOfQuestions(0L)
                        .numberOfAnswers(0L)
                        .numberOfAdoptedAnswers(0L)
                        .date(date)
                        .user(user)
                        .build());

        Ranks rank = rankRepository.save(Ranks.builder()
                .studyRanking(1L)
                .userName("target-rank")
                .statistics(statistics)
                .studyGroup(group)
                .totalElapsedTime(10L)
                .totalNumberOfQuestions(1L)
                .weekStartDate(date)
                .build());

        // when
        Ranks found = rankRepository.findByUserNameAndStatisticsAndStudyGroupId(
                "target-rank", statistics, group.getId());

        // then
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(rank.getId());
        assertThat(found.getStudyGroup().getId()).isEqualTo(group.getId());
    }

    @Test
    @DisplayName("스터디그룹이 없는 랭크를 사용자명과 통계로 조회한다")
    void 그룹없음_사용자명과_통계로_조회한다() {
        // given
        LocalDate date = LocalDate.of(2024, 3, 2);
        User user = TestEntityFactory.persistUser(em, "nogroup-user");
        Statistics stats = statisticsRepository.save(
                Statistics.builder()
                        .elapsedTime(0L)
                        .numberOfQuestions(2L)
                        .numberOfAnswers(0L)
                        .numberOfAdoptedAnswers(0L)
                        .date(date)
                        .user(user)
                        .build());
        Ranks rank = rankRepository.save(Ranks.builder()
                .userName("nogroup-user")
                .statistics(stats)
                .totalElapsedTime(50L)
                .totalNumberOfQuestions(2L)
                .weekStartDate(date)
                .questionRanking(1L)
                .build());

        // when
        Ranks found = rankRepository.findByUserNameAndStatisticsAndStudyGroupIsNull("nogroup-user", stats);

        // then
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(rank.getId());
        assertThat(found.getStudyGroup()).isNull();
    }

    @Test
    @DisplayName("주차 시작일과 공부 순위 오름차순으로 랭크를 조회한다")
    void 주차별_공부순위_오름차순_조회한다() {
        // given
        LocalDate week = LocalDate.of(2024, 3, 4);
        User firstUser = TestEntityFactory.persistUser(em, "rank-first");
        User secondUser = TestEntityFactory.persistUser(em, "rank-second");
        Statistics firstStats = statisticsRepository.save(Statistics.builder()
                .elapsedTime(100L)
                .numberOfQuestions(0L)
                .numberOfAnswers(0L)
                .numberOfAdoptedAnswers(0L)
                .date(week)
                .user(firstUser)
                .build());
        Statistics secondStats = statisticsRepository.save(Statistics.builder()
                .elapsedTime(50L)
                .numberOfQuestions(0L)
                .numberOfAnswers(0L)
                .numberOfAdoptedAnswers(0L)
                .date(week)
                .user(secondUser)
                .build());
        rankRepository.save(Ranks.builder()
                .studyRanking(2L)
                .userName("rank-second")
                .statistics(secondStats)
                .totalElapsedTime(50L)
                .totalNumberOfQuestions(0L)
                .weekStartDate(week)
                .build());
        rankRepository.save(Ranks.builder()
                .studyRanking(1L)
                .userName("rank-first")
                .statistics(firstStats)
                .totalElapsedTime(100L)
                .totalNumberOfQuestions(0L)
                .weekStartDate(week)
                .build());

        // when
        Page<Ranks> result = rankRepository.findAllByWeekStartDateOrderByStudyRankingAsc(week, PageRequest.of(0, 10));

        // then
        assertThat(result.getContent()).extracting(Ranks::getUserName)
                .containsExactly("rank-first", "rank-second");
    }
}
