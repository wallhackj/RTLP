package org.wallhack.logprocessor.realtimelogprocessor.service.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.wallhack.logprocessor.realtimelogprocessor.repository.elastic.LogRepository;
import org.wallhack.logprocessor.realtimelogprocessor.service.RepositoryService;
import org.wallhack.logprocessor.realtimelogprocessor.service.dto.LogDTO;
import org.wallhack.logprocessor.realtimelogprocessor.service.dto.LogDocument;
import org.wallhack.logprocessor.realtimelogprocessor.service.dto.LogEntity;

@Slf4j
@Component("errorLogHandler")
@AllArgsConstructor
public class ErrorHandler implements LevelHandler {
    private final RepositoryService repository;
    private final LogRepository elasticRepository;

    @Override
    public void handle(LogDTO level) {
        var message = level.message() + " at :" + level.timestamp();
        log.error(message);
        var errorForSavingDB = LogEntity.builder()
                .timestamp(level.timestamp())
                .level(level.level())
                .message(message)
                .build();

        var errorForSavingElastic = new LogDocument();
        errorForSavingElastic.setTimestamp(level.timestamp());
        errorForSavingElastic.setLevel(level.level());
        errorForSavingElastic.setMessage(message);

        try {
            var entity = repository.save(errorForSavingDB);
            var output = elasticRepository.createOrUpdateDocument(errorForSavingElastic);
            if (entity != null) {
                System.out.println(output);
            }else throw new RuntimeException("Null entity!");

        } catch (Exception e) {
            log.error("Exception in ErrorHandler{}", e.getMessage());
        }
    }
}
