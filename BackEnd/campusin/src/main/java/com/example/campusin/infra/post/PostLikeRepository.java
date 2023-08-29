package com.example.campusin.infra.post;

import com.example.campusin.domain.post.PostLike;
import com.example.campusin.domain.post.PostLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId> {

}
