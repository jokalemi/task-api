package com.seekglobal.taskapi.service;

import com.seekglobal.taskapi.model.Task;
import com.seekglobal.taskapi.repository.TaskRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    public Mono<Task> createTask(@Valid Task task) {
        return taskRepository.save(task);
    }
}
