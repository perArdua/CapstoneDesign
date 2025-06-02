package com.example.campusin.infra.postsearch;

import com.example.campusin.domain.postsearch.PostSearch;
import lombok.RequiredArgsConstructor;
import org.opensearch.client.ResponseException;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.SortOrder;
import org.opensearch.client.opensearch.core.BulkRequest;
import org.opensearch.client.opensearch.core.BulkResponse;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PostSearchRepository {

    private final OpenSearchClient openSearchClient;
    private final String postIndex;

    public List<PostSearch> searchByKeyset(String keyword, String lastSortValue, int size) {
        try {
            SearchRequest.Builder builder = new SearchRequest.Builder()
                    .index(postIndex)
                    .size(size)
                    .sort(s -> s.field(f -> f.field("id.keyword").order(SortOrder.Desc)))
                    .query(q -> q
                            .multiMatch(m -> m
                                    .fields("title", "content")
                                    .query(keyword)));

            if (lastSortValue != null && !lastSortValue.isBlank()) {
                builder.searchAfter(Collections.singletonList(lastSortValue));
            }

            SearchResponse<PostSearch> response = openSearchClient.search(builder.build(), PostSearch.class);

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            throw new RuntimeException("OpenSearch search_after 검색 실패", e);
        }
    }

    public void bulkIndex(List<PostSearch> documents) {
        int maxRetries = 5;
        int retryCount = 0;
        long backoffMillis = 1000;

        while (retryCount <= maxRetries) {
            BulkRequest.Builder br = new BulkRequest.Builder();

            for (PostSearch document : documents) {
                br.operations(op -> op
                        .index(idx -> idx
                                .index(postIndex)
                                .id(document.getId())
                                .document(document)
                        )
                );
            }

            try {
                BulkResponse response = openSearchClient.bulk(br.build());
                if (response.errors()) {
                    throw new RuntimeException("OpenSearch bulk 인덱싱 실패");
                }
                return;
            } catch (ResponseException e) {
                if (e.getResponse().getStatusLine().getStatusCode() == 429) {
                    retryCount++;
                    try {
                        Thread.sleep(backoffMillis);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("재색인 중 인터럽트 발생", ie);
                    }
                    backoffMillis *= 2;
                } else {
                    throw new RuntimeException("OpenSearch bulk 인덱싱 실패", e);
                }
            } catch (IOException e) {
                throw new RuntimeException("OpenSearch bulk 인덱싱 IO 실패", e);
            }
        }

        throw new RuntimeException("OpenSearch bulk 인덱싱 실패 - 최대 재시도 초과");
    }
}