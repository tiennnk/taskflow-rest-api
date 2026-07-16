package io.github.tiennnk.taskflow_rest_api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.tiennnk.taskflow_rest_api.dto.task.TaskRequest;
import io.github.tiennnk.taskflow_rest_api.dto.task.TaskResponse;
import io.github.tiennnk.taskflow_rest_api.entity.Priority;
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
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

  @Mock
  private TaskRepository taskRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private TaskService taskService;

  private User owner;
  private Task task;

  @BeforeEach
  void setUp() {
    owner = User.builder().id(1L).email("owner@test.com").build();
    task = Task.builder()
        .id(10L)
        .title("Original title")
        .description("desc")
        .status(TaskStatus.TODO)
        .priority(Priority.MEDIUM)
        .dueDate(LocalDate.now().plusDays(3))
        .owner(owner)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build();
  }

  @Test
  void createTask_savesTaskOwnedByCurrentUser() {
    when(userRepository.getReferenceById(1L)).thenReturn(owner);
    when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

    TaskRequest request = new TaskRequest();
    request.setTitle("Write report");
    request.setPriority(Priority.HIGH);
    request.setDueDate(LocalDate.now().plusDays(1));

    TaskResponse response = taskService.createTask(1L, request);

    assertThat(response.getTitle()).isEqualTo("Write report");
    assertThat(response.getStatus()).isEqualTo(TaskStatus.TODO);
  }

  @Test
  void getTask_whenNotOwner_throwsForbidden() {
    when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

    assertThatThrownBy(() -> taskService.getTask(2L, 10L))
        .isInstanceOf(ForbiddenOperationException.class);
  }

  @Test
  void getTask_whenNotFound_throwsResourceNotFound() {
    when(taskRepository.findById(99L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> taskService.getTask(1L, 99L))
        .isInstanceOf(ResourceNotFoundException.class);
  }

  @Test
  void updateStatus_whenOwner_updatesStatus() {
    when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

    TaskResponse response = taskService.updateStatus(1L, 10L, TaskStatus.DONE);

    assertThat(response.getStatus()).isEqualTo(TaskStatus.DONE);
  }

  @Test
  void createTask_whenUserAlreadyHas100Tasks_throwsTaskLimitExceeded() {
    when(taskRepository.countByOwnerId(1L)).thenReturn(100L);

    TaskRequest request = new TaskRequest();
    request.setTitle("One too many");
    request.setPriority(Priority.LOW);

    assertThatThrownBy(() -> taskService.createTask(1L, request))
        .isInstanceOf(TaskLimitExceededException.class);
  }

  @Test
  void updateTask_whenTaskAlreadyDone_throwsInvalidTaskState() {
    task.setStatus(TaskStatus.DONE);
    when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

    TaskRequest request = new TaskRequest();
    request.setTitle("Trying to edit a done task");
    request.setPriority(Priority.LOW);

    assertThatThrownBy(() -> taskService.updateTask(1L, 10L, request))
        .isInstanceOf(InvalidTaskStateException.class);
  }

  @Test
  void updateStatus_toDoneWithoutDueDate_throwsInvalidTaskState() {
    task.setDueDate(null);
    when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

    assertThatThrownBy(() -> taskService.updateStatus(1L, 10L, TaskStatus.DONE))
        .isInstanceOf(InvalidTaskStateException.class);
  }

  @Test
  void updateStatus_archiveBeforeDone_throwsInvalidTaskState() {
    when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

    assertThatThrownBy(() -> taskService.updateStatus(1L, 10L, TaskStatus.ARCHIVED))
        .isInstanceOf(InvalidTaskStateException.class);
  }

  @Test
  void updateStatus_archiveAfterDone_succeeds() {
    task.setStatus(TaskStatus.DONE);
    when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

    TaskResponse response = taskService.updateStatus(1L, 10L, TaskStatus.ARCHIVED);

    assertThat(response.getStatus()).isEqualTo(TaskStatus.ARCHIVED);
  }

  @Test
  void deleteTask_whenOwner_deletesTask() {
    when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

    taskService.deleteTask(1L, 10L);

    verify(taskRepository).delete(task);
  }
}
