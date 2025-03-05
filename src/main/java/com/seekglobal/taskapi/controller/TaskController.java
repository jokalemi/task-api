package com.seekglobal.taskapi.controller;

import com.seekglobal.taskapi.model.Task;
import com.seekglobal.taskapi.service.TaskService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Tag(name = "Task API", description = "Endpoints for task management")
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    public Mono<ResponseEntity<Task>> createTask(@Valid @RequestBody Task task) {
        return taskService.createTask(task)
                .map(savedTask -> ResponseEntity.status(HttpStatus.CREATED).body(savedTask));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Task>> updateTask(@PathVariable String id, @RequestBody Task updatedTask) {
        return taskService.updateTask(id, updatedTask)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTask(@PathVariable String id) {
        return taskService.deleteTask(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Task>> getTaskById(@PathVariable String id) {
        return taskService.getTaskById(id)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<Task>>> searchTasks(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Flux<Task> tasks = taskService.searchTasks(searchTerm, status, page, size);
        return Mono.just(ResponseEntity.ok(tasks));
    }
}
