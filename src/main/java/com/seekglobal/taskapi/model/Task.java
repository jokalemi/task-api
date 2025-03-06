package com.seekglobal.taskapi.model;

import com.seekglobal.taskapi.validation.ValidEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Document(collection = "tasks")
public class Task {
    @Id
    private String id;

    @NotBlank(message = "{task.title.not_blank}")
    @Size(max = 100, message = "{task.title.size}")
    private String title;

    @NotBlank(message = "{task.description.not_blank}")
    @Size(max = 500, message = "{task.description.size}")
    private String description;

    @ValidEnum(enumClass = TaskStatus.class)
    private String status;

    @Builder.Default
    private boolean isDeleted = false;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}

