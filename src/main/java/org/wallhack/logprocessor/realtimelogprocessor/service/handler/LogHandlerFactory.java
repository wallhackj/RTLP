package org.wallhack.logprocessor.realtimelogprocessor.service.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class LogHandlerFactory {
    private final Map<String, LevelHandler> handlers;

    @Autowired
    public LogHandlerFactory(Map<String, LevelHandler> handlers) {
        this.handlers = handlers;
        log.info("Registered handlers: {}", handlers.keySet());
    }

    public LevelHandler getHandler(String level) {
        return switch (level.toUpperCase()) {
            case "INFO" -> handlers.get("infoLogHandler");
            case "WARN" -> handlers.get("warnLogHandler");
            case "ERROR" -> handlers.get("errorLogHandler");
            default -> throw new IllegalArgumentException("Unsupported log level: " + level);
        };
    }
}
