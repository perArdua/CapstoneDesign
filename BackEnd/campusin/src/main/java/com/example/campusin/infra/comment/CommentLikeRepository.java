package com.example.campusin.infra.comment;

import com.example.campusin.domain.comment.CommentLike;
import com.example.campusin.domain.comment.CommentLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, CommentLikeId>{
}
