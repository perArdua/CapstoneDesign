package com.example.campusin.infra.comment;

import com.example.campusin.domain.comment.dto.response.CommentsOnPostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface CommentRepositoryCustom {

    Page<CommentsOnPostResponse> findByPost(Long postId, Pageable pageable);

    //Optional<CommentOneResponse> findBestCommentByPost(Long postId);
}
