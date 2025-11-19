package com.example.campusin.infra.post;

import com.example.campusin.domain.board.BoardType;
import com.example.campusin.domain.post.Post;
import com.example.campusin.domain.post.PostReport;
import com.example.campusin.domain.post.ReportType;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.TestEntityFactory;
import com.example.campusin.support.DataJpaTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PostReportRepository")
class PostReportRepositoryTest extends DataJpaTestSupport {

    @Autowired
    PostReportRepository postReportRepository;

    @Test
    @DisplayName("게시글 신고를 저장하고 조회한다")
    void saveAndFind() {
        // given
        User user = TestEntityFactory.persistUser(em, "post-report");
        Post post = TestEntityFactory.persistPost(em, "report target", user, TestEntityFactory.persistBoard(em, BoardType.Free), null);

        PostReport report = postReportRepository.save(new PostReport(post, user, ReportType.SPAM));

        // when // then
        assertThat(postReportRepository.findById(report.getId())).isPresent();
    }

    @Test
    @DisplayName("게시글 신고 점수를 합산하고 게시글별 신고 기록을 삭제한다")
    void sumAndDeleteByPostId() {
        // given
        User reporter1 = TestEntityFactory.persistUser(em, "post-reporter1");
        User reporter2 = TestEntityFactory.persistUser(em, "post-reporter2");
        Post post = TestEntityFactory.persistPost(em, "report target 2", reporter1, TestEntityFactory.persistBoard(em, BoardType.Free), null);
        postReportRepository.save(new PostReport(post, reporter1, ReportType.ABUSE));
        postReportRepository.save(new PostReport(post, reporter2, ReportType.SPAM));
        em.flush();

        // when
        int sum = postReportRepository.sumReportScore(post.getId());
        postReportRepository.deleteByPostId(post.getId());
        em.flush();

        // then
        assertThat(sum).isEqualTo(ReportType.ABUSE.getScore() + ReportType.SPAM.getScore());
        assertThat(postReportRepository.findAll()).isEmpty();
    }
}
