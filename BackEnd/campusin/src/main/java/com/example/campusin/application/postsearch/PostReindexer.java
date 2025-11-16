package com.example.campusin.application.postsearch;


import com.example.campusin.application.postsearch.policy.BatchReindexPolicy;
import com.example.campusin.domain.post.Post;
import com.example.campusin.infra.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostReindexer {
    private final PostRepository postRepository;
    private final PostSearchIndexer postSearchIndexer;
    private final BatchReindexPolicy batchReindexPolicy;

    public void reindexAll() {
        Long lastId = 0L;
        while (true) {
            List<Post> batch = batchReindexPolicy.nextBatch(lastId);
            if (batch.isEmpty()) break;

            postSearchIndexer.bulkIndex(batch);
            lastId = batch.get(batch.size() - 1).getId();
        }
    }
}
