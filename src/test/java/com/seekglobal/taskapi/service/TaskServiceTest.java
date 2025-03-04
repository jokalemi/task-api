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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void shouldCreateTaskSuccessfully() {
        Task task = new Task(null, "Título", "Descripción", TaskStatus.TODO.toString());
        Task savedTask = new Task("1", "Título", "Descripción", TaskStatus.TODO.toString());

        when(taskRepository.save(any(Task.class))).thenReturn(Mono.just(savedTask));

        Mono<Task> createdTask = taskService.createTask(task);

        StepVerifier.create(createdTask)
                .assertNext(t -> {
                    assertNotNull(t.getId());
                    assertEquals("Título", t.getTitle());
                    assertEquals(TaskStatus.TODO.toString(), t.getStatus());
                })
                .verifyComplete();
    }
}
