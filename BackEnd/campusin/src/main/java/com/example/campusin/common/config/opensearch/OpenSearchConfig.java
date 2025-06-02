package com.example.campusin.common.config.opensearch;

import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensearch.client.RestClient;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.OpenSearchTransport;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class OpenSearchConfig {

    @Value("${spring.opensearch.uris}")
    private String openSearchUrl;

    @Value("${spring.opensearch.username}")
    private String username;

    @Value("${spring.opensearch.password}")
    private String password;

    @Value("${spring.opensearch.post-index}")
    private String postIndex;

    @Bean
    public OpenSearchClient openSearchClient() {
        RestClient restClient = RestClient.builder(HttpHost.create(openSearchUrl))
                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                        .setDefaultCredentialsProvider(credentialsProvider()))
                .build();

        OpenSearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        return new OpenSearchClient(transport);
    }

    private CredentialsProvider credentialsProvider() {
        CredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        return provider;
    }

    @Bean
    public String postIndex() {
        return postIndex;
    }
}