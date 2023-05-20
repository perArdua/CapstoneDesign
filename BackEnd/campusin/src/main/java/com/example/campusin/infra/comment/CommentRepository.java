package com.example.campusin.infra.comment;

import com.example.campusin.domain.comment.Comment;
import com.example.campusin.domain.comment.response.CommentsOnPostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by kok8454@gmail.com on 2023-03-19
 * Github : http://github.com/perArdua
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<CommentsOnPostResponse> findByPost(Long postId, Pageable pageable);
}
