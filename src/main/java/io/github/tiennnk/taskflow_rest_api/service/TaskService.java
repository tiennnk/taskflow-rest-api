package io.github.tiennnk.taskflow_rest_api.service;

import io.github.tiennnk.taskflow_rest_api.dto.task.TaskRequest;
import io.github.tiennnk.taskflow_rest_api.dto.task.TaskResponse;
import io.github.tiennnk.taskflow_rest_api.entity.Task;
import io.github.tiennnk.taskflow_rest_api.entity.TaskStatus;
import io.github.tiennnk.taskflow_rest_api.entity.User;
import io.github.tiennnk.taskflow_rest_api.exception.ForbiddenOperationException;
import io.github.tiennnk.taskflow_rest_api.exception.InvalidTaskStateException;
import io.github.tiennnk.taskflow_rest_api.exception.ResourceNotFoundException;
import io.github.tiennnk.taskflow_rest_api.exception.TaskLimitExceededException;
import io.github.tiennnk.taskflow_rest_api.repository.TaskRepository;
import io.github.tiennnk.taskflow_rest_api.repository.UserRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskService {

  private static final int MAX_TASKS_PER_USER = 100;

  private final TaskRepository taskRepository;
  private final UserRepository userRepository;

  @Transactional
  public TaskResponse createTask(Long ownerId, TaskRequest request) {
    if (taskRepository.countByOwnerId(ownerId) >= MAX_TASKS_PER_USER) {
      throw new TaskLimitExceededException("Maximum of " + MAX_TASKS_PER_USER + " tasks per user reached");
    }

    User owner = userRepository.getReferenceById(ownerId);

    Task task = Task.builder()
        .title(request.getTitle())
        .description(request.getDescription())
        .priority(request.getPriority())
        .status(TaskStatus.TODO)
        .dueDate(request.getDueDate())
        .owner(owner)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build();

    return TaskResponse.fromEntity(taskRepository.save(task));
  }

  public TaskResponse getTask(Long ownerId, Long taskId) {
    return TaskResponse.fromEntity(findOwnedTask(ownerId, taskId));
  }

  public Page<TaskResponse> listTasks(Long ownerId, TaskStatus status, Pageable pageable) {
    Page<Task> tasks = (status != null)
        ? taskRepository.findByOwnerIdAndStatus(ownerId, status, pageable)
        : taskRepository.findByOwnerId(ownerId, pageable);
    return tasks.map(TaskResponse::fromEntity);
  }

  public Page<TaskResponse> searchTasks(Long ownerId, String keyword, Pageable pageable) {
    return taskRepository.findByOwnerIdAndTitleContainingIgnoreCase(ownerId, keyword, pageable)
        .map(TaskResponse::fromEntity);
  }

  @Transactional
  public TaskResponse updateTask(Long ownerId, Long taskId, TaskRequest request) {
    Task task = findOwnedTask(ownerId, taskId);

    if (task.getStatus() == TaskStatus.DONE || task.getStatus() == TaskStatus.ARCHIVED) {
      throw new InvalidTaskStateException("Task is already completed");
    }

    task.setTitle(request.getTitle());
    task.setDescription(request.getDescription());
    task.setPriority(request.getPriority());
    task.setDueDate(request.getDueDate());
    task.setUpdatedAt(Instant.now());
    return TaskResponse.fromEntity(task);
  }

  @Transactional
  public TaskResponse updateStatus(Long ownerId, Long taskId, TaskStatus newStatus) {
    Task task = findOwnedTask(ownerId, taskId);
    TaskStatus current = task.getStatus();

    if (current == TaskStatus.ARCHIVED) {
      throw new InvalidTaskStateException("Task is archived");
    }
    if (current == TaskStatus.DONE && newStatus != TaskStatus.ARCHIVED) {
      throw new InvalidTaskStateException("Completed task can only be archived");
    }
    if (newStatus == TaskStatus.ARCHIVED && current != TaskStatus.DONE) {
      throw new InvalidTaskStateException("Only completed tasks can be archived");
    }
    if (newStatus == TaskStatus.DONE && task.getDueDate() == null) {
      throw new InvalidTaskStateException("Task needs a due date before it can be marked done");
    }

    task.setStatus(newStatus);
    task.setUpdatedAt(Instant.now());
    return TaskResponse.fromEntity(task);
  }

  @Transactional
  public void deleteTask(Long ownerId, Long taskId) {
    Task task = findOwnedTask(ownerId, taskId);
    taskRepository.delete(task);
  }

  private Task findOwnedTask(Long ownerId, Long taskId) {
    Task task = taskRepository.findById(taskId)
        .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

    if (!task.getOwner().getId().equals(ownerId)) {
      throw new ForbiddenOperationException("You do not have access to this task");
    }

    return task;
  }
}
