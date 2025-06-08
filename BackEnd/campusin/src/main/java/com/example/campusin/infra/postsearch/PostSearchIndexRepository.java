package com.example.campusin.infra.postsearch;

import com.example.campusin.application.postsearch.policy.BulkIndexRetryPolicy;
import com.example.campusin.domain.postsearch.PostSearch;
import lombok.RequiredArgsConstructor;
import org.opensearch.client.ResponseException;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.BulkRequest;
import org.opensearch.client.opensearch.core.BulkResponse;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostSearchIndexRepository {

    private final OpenSearchClient client;
    private final String postIndex;
    private final BulkIndexRetryPolicy bulkIndexRetryPolicy;

    public void bulkIndex(List<PostSearch> documents) {
        int retryCount = 0;

        while (true) {
            try {
                BulkRequest.Builder br = new BulkRequest.Builder();
                for (PostSearch doc : documents) {
                    br.operations(op -> op.index(i -> i.index(postIndex).id(doc.getId()).document(doc)));
                }
                BulkResponse response = client.bulk(br.build());
                if (response.errors()) {
                    throw new RuntimeException("일부 문서 인덱싱 실패");
                }
                return;

            } catch (ResponseException e) {
                int status = e.getResponse().getStatusLine().getStatusCode();
                if (bulkIndexRetryPolicy.shouldRetry(retryCount, status)) {
                    Duration backoff = bulkIndexRetryPolicy.getBackoffDuration(retryCount);
                    retryCount++;
                    sleep(backoff);
                } else {
                    throw new RuntimeException("OpenSearch 인덱싱 실패", e);
                }
            } catch (IOException e) {
                throw new RuntimeException("OpenSearch bulk 인덱싱 IO 실패", e);
            }
        }
    }

    private void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("인터럽트 발생", e);
        }
    }
}
