package com.example.campusin.application.postsearch;

import com.example.campusin.domain.post.Post;
import com.example.campusin.domain.postsearch.PostSearch;
import com.example.campusin.domain.postsearch.PostSearchMapper;
import com.example.campusin.domain.postsearch.dto.response.PostSearchResponse;
import com.example.campusin.infra.post.PostRepository;
import com.example.campusin.infra.postsearch.PostSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostSearchService {

    private final PostSearchRepository postSearchRepository;
    private final PostRepository postRepository;

    public List<PostSearchResponse> searchPostsAfter(String keyword, String lastSortValue, int size) {
        return postSearchRepository.searchByKeyset(keyword, lastSortValue, size).stream()
                .map(PostSearchResponse::from)
                .collect(Collectors.toList());
    }

    @Async
    public void reindexAllAsync() {
        System.out.println("[재색인] 비동기 재색인 시작");

        Long lastId = 0L;
        int batchSize = 9000;
        int totalIndexed = 0;
        List<Post> batch;
        Instant startAll = Instant.now();

        try {
            do {
                Instant start = Instant.now();

                batch = postRepository.findTop9000ByIdGreaterThanOrderByIdAsc(lastId);
                if (batch.isEmpty()) break;

                List<PostSearch> documents = batch.stream()
                        .map(PostSearchMapper::fromPost)
                        .collect(Collectors.toList());

                postSearchRepository.bulkIndex(documents);

                lastId = batch.get(batch.size() - 1).getId();
                totalIndexed += documents.size();

                Instant end = Instant.now();
                System.out.printf("[재색인] %s ~ %s 인덱싱 완료 (소요: %d ms, 누적: %d)%n",
                        String.valueOf(documents.get(0).getId()),
                        String.valueOf(lastId),
                        Duration.between(start, end).toMillis(),
                        totalIndexed
                );

            } while (batch.size() == batchSize);

            Instant endAll = Instant.now();
            System.out.printf("[재색인] 전체 완료 - 총 인덱싱 수: %d, 총 소요 시간: %d ms%n",
                    totalIndexed,
                    Duration.between(startAll, endAll).toMillis());
        } catch (Exception e) {
            System.err.println("[재색인] 재색인 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
