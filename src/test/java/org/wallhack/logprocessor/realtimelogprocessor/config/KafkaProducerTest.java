package org.wallhack.logprocessor.realtimelogprocessor.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import org.testcontainers.utility.DockerImageName;
import org.wallhack.logprocessor.realtimelogprocessor.service.dto.LogDTO;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
class KafkaProducerTest {
    private static KafkaContainer kafka;

    @Autowired
    private KafkaTemplate<String, LogDTO> kafkaTemplate;

    @BeforeEach
    void setUp() {
        DockerImageName kafkaImage = DockerImageName.parse("bitnami/kafka:latest")
                .asCompatibleSubstituteFor("confluentinc/cp-kafka");
        kafka = new KafkaContainer(kafkaImage);
        kafka.start();
    }

    @AfterEach
    void tearDown() {
        kafka.stop();
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Test
    public void testSendLogDTO() {
        var logDTO = new LogDTO("logId123", "INFO", Date.from(Instant.now()));

        kafkaTemplate.send("test-log-topic", logDTO);

        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-group");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        consumerProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        consumerProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, LogDTO.class.getName());
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        KafkaConsumer<String, LogDTO> consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singletonList("test-log-topic"));

        ConsumerRecord<String, LogDTO> record = consumer.poll(Duration.ofSeconds(5)).iterator().next();

        assertThat(record).isNotNull();
        assertThat(record.value()).isEqualTo(logDTO);
        assertThat(record.key()).isEqualTo("logId123");

        consumer.close();
    }
}