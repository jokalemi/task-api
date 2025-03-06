package com.seekglobal.taskapi.service;

import com.seekglobal.taskapi.builder.TaskQueryBuilder;
import com.seekglobal.taskapi.exception.NotFoundException;
import com.seekglobal.taskapi.model.Task;
import com.seekglobal.taskapi.model.TaskStatus;
import com.seekglobal.taskapi.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ReactiveMongoTemplate mongoTemplate;

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
    void shouldReturnErrorWhenUpdatingNonExistentTask() {
        when(taskRepository.findById("999")).thenReturn(Mono.empty());

        StepVerifier.create(taskService.updateTask("999", Task.builder().build()))
                .expectError(NotFoundException.class)
                .verify();

        verify(taskRepository, never()).save(any(Task.class));
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

    @Test
    void shouldReturnErrorWhenDeletingNonExistentTask() {
        when(taskRepository.findById("999")).thenReturn(Mono.empty());

        StepVerifier.create(taskService.deleteTask("999"))
                .expectError(NotFoundException.class)
                .verify();

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void shouldReturnTaskById() {
        Task task = Task.builder().id("1").title("Title").description("Description").status(TaskStatus.TODO.name()).build();

        when(taskRepository.findById("1")).thenReturn(Mono.just(task));

        Mono<Task> result = taskService.getTaskById("1");

        StepVerifier.create(result)
                .assertNext(foundTask -> assertEquals("1", foundTask.getId()))
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorWhenTaskNotFound() {
        when(taskRepository.findById("1")).thenReturn(Mono.empty());

        Mono<Task> result = taskService.getTaskById("1");

        StepVerifier.create(result)
                .expectError(NotFoundException.class)
                .verify();
    }

    @Nested
    class SearchTasksTests {
        private Task task1, task2;
        private int page, size;

        @BeforeEach
        void setUp() {
            task1 = Task.builder().id("1").title("Task One").description("Description One").status(TaskStatus.TODO.name()).build();
            task2 = Task.builder().id("2").title("Task Two").description("Description Two").status(TaskStatus.IN_PROGRESS.name()).build();
            page = 0;
            size = 10;
        }

        @Test
        void shouldSearchTasksSuccessfully() {
            String searchTerm = "Title";
            String status = TaskStatus.TODO.name();

            Query expectedQuery = new TaskQueryBuilder()
                    .withSearchTerm(searchTerm)
                    .withStatus(status)
                    .build();

            PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "title"));
            expectedQuery.with(pageRequest);

            List<Task> tasks = List.of(task1, task2);

            when(mongoTemplate.find(expectedQuery, Task.class)).thenReturn(Flux.fromIterable(tasks));

            Flux<Task> result = taskService.searchTasks(searchTerm, status, page, size);

            StepVerifier.create(result)
                    .expectNextMatches(task -> task.getId().equals("1") && task.getTitle().equals("Task One"))
                    .expectNextMatches(task -> task.getId().equals("2") && task.getTitle().equals("Task Two"))
                    .verifyComplete();

            verify(mongoTemplate).find(expectedQuery, Task.class);
        }

        @Test
        void shouldReturnAllTasksWhenNoFiltersProvided() {
            Query expectedQuery = new TaskQueryBuilder().build();

            PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "title"));
            expectedQuery.with(pageRequest);

            List<Task> tasks = List.of(task1, task2);
            when(mongoTemplate.find(expectedQuery, Task.class)).thenReturn(Flux.fromIterable(tasks));

            Flux<Task> result = taskService.searchTasks(null, null, page, size);

            StepVerifier.create(result)
                    .expectNextCount(2)
                    .verifyComplete();

            verify(mongoTemplate).find(expectedQuery, Task.class);
        }

        @Test
        void shouldReturnEmptyFluxWhenNoTasksFound() {
            Query expectedQuery = new TaskQueryBuilder().build();

            PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "title"));
            expectedQuery.with(pageRequest);

            when(mongoTemplate.find(expectedQuery, Task.class)).thenReturn(Flux.empty());

            Flux<Task> result = taskService.searchTasks(null, null, page, size);

            StepVerifier.create(result)
                    .verifyComplete();

            verify(mongoTemplate).find(expectedQuery, Task.class);
        }
    }
}
