package com.maheer.taskgrid.dto;

import com.maheer.taskgrid.entity.TaskType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateTaskRequestDto {
    @NotBlank
    @Size(min = 3,max = 100)
    private String name;

    private String payload;

    @NotNull
    private TaskType taskType;

    @NotNull
    private Instant executeAt;

    @Min(0)
    @Max(5)
    private int maxRetries;
}