package com.maheer.taskgrid.dto;

import com.maheer.taskgrid.entity.TaskStatus;
import com.maheer.taskgrid.entity.TaskType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDto {
    private Long id;
    private String name;
    private String payload;
    private TaskType taskType;
    private TaskStatus status;
    private Instant executeAt;
    private int retryCount;
    private int maxRetries;
    private String lastError;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant startedAt;
    private Instant completedAt;
    private String claimedBy;
}
