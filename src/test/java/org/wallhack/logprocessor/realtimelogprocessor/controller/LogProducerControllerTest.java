package org.wallhack.logprocessor.realtimelogprocessor.controller;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.wallhack.logprocessor.realtimelogprocessor.service.dto.LogDTO;

import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@WebFluxTest(LogProducerController.class)
class LogProducerControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private KafkaTemplate<String, LogDTO> kafkaTemplate;

    @InjectMocks
    private LogProducerController controller;

    private final static String LOG_ENDPOINT = "/log";

    @Test
    public void testSendLog() {
        var logDTO = new LogDTO("This is a test log message", "INFO", Date.from(Instant.now()));

        webTestClient.post()
                .uri(LOG_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(logDTO)
                .exchange()
                .expectStatus().isOk();

        ArgumentCaptor<LogDTO> captor = ArgumentCaptor.forClass(LogDTO.class);
        System.out.println(controller);

        verify(kafkaTemplate).send(eq("test-log-topic"), captor.capture());
        assertThat(captor.getValue()).isEqualTo(logDTO);
    }
}