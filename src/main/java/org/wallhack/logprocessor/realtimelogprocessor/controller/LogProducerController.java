package org.wallhack.logprocessor.realtimelogprocessor.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.wallhack.logprocessor.realtimelogprocessor.service.dto.LogDTO;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@AllArgsConstructor
public class LogProducerController {
    private KafkaTemplate<String, LogDTO> kafkaTemplate;

    private static boolean isValidDTO(LogDTO logDTO) {
        return logDTO.message().isEmpty() && logDTO.level().isEmpty() && logDTO.timestamp() == null;
    }

    @PostMapping("/log")
    public Mono<ResponseEntity<String>> sendLogToKafka(@RequestBody LogDTO logDTO) {
        if (isValidDTO(logDTO)) {
            log.warn("DTO must not be empty: {}", logDTO);
            return Mono.just(ResponseEntity.badRequest().body("Bad DTO"));
        }

        return Mono.fromFuture(() -> kafkaTemplate.send("realtimelog", logDTO)).flatMap(result -> {
            log.info("Log sent to Kafka: {}", result);
            return Mono.just(ResponseEntity.ok("Log processed successfully"));
        }).onErrorResume(e -> {
            log.error("Error sending log to Kafka: {}", e.getMessage());
            return Mono.just(ResponseEntity.status(500).body("Error sending log to Kafka"));
        });

    }
}
