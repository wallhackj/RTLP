package org.wallhack.logprocessor.realtimelogprocessor.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.wallhack.logprocessor.realtimelogprocessor.service.dto.LogDTO;
import org.wallhack.logprocessor.realtimelogprocessor.service.handler.LevelHandler;
import org.wallhack.logprocessor.realtimelogprocessor.service.handler.LogHandlerFactory;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.ExecutorService;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@DisabledInAotMode
@EmbeddedKafka
class LogProcessingServiceTest {
    @Autowired
    private LogProcessingService logProcessingService;

    @MockBean
    private LogHandlerFactory logHandlerFactory;

    private ExecutorService executor;

    @BeforeEach
    void setUp() throws Exception {
        executor = mock(ExecutorService.class);

        Field executorField = LogProcessingService.class.getDeclaredField("executor");
        executorField.setAccessible(true);
        executorField.set(logProcessingService, executor);
    }

    @Test
    void testConsume_whenLogCreatedAndReceived_ListenerEvent() {
        var logDTO = new LogDTO("This is a test log message", "INFO", Date.from(Instant.now()));

        LevelHandler handler = mock(LevelHandler.class);
        when(logHandlerFactory.getHandler(anyString())).thenReturn(handler);

        logProcessingService.consume(logDTO);

        ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        verify(executor).submit(captor.capture());

        captor.getValue().run();

        verify(handler).handle(any(LogDTO.class));
    }
}