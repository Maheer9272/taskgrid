package com.maheer.taskgrid.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void newTask_shouldInitializeWithPendingStatusAndTimestamps() {
        Instant executeAt = Instant.now().plusSeconds(60);

        Task task = new Task(
                "test-task",
                "hello",
                TaskType.PRINT_MESSAGE,
                executeAt,
                3
        );

        assertNull(task.getId());
        assertEquals("test-task", task.getName());
        assertEquals("hello", task.getPayload());
        assertEquals(TaskType.PRINT_MESSAGE, task.getTaskType());
        assertEquals(executeAt, task.getExecuteAt());

        assertEquals(TaskStatus.PENDING, task.getStatus());
        assertEquals(0, task.getRetryCount());
        assertEquals(3, task.getMaxRetries());

        assertNotNull(task.getCreatedAt());
        assertNotNull(task.getUpdatedAt());
        assertNull(task.getStartedAt());
        assertNull(task.getCompletedAt());
        assertNull(task.getClaimedBy());
        assertNull(task.getLastError());
    }

    @Test
    void cancel_shouldChangePendingTaskToCancelled() {
        Task task = new Task(
                "test-task",
                "hello",
                TaskType.PRINT_MESSAGE,
                Instant.now().plusSeconds(60),
                3
        );

        Instant previousUpdatedAt = task.getUpdatedAt();

        task.cancel();

        assertEquals(TaskStatus.CANCELLED, task.getStatus());
        assertNotNull(task.getUpdatedAt());
        assertFalse(task.getUpdatedAt().isBefore(previousUpdatedAt));
    }

    @Test
    void cancel_shouldThrowException_whenTaskIsNotPending() {
        Task task = new Task(
                "test-task",
                "hello",
                TaskType.PRINT_MESSAGE,
                Instant.now().plusSeconds(60),
                3
        );

        task.cancel();

        assertThrows(
                IllegalStateException.class,
                task::cancel
        );
    }
}
