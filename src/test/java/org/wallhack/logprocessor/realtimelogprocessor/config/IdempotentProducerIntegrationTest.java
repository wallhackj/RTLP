package org.wallhack.logprocessor.realtimelogprocessor.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.wallhack.logprocessor.realtimelogprocessor.service.dto.LogDTO;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@DisabledInAotMode
@ActiveProfiles("test")
class IdempotentProducerIntegrationTest {
    @Autowired
    private KafkaTemplate<String, LogDTO> kafkaTemplate;

    @MockBean
    // to make test faster ,because i don't work with topic in this test
    private KafkaAdmin admin; // Replace @MockBean with a manual mock

    @BeforeEach
    void setup() {
        admin = Mockito.mock(KafkaAdmin.class);
    }

    @Test
    public void testProducerConfig_whenIdempotentEnabled() {
        System.out.println(admin);
        ProducerFactory<String, LogDTO> factory = kafkaTemplate.getProducerFactory();

        Map<String, Object> config = factory.getConfigurationProperties();

        assertThat(config.get(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG)).isEqualTo("true");
        assertThat(config.get(ProducerConfig.ACKS_CONFIG)).isEqualTo("all");

        if (config.containsKey(ProducerConfig.RETRIES_CONFIG)) {
            assertThat(Integer.parseInt(
                    config.get(ProducerConfig.RETRIES_CONFIG).toString()) > 0)
                    .isEqualTo("true");
        }
    }
}
