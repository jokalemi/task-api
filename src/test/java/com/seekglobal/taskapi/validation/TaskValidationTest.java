package com.seekglobal.taskapi.validation;

import com.seekglobal.taskapi.model.Task;
import com.seekglobal.taskapi.model.TaskStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static jakarta.validation.Validation.buildDefaultValidatorFactory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TaskValidationTest {
    private Validator validator;

    @BeforeEach
    void setupValidatorInstance() {
        ValidatorFactory factory = buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldDetectBlankTitle() {
        Task task = Task.builder().id(null).title("").description("Valid description").status(TaskStatus.TODO.name()).build();
        Set<ConstraintViolation<Task>> violations = validator.validate(task);

        assertFalse(violations.isEmpty());
        assertEquals("{task.title.not_blank}", violations.iterator().next().getMessage());
    }

    @Test
    void shouldDetectNullStatus() {
        Task task = Task.builder().id(null).title("Valid title").description("Valid description").status(null).build();
        Set<ConstraintViolation<Task>> violations = validator.validate(task);

        assertFalse(violations.isEmpty());
        assertEquals("{task.status.invalid}", violations.iterator().next().getMessage());
    }

    @Test
    void shouldDetectBlankDescription() {
        Task task = Task.builder().id(null).title("Valid title").description("").status(TaskStatus.TODO.name()).build();
        Set<ConstraintViolation<Task>> violations = validator.validate(task);

        assertFalse(violations.isEmpty());
        assertEquals("{task.description.not_blank}", violations.iterator().next().getMessage());
    }

    @Test
    void shouldPassValidationWithValidData() {
        Task task = Task.builder().id(null).title("Valid title").description("Valid description").status(TaskStatus.TODO.name()).build();
        Set<ConstraintViolation<Task>> violations = validator.validate(task);

        assertEquals(0, violations.size());
    }
}
