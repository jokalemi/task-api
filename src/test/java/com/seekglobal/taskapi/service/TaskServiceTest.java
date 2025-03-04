package com.seekglobal.taskapi.service;

import com.seekglobal.taskapi.model.Task;
import com.seekglobal.taskapi.model.TaskStatus;
import com.seekglobal.taskapi.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        Task task = Task.builder().id(null).title("Título").description("Descripción").status(TaskStatus.TODO.name()).build();
        Task savedTask = Task.builder().id("1").title("Título").description("Descripción").status(TaskStatus.TODO.name()).build();

        when(taskRepository.save(any(Task.class))).thenReturn(Mono.just(savedTask));

        Mono<Task> createdTask = taskService.createTask(task);

        StepVerifier.create(createdTask)
                .assertNext(t -> {
                    assertNotNull(t.getId());
                    assertEquals("Título", t.getTitle());
                    assertEquals(TaskStatus.TODO.name(), t.getStatus());
                })
                .verifyComplete();
    }

    @Test
    void shouldUpdateTaskSuccessfully() {
        Task existingTask = Task.builder().id("1").title("Tarea vieja").description("Descripción vieja").status(TaskStatus.TODO.name()).build();
        Task updatedTask = Task.builder().id("1").title("Tarea nueva").description("Descripción nueva").status(TaskStatus.COMPLETED.name()).build();

        when(taskRepository.findById("1")).thenReturn(Mono.just(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(Mono.just(updatedTask));

        Mono<Task> result = taskService.updateTask("1", updatedTask);

        StepVerifier.create(result)
                .expectNextMatches(task ->
                        task.getTitle().equals("Tarea nueva") &&
                                task.getDescription().equals("Descripción nueva") &&
                                Objects.equals(task.getStatus(), TaskStatus.COMPLETED.name())
                )
                .verifyComplete();

        verify(taskRepository).findById("1");
        verify(taskRepository).save(any(Task.class));
    }
}
