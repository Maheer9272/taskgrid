package com.maheer.taskgrid.service;

import com.maheer.taskgrid.entity.Task;
import org.springframework.stereotype.Service;

@Service
public class TaskExecutor {

    public void execute(Task task) {

        switch (task.getTaskType()) {

            case PRINT_MESSAGE:
                System.out.println(task.getPayload());
                break;

            case SLEEP:
                try {
                    long duration = Long.parseLong(task.getPayload());
                    Thread.sleep(duration);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Task interrupted", e);
                }
                break;

            case SIMULATE_FAILURE:
                throw new RuntimeException("Simulated task failure");

            default:
                throw new IllegalArgumentException(
                        "Unsupported task type: " + task.getTaskType()
                );
        }
    }
}