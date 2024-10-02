package org.wallhack.logprocessor.realtimelogprocessor.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.wallhack.logprocessor.realtimelogprocessor.service.dto.LogDTO;
import org.wallhack.logprocessor.realtimelogprocessor.service.handler.LogHandlerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Service
public class LogProcessingService {
    private final LogHandlerFactory logHandlerFactory;
    private ExecutorService executor;

    public LogProcessingService(LogHandlerFactory logHandlerFactory) {
        this.logHandlerFactory = logHandlerFactory;
    }

    @PostConstruct
    private void init() {
        executor = Executors.newFixedThreadPool(5);
    }

    @KafkaListener(topics = "realtimelog", groupId = "log-events"
            , containerFactory = "kafkaListenerContainerFactory")
    public void consume(LogDTO message) {
        processMessage(message);
    }

    private void processMessage(LogDTO message) {
        executor.submit(() -> logHandlerFactory.getHandler(message.level()).handle(message));
    }

    @PreDestroy
    private void destroy() {
        executor.shutdown();
    }

}
