package io.github.tiennnk.taskflow_rest_api.dto.task;

import io.github.tiennnk.taskflow_rest_api.entity.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskStatusUpdateRequest {

  @NotNull
  private TaskStatus status;
}
