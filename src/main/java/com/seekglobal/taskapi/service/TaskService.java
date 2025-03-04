package com.seekglobal.taskapi.service;

import com.seekglobal.taskapi.builder.TaskQueryBuilder;
import com.seekglobal.taskapi.exception.TaskNotFoundException;
import com.seekglobal.taskapi.model.Task;
import com.seekglobal.taskapi.repository.TaskRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final ReactiveMongoTemplate mongoTemplate;

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

    public Mono<Task> getTaskById(String id) {
        return taskRepository.findById(id)
                .filter(task -> !task.isDeleted())
                .switchIfEmpty(Mono.error(new TaskNotFoundException("Task not found with id: " + id)));
    }

    public Flux<Task> searchTasks(String searchTerm, String status, int page, int size) {
        Query query = new TaskQueryBuilder()
                .withSearchTerm(searchTerm)
                .withStatus(status)
                .build();

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "title"));
        query.with(pageRequest);

        return mongoTemplate.find(query, Task.class);
    }
}
