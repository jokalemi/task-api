package com.seekglobal.taskapi.model;

import com.seekglobal.taskapi.validation.ValidEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
}

