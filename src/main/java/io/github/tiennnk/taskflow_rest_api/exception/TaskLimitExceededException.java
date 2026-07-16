package io.github.tiennnk.taskflow_rest_api.exception;

public class TaskLimitExceededException extends RuntimeException {
  public TaskLimitExceededException(String message) {
    super(message);
  }
}
