package com.maheer.taskgrid.repository;

import com.maheer.taskgrid.entity.Task;
import com.maheer.taskgrid.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    Optional<Task> findTaskById(Long id);

    @Query("""
        SELECT t
        FROM Task t
        WHERE t.status = :status
          AND t.executeAt <= :currentTime
        """)
    List<Task> findByStatusAndExecuteAtLessThanEqual(
            @Param("status") TaskStatus status,
            @Param("currentTime") Instant currentTime
    );
}
