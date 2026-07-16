package io.github.tiennnk.taskflow_rest_api.exception;

public class ForbiddenOperationException extends RuntimeException {
  public ForbiddenOperationException(String message) {
    super(message);
  }
}
