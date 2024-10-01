package org.wallhack.logprocessor.realtimelogprocessor.service.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.wallhack.logprocessor.realtimelogprocessor.service.dto.LogDTO;
import org.wallhack.logprocessor.realtimelogprocessor.service.dto.LogDocument;
import org.wallhack.logprocessor.realtimelogprocessor.service.dto.LogEntity;
import org.wallhack.logprocessor.realtimelogprocessor.repository.elastic.LogRepository;
import org.wallhack.logprocessor.realtimelogprocessor.repository.jpa.H2Repository;

@Slf4j
@Component("errorLogHandler")
@AllArgsConstructor
public class ErrorHandler implements LevelHandler {
    private final H2Repository h2Repository;
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
            h2Repository.save(errorForSavingDB);
            var output = elasticRepository.createOrUpdateDocument(errorForSavingElastic);
            System.out.println(output);
        } catch (Exception e) {
            log.error("Exception in ErrorHandler{}", e.getMessage());
        }
    }
}
