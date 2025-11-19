package com.example.campusin.application.postsearch.policy;

import com.example.campusin.domain.post.Post;
import com.example.campusin.infra.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DefaultBatchReindexPolicy implements BatchReindexPolicy {
    private static final int BATCH_SIZE = 9000;
    private final PostRepository postRepository;

    @Override
    public List<Post> nextBatch(Long lastId) {
        return postRepository.findTop9000ByIdGreaterThanOrderByIdAsc(lastId);
    }
}
