package com.seekglobal.taskapi.controller;

import com.seekglobal.taskapi.exception.TaskNotFoundException;
import com.seekglobal.taskapi.model.Task;
import com.seekglobal.taskapi.model.TaskStatus;
import com.seekglobal.taskapi.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(TaskController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private TaskService taskService;

    @Test
    void shouldCreateTask() {
        Task task = Task.builder().id("1").title("New Task").description("Description").status(TaskStatus.TODO.name()).build();
        when(taskService.createTask(any(Task.class))).thenReturn(Mono.just(task));

        webTestClient.post().uri("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(task)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Task.class)
                .value(response -> assertEquals("New Task", response.getTitle()));
    }

    @Test
    void shouldGetTaskById() {
        Task task = Task.builder().id("1").title("Task Title").description("Description").status(TaskStatus.TODO.name()).build();
        when(taskService.getTaskById("1")).thenReturn(Mono.just(task));

        webTestClient.get().uri("/tasks/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Task.class)
                .value(response -> assertEquals("Task Title", response.getTitle()));
    }

    @Test
    void shouldReturnNotFoundWhenTaskDoesNotExist() {
        when(taskService.getTaskById("99")).thenReturn(Mono.error(new TaskNotFoundException("Task not found")));

        webTestClient.get().uri("/tasks/99")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldUpdateTask() {
        Task updatedTask = Task.builder().id("1").title("Updated Task").description("Updated Description").status(TaskStatus.COMPLETED.name()).build();
        when(taskService.updateTask(eq("1"), any(Task.class))).thenReturn(Mono.just(updatedTask));

        webTestClient.put().uri("/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedTask)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Task.class)
                .value(response -> assertEquals("Updated Task", response.getTitle()));
    }

    @Test
    void shouldDeleteTask() {
        when(taskService.deleteTask("1")).thenReturn(Mono.empty());

        webTestClient.delete().uri("/tasks/1")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void shouldSearchTasks() {
        Task task1 = Task.builder().id("1").title("Task 1").description("Description").status(TaskStatus.TODO.name()).build();
        Task task2 = Task.builder().id("1").title("Task 2").description("Description").status(TaskStatus.COMPLETED.name()).build();

        when(taskService.searchTasks(null, null, 0, 10))
                .thenReturn(Flux.just(task1, task2));

        webTestClient.get().uri("/tasks")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Task.class)
                .hasSize(2);
    }
}
