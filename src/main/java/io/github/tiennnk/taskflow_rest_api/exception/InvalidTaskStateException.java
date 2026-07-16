package io.github.tiennnk.taskflow_rest_api.exception;

public class InvalidTaskStateException extends RuntimeException {
  public InvalidTaskStateException(String message) {
    super(message);
  }
}
