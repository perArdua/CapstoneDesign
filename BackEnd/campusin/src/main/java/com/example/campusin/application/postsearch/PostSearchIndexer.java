package com.example.campusin.application.postsearch;

import com.example.campusin.domain.post.Post;
import com.example.campusin.domain.postsearch.PostSearch;
import com.example.campusin.domain.postsearch.PostSearchMapper;
import lombok.RequiredArgsConstructor;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class PostSearchIndexer {

    private final OpenSearchClient openSearchClient;

    @Value("${spring.opensearch.post-index}")
    private String postIndex;

    public void index(Post post) {
        try {
            PostSearch postSearch = PostSearchMapper.fromPost(post);

            openSearchClient.index(IndexRequest.of(i -> i
                    .index(postIndex)
                    .id(postSearch.getId())
                    .document(postSearch)
            ));
        } catch (Exception e) {
            throw new RuntimeException("OpenSearch 인덱싱 실패", e);
        }
    }
}