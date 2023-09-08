package com.example.campusin.infra.post;

import com.example.campusin.domain.post.PostReport;
import com.example.campusin.domain.post.PostReportId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Created by kok8454@gmail.com on 2023-09-09
 * Github : http://github.com/perArdua
 */
public interface PostReportRepository extends JpaRepository<PostReport, PostReportId> {

    @Query("DELETE FROM PostReport pr WHERE pr.id.postId = :postId")
    void deleteByPostId(@Param("postId") Long postId);
}
