package io.github.tiennnk.taskflow_rest_api.repository;

import io.github.tiennnk.taskflow_rest_api.entity.Task;
import io.github.tiennnk.taskflow_rest_api.entity.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

  Page<Task> findByOwnerId(Long ownerId, Pageable pageable);

  Page<Task> findByOwnerIdAndStatus(Long ownerId, TaskStatus status, Pageable pageable);

  Page<Task> findByOwnerIdAndTitleContainingIgnoreCase(Long ownerId, String keyword, Pageable pageable);

  long countByOwnerId(Long ownerId);
}
