package com.example.campusin.application.postsearch;

import com.example.campusin.domain.post.Post;
import com.example.campusin.domain.postsearch.PostSearch;
import com.example.campusin.domain.postsearch.PostSearchMapper;
import com.example.campusin.infra.postsearch.PostSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class PostSearchIndexer {
    private final PostSearchRepository postSearchRepository;

    public void index(Post post) {
        PostSearch document = PostSearchMapper.fromPost(post);
        postSearchRepository.bulkIndex(List.of(document));
    }

    public void bulkIndex(List<Post> posts) {
        List<PostSearch> documents = posts.stream()
                .map(PostSearchMapper::fromPost)
                .toList();
        postSearchRepository.bulkIndex(documents);
    }
}