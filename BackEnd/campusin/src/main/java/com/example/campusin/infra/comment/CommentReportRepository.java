package com.example.campusin.infra.comment;

import com.example.campusin.domain.comment.CommentReport;
import com.example.campusin.domain.comment.CommentReportId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by kok8454@gmail.com on 2023-09-09
 * Github : http://github.com/perArdua
 */
public interface CommentReportRepository extends JpaRepository<CommentReport, CommentReportId> {

    @Query("DELETE FROM CommentReport cr WHERE cr.id.commentId = :commentId")
    void deleteByCommentId(Long commentId);
}
