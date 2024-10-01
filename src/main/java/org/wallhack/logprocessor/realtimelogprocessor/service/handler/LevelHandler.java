package org.wallhack.logprocessor.realtimelogprocessor.service.handler;

import org.wallhack.logprocessor.realtimelogprocessor.service.dto.LogDTO;

public interface LevelHandler {
    void handle(LogDTO level);
}
