package com.example.campusin.infra.postsearch;

import com.example.campusin.domain.postsearch.PostSearch;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.action.ActionListener;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.opensearch.search.sort.SortBuilders;
import org.opensearch.search.sort.SortOrder;
import org.slf4j.MDC;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PostSearchRepository {

    private final ObjectMapper objectMapper;
    private final RestHighLevelClient restHighLevelClient;
    private final PostSearchIndexRepository indexRepository;
    private final String postIndex;

    public void searchByKeyset(String keyword, String lastSortValue, int size,
                               ActionListener<List<PostSearch>> listener) {

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.multiMatchQuery(keyword, "title", "content"))
                .size(size)
                .sort(SortBuilders.fieldSort("id").order(SortOrder.DESC));

        if (lastSortValue != null && !lastSortValue.isBlank()) {
            sourceBuilder.searchAfter(new Object[]{lastSortValue});
        }

        SearchRequest request = new SearchRequest(postIndex).source(sourceBuilder).requestCache(true);


        restHighLevelClient.searchAsync(request, RequestOptions.DEFAULT, new ActionListener<>() {
            @Override
            public void onResponse(SearchResponse response) {
                List<PostSearch> posts = Arrays.stream(response.getHits().getHits())
                        .map(hit -> {
                            try {
                                return objectMapper.readValue(hit.getSourceAsString(), PostSearch.class);
                            } catch (Exception e) {
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .toList();
                listener.onResponse(posts);
            }

            @Override
            public void onFailure(Exception e) {
                listener.onFailure(e);
            }
        });
    }

    public void searchByOffset(String keyword, int page, int size,
                               ActionListener<List<PostSearch>> listener) {

        // bool query with multiple field matches
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .should(QueryBuilders.matchQuery("title_korean", keyword))
                .should(QueryBuilders.matchQuery("title_english", keyword))
                .should(QueryBuilders.matchQuery("title_ngram", keyword))
                .should(QueryBuilders.matchQuery("content_korean", keyword))
                .should(QueryBuilders.matchQuery("content_english", keyword))
                .should(QueryBuilders.matchQuery("content_ngram", keyword))
                .minimumShouldMatch(1); // 적어도 하나는 매치돼야 hit

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .query(boolQuery)
                .from((page - 1) * size)
                .size(size)
                .sort(SortBuilders.fieldSort("id").order(SortOrder.DESC));

        SearchRequest request = new SearchRequest(postIndex)
                .source(sourceBuilder);

        restHighLevelClient.searchAsync(request, RequestOptions.DEFAULT, new ActionListener<>() {
            @Override
            public void onResponse(SearchResponse response) {
                List<PostSearch> posts = Arrays.stream(response.getHits().getHits())
                        .map(hit -> {
                            try {
                                return objectMapper.readValue(hit.getSourceAsString(), PostSearch.class);
                            } catch (Exception e) {
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .toList();
                listener.onResponse(posts);
            }

            @Override
            public void onFailure(Exception e) {
                listener.onFailure(e);
            }
        });
    }



    public void bulkIndex(List<PostSearch> documents) {
        indexRepository.bulkIndex(documents);
    }
}
