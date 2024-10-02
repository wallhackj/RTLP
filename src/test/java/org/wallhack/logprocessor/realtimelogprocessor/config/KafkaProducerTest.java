package org.wallhack.logprocessor.realtimelogprocessor.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;
import org.wallhack.logprocessor.realtimelogprocessor.service.dto.LogDTO;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@SpringBootTest(properties = "spring.kafka.producer.bootstrap-servers==${spring.embedded.kafka.brokers}")
@EmbeddedKafka(partitions = 3, count = 3, controlledShutdown = true)
class KafkaProducerTest {
    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private Environment env;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    private final String LOG_ENDPOINT = "/log";

    private WebClient webClient;
    private KafkaMessageListenerContainer<String, LogDTO> listenerContainer;

    @BeforeEach
    void setUp() {
        webClient = webClientBuilder.baseUrl("http://localhost:8080").build();
        DefaultKafkaConsumerFactory<String, Object> consumerFactory =
                new DefaultKafkaConsumerFactory<>(getConsumerProperties());

        listenerContainer = new KafkaMessageListenerContainer<>(consumerFactory,
                new ContainerProperties("realtimelog"));

        listenerContainer.setupMessageListener((MessageListener<String, LogDTO>) message -> {
            // Handle received messages here if needed
        });

        listenerContainer.start();
    }

    private Map<String, Object> getConsumerProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafka.getBrokersAsString());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, env.getProperty("spring.kafka.consumer.group-id"));
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES,
                env.getProperty("spring.kafka.consumer.properties.spring.json.trusted.packages"));
        return props;
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void testProduct_whenGivenValidProduct_successfullySendsKafkaMessage() {
        var validDTO = new LogDTO("Test message", "ERROR", Date.valueOf(LocalDate.now()));

        var response = webClient.post()
                .uri(LOG_ENDPOINT)
                .bodyValue(validDTO)
                .retrieve()
                .toEntity(String.class)
                .block();

        // Then the response should be OK
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Log processed successfully");
    }

    @Test
    public void testProduct_whenGivenInvalidProduct_returnsBadRequest() {
        var invalidDTO = new LogDTO("", "", null);

        var response = webClient.post()
                .uri(LOG_ENDPOINT)
                .bodyValue(invalidDTO)
                .retrieve()
                .toEntity(String.class)
                .block();


        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Bad DTO");
    }
}