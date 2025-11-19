package com.example.campusin.infra.statistics;

import com.example.campusin.domain.board.BoardType;
import com.example.campusin.domain.comment.Comment;
import com.example.campusin.domain.statistics.Statistics;
import com.example.campusin.domain.tag.TagType;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.TestEntityFactory;
import com.example.campusin.support.DataJpaTestSupport;
import com.example.campusin.support.H2Functions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("StatisticsRepository")
class StatisticsRepositoryTest extends DataJpaTestSupport {

    @Autowired
    StatisticsRepository statisticsRepository;

    @BeforeEach
    void registerDateFormatFunction() {
        String aliasTarget = H2Functions.class.getName() + ".dateFormat";
        em.getEntityManager().createNativeQuery(
                        "CREATE ALIAS IF NOT EXISTS DATE_FORMAT FOR \"" + aliasTarget + "\"")
                .executeUpdate();
    }

    @Test
    @DisplayName("통계를 저장하고 조회한다")
    void saveAndFind() {
        // given
        User user = TestEntityFactory.persistUser(em, "stats-user");
        Statistics statistics = statisticsRepository.save(
                Statistics.builder()
                        .elapsedTime(10L)
                        .numberOfQuestions(1L)
                        .numberOfAnswers(0L)
                        .numberOfAdoptedAnswers(0L)
                        .date(LocalDate.now())
                        .user(user)
                        .build());

        // when // then
        assertThat(statisticsRepository.findById(statistics.getId())).isPresent();
    }

    @Test
    @DisplayName("질문/답변/채택 건수를 조회하고 날짜로 통계를 찾는다")
    void countByDateRangeAndFindByDate() {
        // given
        User user = TestEntityFactory.persistUser(em, "stats-user2");
        var board = TestEntityFactory.persistBoard(em, BoardType.Question);
        var tag = TestEntityFactory.persistTag(em, TagType.IT);
        var post = TestEntityFactory.persistPost(em, "question", user, board, tag);
        em.persistAndFlush(Comment.builder()
                .content("answer")
                .isAnswer(true)
                .isAdopted(true)
                .post(post)
                .user(user)
                .build());
        String start = LocalDate.now().minusDays(1).toString();
        String end = LocalDate.now().plusDays(1).toString();
        Statistics statistics = statisticsRepository.save(
                Statistics.builder()
                        .elapsedTime(0L)
                        .numberOfQuestions(1L)
                        .numberOfAnswers(1L)
                        .numberOfAdoptedAnswers(1L)
                        .date(LocalDate.now())
                        .user(user)
                        .build());

        // when
        long questions = statisticsRepository.countQuestionsByUserAndModifiedAtBetween(user, start, end);
        long answers = statisticsRepository.countAnswersByUserAndModifiedAtBetweenAndIsAnswerTrue(user, start, end);
        long adopted = statisticsRepository.countAnswersByUserAndModifiedAtBetweenAndIsAdoptedTrue(user, start, end);
        Statistics found = statisticsRepository.findByUserAndDate(user, statistics.getDate().toString());

        // then
        assertThat(questions).isEqualTo(1L);
        assertThat(answers).isEqualTo(1L);
        assertThat(adopted).isEqualTo(1L);
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(statistics.getId());
    }
}
