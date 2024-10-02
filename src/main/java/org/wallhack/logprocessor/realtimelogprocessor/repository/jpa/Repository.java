package org.wallhack.logprocessor.realtimelogprocessor.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wallhack.logprocessor.realtimelogprocessor.service.dto.LogEntity;

@org.springframework.stereotype.Repository
public interface Repository extends JpaRepository<LogEntity, Long> {
}
