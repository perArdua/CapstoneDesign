package com.example.campusin.infra.comment;

import com.example.campusin.domain.comment.CommentReport;
import com.example.campusin.domain.comment.CommentReportId;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by kok8454@gmail.com on 2023-09-09
 * Github : http://github.com/perArdua
 */
public interface CommentReportRepository extends JpaRepository<CommentReport, CommentReportId> {
}
