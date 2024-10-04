package org.wallhack.logprocessor.realtimelogprocessor.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.wallhack.logprocessor.realtimelogprocessor.service.dto.LogDTO;

import java.time.Instant;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(LogProducerController.class)
class LogProducerControllerTest {
    @Autowired
    private MockMvc mvc;

    @Mock
    private KafkaTemplate<String, LogDTO> kafkaTemplate;

    @InjectMocks
    private LogProducerController controller;

    private final static String LOG_ENDPOINT = "/log";

    @Test
    @SneakyThrows
    public void testSendLog() {
        var logDTO = new LogDTO("This is a test log message", "INFO", Date.from(Instant.now()));

        mvc.perform(post(LOG_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(logDTO)))
                .andExpect(status().isOk());

        ArgumentCaptor<LogDTO> captor = ArgumentCaptor.forClass(LogDTO.class);

        verify(kafkaTemplate).send(eq("test-log-topic"), captor.capture());
        assertThat(captor.getValue()).isEqualTo(logDTO);
    }
}