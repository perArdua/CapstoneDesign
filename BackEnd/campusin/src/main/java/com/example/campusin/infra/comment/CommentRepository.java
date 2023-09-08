package com.example.campusin.infra.comment;

import com.example.campusin.domain.comment.Comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by kok8454@gmail.com on 2023-03-19
 * Github : http://github.com/perArdua
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom{

    @Query("select c from Comment c where c.user.id = :userId and c.post.id = :postId ")
    Optional<Comment> findFirstByUserIdAndPostId(Long userId, Long postId);


}
