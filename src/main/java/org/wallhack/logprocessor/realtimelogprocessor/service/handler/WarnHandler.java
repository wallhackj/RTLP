package org.wallhack.logprocessor.realtimelogprocessor.service.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.wallhack.logprocessor.realtimelogprocessor.service.dto.LogDTO;
import org.wallhack.logprocessor.realtimelogprocessor.service.dto.LogDocument;
import org.wallhack.logprocessor.realtimelogprocessor.repository.elastic.LogRepository;

@Slf4j
@Component("warnLogHandler")
@AllArgsConstructor
public class WarnHandler implements LevelHandler {
    private final LogRepository elasticRepository;

    @Override
    public void handle(LogDTO level) {
        var message = level.message() + " at :" + level.timestamp();
        log.warn(message);
        var warnForSaving = new LogDocument();
        warnForSaving.setTimestamp(level.timestamp());
        warnForSaving.setLevel(level.level());
        warnForSaving.setMessage(message);

        try {
            elasticRepository.createOrUpdateDocument(warnForSaving);
        } catch (Exception e) {
            log.error("Exception in WarnHandler{}", e.getMessage());
        }
    }
}
