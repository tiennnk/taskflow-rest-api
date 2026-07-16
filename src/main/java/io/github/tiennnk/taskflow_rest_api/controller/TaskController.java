package io.github.tiennnk.taskflow_rest_api.controller;

import io.github.tiennnk.taskflow_rest_api.dto.task.TaskRequest;
import io.github.tiennnk.taskflow_rest_api.dto.task.TaskResponse;
import io.github.tiennnk.taskflow_rest_api.dto.task.TaskStatusUpdateRequest;
import io.github.tiennnk.taskflow_rest_api.entity.TaskStatus;
import io.github.tiennnk.taskflow_rest_api.security.UserPrincipal;
import io.github.tiennnk.taskflow_rest_api.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

  private final TaskService taskService;

  @PostMapping
  public ResponseEntity<TaskResponse> create(
      @AuthenticationPrincipal UserPrincipal principal,
      @Valid @RequestBody TaskRequest request) {
    TaskResponse response = taskService.createTask(principal.getId(), request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping
  public Page<TaskResponse> list(
      @AuthenticationPrincipal UserPrincipal principal,
      @RequestParam(required = false) TaskStatus status,
      Pageable pageable) {
    return taskService.listTasks(principal.getId(), status, pageable);
  }

  @GetMapping("/search")
  public Page<TaskResponse> search(
      @AuthenticationPrincipal UserPrincipal principal,
      @RequestParam String keyword,
      Pageable pageable) {
    return taskService.searchTasks(principal.getId(), keyword, pageable);
  }

  @GetMapping("/{id}")
  public TaskResponse getOne(
      @AuthenticationPrincipal UserPrincipal principal,
      @PathVariable Long id) {
    return taskService.getTask(principal.getId(), id);
  }

  @PutMapping("/{id}")
  public TaskResponse update(
      @AuthenticationPrincipal UserPrincipal principal,
      @PathVariable Long id,
      @Valid @RequestBody TaskRequest request) {
    return taskService.updateTask(principal.getId(), id, request);
  }

  @PatchMapping("/{id}/status")
  public TaskResponse updateStatus(
      @AuthenticationPrincipal UserPrincipal principal,
      @PathVariable Long id,
      @Valid @RequestBody TaskStatusUpdateRequest request) {
    return taskService.updateStatus(principal.getId(), id, request.getStatus());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(
      @AuthenticationPrincipal UserPrincipal principal,
      @PathVariable Long id) {
    taskService.deleteTask(principal.getId(), id);
    return ResponseEntity.noContent().build();
  }
}
