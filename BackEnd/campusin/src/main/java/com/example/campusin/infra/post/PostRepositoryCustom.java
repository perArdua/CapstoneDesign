package com.example.campusin.infra.post;

import com.example.campusin.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {
    Page<Post> searchPosts(String keyword, Pageable pageable);
}