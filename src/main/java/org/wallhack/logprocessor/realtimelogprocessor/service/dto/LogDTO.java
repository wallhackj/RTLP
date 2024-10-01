package org.wallhack.logprocessor.realtimelogprocessor.service.dto;

import java.util.Date;

public record LogDTO(String message, String level, Date timestamp) {
}
