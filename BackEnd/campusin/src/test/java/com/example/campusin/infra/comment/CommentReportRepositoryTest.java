package com.example.campusin.infra.comment;

import com.example.campusin.domain.board.BoardType;
import com.example.campusin.domain.comment.Comment;
import com.example.campusin.domain.comment.CommentReport;
import com.example.campusin.domain.comment.CommentReportId;
import com.example.campusin.domain.post.Post;
import com.example.campusin.domain.user.User;
import com.example.campusin.infra.TestEntityFactory;
import com.example.campusin.support.DataJpaTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CommentReportRepository")
class CommentReportRepositoryTest extends DataJpaTestSupport {

    @Autowired
    CommentReportRepository commentReportRepository;

    @Test
    @DisplayName("댓글 신고를 저장하고 ID로 조회한다")
    void saveAndFind() {
        // given
        User author = TestEntityFactory.persistUser(em, "comment-report-author");
        User reporter = TestEntityFactory.persistUser(em, "reporter");
        Post post = TestEntityFactory.persistPost(em, "post", author, TestEntityFactory.persistBoard(em, BoardType.Free), null);
        Comment comment = TestEntityFactory.persistComment(em, post, author, null);

        CommentReport report = commentReportRepository.save(new CommentReport(reporter, comment));

        // when // then
        assertThat(commentReportRepository.findById(new CommentReportId(reporter.getId(), comment.getId()))).isPresent();
        assertThat(report.getId()).isEqualTo(new CommentReportId(reporter.getId(), comment.getId()));
    }

    @Test
    @DisplayName("댓글 ID로 신고 기록을 삭제한다")
    void deleteByCommentId() {
        // given
        User reporter = TestEntityFactory.persistUser(em, "reporter-delete");
        Post post = TestEntityFactory.persistPost(em, "post", reporter, TestEntityFactory.persistBoard(em, BoardType.Free), null);
        Comment comment = TestEntityFactory.persistComment(em, post, reporter, null);
        commentReportRepository.save(new CommentReport(reporter, comment));
        em.flush();

        // when
        commentReportRepository.deleteByCommentId(comment.getId());
        em.flush();
        em.clear();

        // then
        assertThat(commentReportRepository.findById(new CommentReportId(reporter.getId(), comment.getId()))).isNotPresent();
    }
}
