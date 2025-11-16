package com.example.campusin.common.config.opensearch;

import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;
import org.opensearch.client.RestHighLevelClient;
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

    @Bean(destroyMethod = "close")
    public RestHighLevelClient restHighLevelClient() throws IOReactorException {
        // I/O 스레드 수 설정
        IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
                .setIoThreadCount(Runtime.getRuntime().availableProcessors() * 2)
                .build();

        ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);

        PoolingNHttpClientConnectionManager connectionManager =
                new PoolingNHttpClientConnectionManager(ioReactor);
        connectionManager.setMaxTotal(400);
        connectionManager.setDefaultMaxPerRoute(400);

        // HTTP 클라이언트 설정
        RestClientBuilder builder = RestClient.builder(HttpHost.create(openSearchUrl))
                .setHttpClientConfigCallback(httpClientBuilder -> {
                    return httpClientBuilder
                            .setDefaultCredentialsProvider(credentialsProvider())
                            .setConnectionManager(connectionManager);
                })
                .setRequestConfigCallback(requestConfigBuilder ->
                        requestConfigBuilder
                                .setConnectTimeout(3000)
                                .setSocketTimeout(10000)
                );

        return new RestHighLevelClient(builder);
    }

    @Bean
    public OpenSearchClient openSearchClient() throws IOReactorException {
        RestClient restClient = restHighLevelClient().getLowLevelClient();
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