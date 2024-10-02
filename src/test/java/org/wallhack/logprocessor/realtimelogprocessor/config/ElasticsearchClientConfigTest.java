package org.wallhack.logprocessor.realtimelogprocessor.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest
class ElasticsearchClientConfigTest {
    private static ElasticsearchContainer elasticsearchContainer;

    @BeforeAll
    static void setUp() {
        elasticsearchContainer = new ElasticsearchContainer(DockerImageName.parse("elasticsearch:8.15.2"));
        elasticsearchContainer.start();
    }

    @AfterAll
    static void tearDown() {
        if (elasticsearchContainer != null) {
            elasticsearchContainer.stop();
        }
    }

    @Test
    void testElasticsearchClientConfig() {
        var restClient = RestClient.builder(
                new HttpHost(elasticsearchContainer.getHost(), elasticsearchContainer.getFirstMappedPort())
        ).build();
        Assertions.assertNotNull(restClient);

        var elasticTransport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        Assertions.assertNotNull(elasticTransport);

        var elasticClient = new ElasticsearchClient(elasticTransport);
        Assertions.assertNotNull(elasticClient);
    }

}