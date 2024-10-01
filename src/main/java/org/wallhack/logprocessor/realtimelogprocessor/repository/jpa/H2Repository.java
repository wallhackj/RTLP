package org.wallhack.logprocessor.realtimelogprocessor.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.wallhack.logprocessor.realtimelogprocessor.service.dto.LogEntity;

@Repository
public interface H2Repository extends JpaRepository<LogEntity, Long> {
}
