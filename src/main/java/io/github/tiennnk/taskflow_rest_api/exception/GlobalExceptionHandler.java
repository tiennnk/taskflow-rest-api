package io.github.tiennnk.taskflow_rest_api.exception;

import io.github.tiennnk.taskflow_rest_api.dto.error.ErrorResponse;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
    Map<String, String> fieldErrors = new LinkedHashMap<>();
    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      fieldErrors.put(error.getField(), error.getDefaultMessage());
    }

    return build(HttpStatus.BAD_REQUEST, "Validation failed", request, fieldErrors);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, WebRequest request) {
    return build(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
  }

  @ExceptionHandler(EmailAlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleConflict(EmailAlreadyExistsException ex, WebRequest request) {
    return build(HttpStatus.CONFLICT, ex.getMessage(), request, null);
  }

  @ExceptionHandler(ForbiddenOperationException.class)
  public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenOperationException ex, WebRequest request) {
    return build(HttpStatus.FORBIDDEN, ex.getMessage(), request, null);
  }

  @ExceptionHandler({InvalidTaskStateException.class, TaskLimitExceededException.class})
  public ResponseEntity<ErrorResponse> handleTaskRuleViolation(RuntimeException ex, WebRequest request) {
    return build(HttpStatus.CONFLICT, ex.getMessage(), request, null);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex, WebRequest request) {
    return build(HttpStatus.UNAUTHORIZED, "Invalid email or password", request, null);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, WebRequest request) {
    return build(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request, null);
  }

  private ResponseEntity<ErrorResponse> build(
      HttpStatus status, String message, WebRequest request, Map<String, String> fieldErrors) {
    ErrorResponse body = ErrorResponse.builder()
        .timestamp(Instant.now())
        .status(status.value())
        .error(status.getReasonPhrase())
        .message(message)
        .path(request.getDescription(false).replace("uri=", ""))
        .fieldErrors(fieldErrors)
        .build();

    return ResponseEntity.status(status).body(body);
  }
}
