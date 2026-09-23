package com.maheer.taskgrid.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            nullable = false,
            length = 100
    )
    private String name;

    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskType taskType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @Column(
            nullable = false
    )
    private Instant executeAt;

    @Column(
            nullable = false
    )
    private int retryCount;

    @Column(
            nullable = false
    )
    private int maxRetries;

    private String lastError;

    @Column(
            nullable = false,
            updatable = false
    )
    private Instant createdAt;

    @Column(
            nullable = false
    )
    private Instant updatedAt;

    private Instant startedAt;

    private Instant completedAt;

    private String claimedBy;

    protected Task(){}

    public Task(String name, String payload,
                TaskType taskType, Instant executeAt,
                int maxRetries) {
        this.name = name;
        this.payload = payload;
        this.taskType = taskType;
        this.executeAt = executeAt;
        this.maxRetries = maxRetries;

        this.status = TaskStatus.PENDING;

        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;

    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPayload() {
        return payload;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public Instant getExecuteAt() {
        return executeAt;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public String getLastError() {
        return lastError;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public String getClaimedBy() {
        return claimedBy;
    }

    public void cancel(){
        if ((this.getStatus() != TaskStatus.PENDING)){
            throw new IllegalStateException("Cannon cancel the non pending task");
        }
        this.status = TaskStatus.CANCELLED;
        this.updatedAt = Instant.now();
    }
}
