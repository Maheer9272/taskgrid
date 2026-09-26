package com.maheer.taskgrid.scheduler;

import com.maheer.taskgrid.entity.Task;
import com.maheer.taskgrid.entity.TaskStatus;
import com.maheer.taskgrid.repository.TaskRepository;
import com.maheer.taskgrid.service.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class TaskScheduler {

    private final TaskRepository taskRepository;
    private final TaskExecutor taskExecutor;
    public TaskScheduler(TaskRepository taskRepository, TaskExecutor taskExecutor) {
        this.taskRepository = taskRepository;
        this.taskExecutor = taskExecutor;
    }

    @Scheduled(fixedDelay = 5000)
    public void pollTask() {

        List<Task> dueTasksList =
                taskRepository.findByStatusAndExecuteAtLessThanEqual(
                        TaskStatus.PENDING,
                        Instant.now()
                );
        System.out.println(dueTasksList.size());

        for (Task task : dueTasksList) {

            task.setStatus(TaskStatus.RUNNING);
            taskRepository.save(task);

            try {
                taskExecutor.execute(task);

                task.setStatus(TaskStatus.SUCCESS);
                taskRepository.save(task);

            } catch (Exception e) {

                task.setStatus(TaskStatus.FAILED);
                taskRepository.save(task);
            }
        }
    }
}














