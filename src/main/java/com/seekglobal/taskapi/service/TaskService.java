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

    public Mono<Task> updateTask(String id, Task updatedTask) {
        return taskRepository.findById(id)
                .flatMap(existingTask -> {
                    existingTask.setTitle(updatedTask.getTitle());
                    existingTask.setDescription(updatedTask.getDescription());
                    existingTask.setStatus(updatedTask.getStatus());
                    return taskRepository.save(existingTask);
                });
    }

    public Mono<Task> deleteTask(String id) {
        return taskRepository.findById(id)
                .flatMap(task -> {
                    task.setDeleted(true);
                    return taskRepository.save(task);
                });
    }
}
