package io.github.tiennnk.taskflow_rest_api.dto.task;

import io.github.tiennnk.taskflow_rest_api.entity.Priority;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;

@Data
public class TaskRequest {

  @NotBlank
  private String title;

  private String description;

  @NotNull
  private Priority priority;
  
  @FutureOrPresent
  private LocalDate dueDate;
}
