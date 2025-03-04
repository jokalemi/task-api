package com.seekglobal.taskapi.service;

import com.seekglobal.taskapi.model.Task;
import com.seekglobal.taskapi.model.TaskStatus;
import com.seekglobal.taskapi.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void shouldCreateTaskSuccessfully() {
        Task task = Task.builder().id(null).title("Title").description("Description").status(TaskStatus.TODO.name()).build();
        Task savedTask = Task.builder().id("1").title("Title").description("Description").status(TaskStatus.TODO.name()).build();

        when(taskRepository.save(any(Task.class))).thenReturn(Mono.just(savedTask));

        Mono<Task> createdTask = taskService.createTask(task);

        StepVerifier.create(createdTask)
                .assertNext(t -> {
                    assertNotNull(t.getId());
                    assertEquals("Title", t.getTitle());
                    assertEquals(TaskStatus.TODO.name(), t.getStatus());
                })
                .verifyComplete();
    }

    @Test
    void shouldUpdateTaskSuccessfully() {
        Task existingTask = Task.builder().id("1").title("Old task").description("Old description").status(TaskStatus.TODO.name()).build();
        Task updatedTask = Task.builder().id("1").title("New task").description("New description").status(TaskStatus.COMPLETED.name()).build();

        when(taskRepository.findById("1")).thenReturn(Mono.just(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(Mono.just(updatedTask));

        Mono<Task> result = taskService.updateTask("1", updatedTask);

        StepVerifier.create(result)
                .expectNextMatches(task ->
                        task.getTitle().equals("New task") &&
                                task.getDescription().equals("New description") &&
                                Objects.equals(task.getStatus(), TaskStatus.COMPLETED.name())
                )
                .verifyComplete();

        verify(taskRepository).findById("1");
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void shouldMarkTaskAsDeleted() {
        Task task = Task.builder().id("1").title("Title").description("Description").status(TaskStatus.TODO.name()).build();

        when(taskRepository.findById("1")).thenReturn(Mono.just(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        Mono<Task> result = taskService.deleteTask("1");

        StepVerifier.create(result)
                .assertNext(updatedTask -> assertTrue(updatedTask.isDeleted()))
                .verifyComplete();
    }
}
