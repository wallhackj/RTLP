package org.wallhack.logprocessor.realtimelogprocessor.service;

import org.springframework.stereotype.Service;
import org.wallhack.logprocessor.realtimelogprocessor.repository.jpa.Repository;
import org.wallhack.logprocessor.realtimelogprocessor.service.dto.LogEntity;

import java.util.List;
import java.util.Optional;

@Service
public class RepositoryService {
    private final Repository repository;

    public RepositoryService(Repository repository) {
        this.repository = repository;
    }

    public LogEntity save(LogEntity entity) {
        return repository.save(entity);
    }

    public Optional<LogEntity> findById(Long id) {
        return repository.findById(id);
    }

    public List<LogEntity> findAll() {
        return repository.findAll();
    }

    public void delete(LogEntity entity) {
        repository.delete(entity);
    }
}
