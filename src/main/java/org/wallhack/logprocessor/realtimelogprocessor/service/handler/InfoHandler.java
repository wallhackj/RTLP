package org.wallhack.logprocessor.realtimelogprocessor.service.handler;

import org.springframework.stereotype.Component;
import org.wallhack.logprocessor.realtimelogprocessor.service.dto.LogDTO;

@Component("infoLogHandler")
public class InfoHandler implements LevelHandler{
    @Override
    public void handle(LogDTO level) {
        System.out.println("Informational log : " + level.message());
    }
}
