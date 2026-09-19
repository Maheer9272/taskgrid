package com.maheer.taskgrid.specification;

import com.maheer.taskgrid.entity.Task;
import com.maheer.taskgrid.entity.TaskStatus;
import com.maheer.taskgrid.entity.TaskType;
import org.springframework.data.jpa.domain.Specification;

public class TaskSpecifications {

    public static Specification<Task> hasStatus(TaskStatus status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Task> hasTaskType(TaskType taskType) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("taskType"), taskType);
    }
}