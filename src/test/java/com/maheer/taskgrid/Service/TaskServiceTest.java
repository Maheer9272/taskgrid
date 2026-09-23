package com.maheer.taskgrid.Service;

import com.maheer.taskgrid.dto.CreateTaskRequestDto;
import com.maheer.taskgrid.dto.CreateTaskResponseDto;
import com.maheer.taskgrid.dto.TaskResponseDto;
import com.maheer.taskgrid.entity.Task;
import com.maheer.taskgrid.entity.TaskStatus;
import com.maheer.taskgrid.entity.TaskType;
import com.maheer.taskgrid.exception.ResourceNotFoundException;
import com.maheer.taskgrid.repository.TaskRepository;
import com.maheer.taskgrid.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;


    @Test
    void createTaskShouldSavePendingTaskAndReturnResponse() {

        Instant executeAt = Instant.now().plusSeconds(60);

        CreateTaskRequestDto request = new CreateTaskRequestDto(
                "send-reminder",
                "user-123",
                TaskType.PRINT_MESSAGE,
                executeAt,
                3
        );

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CreateTaskResponseDto response = taskService.createTask(request);

        ArgumentCaptor<Task> taskCaptor =
                ArgumentCaptor.forClass(Task.class);

        verify(taskRepository).save(taskCaptor.capture());

        Task savedTask = taskCaptor.getValue();

        // Verify entity creation
        assertEquals("send-reminder", savedTask.getName());
        assertEquals("user-123", savedTask.getPayload());
        assertEquals(TaskType.PRINT_MESSAGE, savedTask.getTaskType());
        assertEquals(executeAt, savedTask.getExecuteAt());
        assertEquals(3, savedTask.getMaxRetries());

        // Verify initial task state
        assertEquals(TaskStatus.PENDING, savedTask.getStatus());
        assertEquals(0, savedTask.getRetryCount());
        assertNotNull(savedTask.getCreatedAt());
        assertNotNull(savedTask.getUpdatedAt());

        // Verify response mapping
        assertEquals(TaskStatus.PENDING, response.getStatus());
        assertEquals("send-reminder", response.getName());
        assertEquals("user-123", response.getPayload());
        assertEquals(TaskType.PRINT_MESSAGE, response.getTaskType());
        assertEquals(executeAt, response.getExecuteAt());
        assertEquals(3, response.getMaxRetries());
    }


    @ParameterizedTest
    @EnumSource(TaskType.class)
    void createTaskShouldPreserveTaskType(TaskType taskType) {

        Instant executeAt = Instant.now().plusSeconds(60);

        CreateTaskRequestDto request = new CreateTaskRequestDto(
                "test-task",
                "payload",
                taskType,
                executeAt,
                3
        );

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CreateTaskResponseDto response = taskService.createTask(request);

        assertEquals(taskType, response.getTaskType());

        ArgumentCaptor<Task> taskCaptor =
                ArgumentCaptor.forClass(Task.class);

        verify(taskRepository).save(taskCaptor.capture());

        assertEquals(taskType, taskCaptor.getValue().getTaskType());
    }


    @Test
    void createTaskShouldAllowZeroRetries() {

        Instant executeAt = Instant.now().plusSeconds(60);

        CreateTaskRequestDto request = new CreateTaskRequestDto(
                "no-retry-task",
                "payload",
                TaskType.SIMULATE_FAILURE,
                executeAt,
                0
        );

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CreateTaskResponseDto response = taskService.createTask(request);

        assertEquals(0, response.getMaxRetries());

        ArgumentCaptor<Task> taskCaptor =
                ArgumentCaptor.forClass(Task.class);

        verify(taskRepository).save(taskCaptor.capture());

        assertEquals(0, taskCaptor.getValue().getMaxRetries());
        assertEquals(0, taskCaptor.getValue().getRetryCount());
    }


    @Test
    void getTaskByIdShouldReturnTask_whenTaskExists() {

        Long taskId = 1L;

        Instant executeAt = Instant.now().plusSeconds(60);

        Task task = new Task(
                "test-task",
                "payload",
                TaskType.PRINT_MESSAGE,
                executeAt,
                3
        );

        when(taskRepository.findTaskById(taskId))
                .thenReturn(Optional.of(task));

        TaskResponseDto response =
                taskService.getTaskById(taskId);

        verify(taskRepository).findTaskById(taskId);

        assertEquals("test-task", response.getName());
        assertEquals("payload", response.getPayload());
        assertEquals(TaskType.PRINT_MESSAGE, response.getTaskType());
        assertEquals(TaskStatus.PENDING, response.getStatus());
        assertEquals(executeAt, response.getExecuteAt());
        assertEquals(3, response.getMaxRetries());
        assertEquals(0, response.getRetryCount());
    }


    @Test
    void getTaskByIdShouldThrowResourceNotFoundException_whenTaskDoesNotExist() {

        Long taskId = 999L;

        when(taskRepository.findTaskById(taskId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> taskService.getTaskById(taskId)
                );

        assertEquals("Task not found", exception.getMessage());

        verify(taskRepository).findTaskById(taskId);
    }


    @Test
    void getTasksShouldReturnAllTasks_whenNoFiltersProvided() {

        Task task1 = new Task(
                "task-1",
                "payload-1",
                TaskType.PRINT_MESSAGE,
                Instant.now().plusSeconds(60),
                3
        );

        Task task2 = new Task(
                "task-2",
                "payload-2",
                TaskType.SLEEP,
                Instant.now().plusSeconds(120),
                2
        );

        Pageable pageable = PageRequest.of(0, 10);

        Page<Task> taskPage = new PageImpl<>(
                List.of(task1, task2),
                pageable,
                2
        );

        when(taskRepository.findAll(
                any(org.springframework.data.jpa.domain.Specification.class),
                eq(pageable)
        )).thenReturn(taskPage);

        Page<TaskResponseDto> response =
                taskService.getTasks(null, null, pageable);

        assertEquals(2, response.getTotalElements());
        assertEquals(2, response.getContent().size());

        assertEquals("task-1",
                response.getContent().get(0).getName());

        assertEquals("task-2",
                response.getContent().get(1).getName());

        verify(taskRepository).findAll(
                any(org.springframework.data.jpa.domain.Specification.class),
                eq(pageable)
        );
    }


    @Test
    void getTasksShouldApplyStatusAndTypeFilters() {

        Task task = new Task(
                "print-task",
                "hello",
                TaskType.PRINT_MESSAGE,
                Instant.now().plusSeconds(60),
                3
        );

        Pageable pageable = PageRequest.of(0, 10);

        Page<Task> taskPage = new PageImpl<>(
                List.of(task),
                pageable,
                1
        );

        when(taskRepository.findAll(
                any(org.springframework.data.jpa.domain.Specification.class),
                eq(pageable)
        )).thenReturn(taskPage);

        Page<TaskResponseDto> response =
                taskService.getTasks(
                        TaskStatus.PENDING,
                        TaskType.PRINT_MESSAGE,
                        pageable
                );

        assertEquals(1, response.getTotalElements());
        assertEquals(
                "print-task",
                response.getContent().getFirst().getName()
        );

        assertEquals(
                TaskStatus.PENDING,
                response.getContent().getFirst().getStatus()
        );

        assertEquals(
                TaskType.PRINT_MESSAGE,
                response.getContent().getFirst().getTaskType()
        );

        verify(taskRepository).findAll(
                any(org.springframework.data.jpa.domain.Specification.class),
                eq(pageable)
        );
    }


    @Test
    void getTasksShouldRespectRequestedPageable() {

        Pageable pageable = PageRequest.of(2, 5);

        Page<Task> taskPage = new PageImpl<>(
                List.of(),
                pageable,
                12
        );

        when(taskRepository.findAll(
                any(org.springframework.data.jpa.domain.Specification.class),
                eq(pageable)
        )).thenReturn(taskPage);

        Page<TaskResponseDto> response =
                taskService.getTasks(null, null, pageable);

        assertEquals(12, response.getTotalElements());
        assertEquals(2, response.getNumber());
        assertEquals(5, response.getSize());

        verify(taskRepository).findAll(
                any(org.springframework.data.jpa.domain.Specification.class),
                eq(pageable)
        );
    }


    @Test
    void cancelTaskShouldCancelPendingTask() {

        Long taskId = 1L;

        Task task = new Task(
                "cancel-me",
                "payload",
                TaskType.PRINT_MESSAGE,
                Instant.now().plusSeconds(60),
                3
        );

        when(taskRepository.findTaskById(taskId))
                .thenReturn(Optional.of(task));

        TaskResponseDto response =
                taskService.cancelTask(taskId);

        assertEquals(TaskStatus.CANCELLED, task.getStatus());
        assertEquals(TaskStatus.CANCELLED, response.getStatus());

        verify(taskRepository).findTaskById(taskId);

        // cancelTask relies on JPA dirty checking
        verify(taskRepository, never()).save(any(Task.class));
    }


    @Test
    void cancelTaskShouldThrowResourceNotFoundException_whenTaskDoesNotExist() {

        Long taskId = 999L;

        when(taskRepository.findTaskById(taskId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> taskService.cancelTask(taskId)
        );

        verify(taskRepository).findTaskById(taskId);

        verify(taskRepository, never()).save(any(Task.class));
    }


    @Test
    void cancelTaskShouldPropagateIllegalStateException_whenTaskIsNotPending() {

        Long taskId = 1L;

        Task task = new Task(
                "already-cancelled",
                "payload",
                TaskType.PRINT_MESSAGE,
                Instant.now().plusSeconds(60),
                3
        );

        task.cancel();

        when(taskRepository.findTaskById(taskId))
                .thenReturn(Optional.of(task));

        assertThrows(
                IllegalStateException.class,
                () -> taskService.cancelTask(taskId)
        );

        verify(taskRepository).findTaskById(taskId);
    }
}