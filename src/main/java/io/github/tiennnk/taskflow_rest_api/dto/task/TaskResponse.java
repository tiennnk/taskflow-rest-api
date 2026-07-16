package io.github.tiennnk.taskflow_rest_api.dto.task;

import io.github.tiennnk.taskflow_rest_api.entity.Priority;
import io.github.tiennnk.taskflow_rest_api.entity.Task;
import io.github.tiennnk.taskflow_rest_api.entity.TaskStatus;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TaskResponse {
  private Long id;
  private String title;
  private String description;
  private TaskStatus status;
  private Priority priority;
  private LocalDate dueDate;
  private Instant createdAt;
  private Instant updatedAt;

  public static TaskResponse fromEntity(Task task) {
    return TaskResponse.builder()
        .id(task.getId())
        .title(task.getTitle())
        .description(task.getDescription())
        .status(task.getStatus())
        .priority(task.getPriority())
        .dueDate(task.getDueDate())
        .createdAt(task.getCreatedAt())
        .updatedAt(task.getUpdatedAt())
        .build();
  }
}
