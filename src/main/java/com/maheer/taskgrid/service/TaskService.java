package com.maheer.taskgrid.service;

import com.maheer.taskgrid.dto.CreateTaskRequestDto;
import com.maheer.taskgrid.dto.CreateTaskResponseDto;
import com.maheer.taskgrid.dto.TaskResponseDto;
import com.maheer.taskgrid.entity.Task;
import com.maheer.taskgrid.entity.TaskStatus;
import com.maheer.taskgrid.entity.TaskType;
import com.maheer.taskgrid.repository.TaskRepository;
import com.maheer.taskgrid.specification.TaskSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional
    public CreateTaskResponseDto createTask(CreateTaskRequestDto requestDto) {
        Task task = new Task(
                requestDto.getName(),
                requestDto.getPayload(),
                requestDto.getTaskType(),
                requestDto.getExecuteAt(),
                requestDto.getMaxRetries()
        );

        taskRepository.save(task);

        return new CreateTaskResponseDto(
                task.getName(),
                task.getPayload(),
                task.getTaskType(),
                task.getStatus(),
                task.getExecuteAt(),
                task.getMaxRetries()
        );
    }

    @Transactional
    public TaskResponseDto getTaskById(Long id) {
        Task task = taskRepository.findTaskById(id)
                .orElseThrow(()->new RuntimeException("resource not found"));

        return new TaskResponseDto(
                task.getName(),
                task.getPayload(),
                task.getTaskType(),
                task.getStatus(),
                task.getExecuteAt(),
                task.getRetryCount(),
                task.getMaxRetries(),
                task.getLastError(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                task.getStartedAt(),
                task.getCompletedAt(),
                task.getClaimedBy()
        );
    }

    @Transactional
    public Page<TaskResponseDto> getTasks(
            TaskStatus status,
            TaskType taskType,
            Pageable pageable
    ) {
        Specification<Task> specification =
                (root, query, criteriaBuilder) -> null;

        if (status != null) {
            specification = specification.and(
                    TaskSpecifications.hasStatus(status)
            );
        }

        if (taskType != null) {
            specification = specification.and(
                    TaskSpecifications.hasTaskType(taskType)
            );
        }

        Page<Task> tasks = taskRepository.findAll(specification, pageable);

        return tasks.map(task -> new TaskResponseDto(
                task.getName(),
                task.getPayload(),
                task.getTaskType(),
                task.getStatus(),
                task.getExecuteAt(),
                task.getRetryCount(),
                task.getMaxRetries(),
                task.getLastError(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                task.getStartedAt(),
                task.getCompletedAt(),
                task.getClaimedBy()
        ));
    }

    @Transactional
    public TaskResponseDto cancelTask(Long id) {
        Task task = taskRepository.findTaskById(id)
                .orElseThrow( () -> new RuntimeException("record not found"));
        task.cancel();

        return new TaskResponseDto(
                task.getName(),
                task.getPayload(),
                task.getTaskType(),
                task.getStatus(),
                task.getExecuteAt(),
                task.getRetryCount(),
                task.getMaxRetries(),
                task.getLastError(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                task.getStartedAt(),
                task.getCompletedAt(),
                task.getClaimedBy()
        );
    }
}
