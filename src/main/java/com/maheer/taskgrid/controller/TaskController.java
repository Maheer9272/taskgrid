package com.maheer.taskgrid.controller;

import com.maheer.taskgrid.dto.CreateTaskRequestDto;
import com.maheer.taskgrid.dto.CreateTaskResponseDto;
import com.maheer.taskgrid.dto.TaskResponseDto;
import com.maheer.taskgrid.entity.TaskStatus;
import com.maheer.taskgrid.entity.TaskType;
import com.maheer.taskgrid.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<CreateTaskResponseDto> createTask(
            @RequestBody CreateTaskRequestDto requestDto
    ){
        CreateTaskResponseDto responseDto = taskService.createTask(requestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getTaskById(
            @PathVariable Long id
    ){
        TaskResponseDto responseDto = taskService.getTaskById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseDto);
    }

    @GetMapping
    public ResponseEntity<Page<TaskResponseDto>> getTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskType taskType,
            Pageable pageable
    ) {
        Page<TaskResponseDto> tasks =
                taskService.getTasks(status, taskType, pageable);

        return ResponseEntity.ok(tasks);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<TaskResponseDto> cancelTask(
            @PathVariable Long id){
        TaskResponseDto responseDto = taskService.cancelTask(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responseDto);
    }
}
