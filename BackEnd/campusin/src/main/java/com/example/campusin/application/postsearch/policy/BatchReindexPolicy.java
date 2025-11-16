package com.example.campusin.application.postsearch.policy;

import com.example.campusin.domain.post.Post;

import java.util.List;

public interface BatchReindexPolicy {
    List<Post> nextBatch(Long lastId);
}
