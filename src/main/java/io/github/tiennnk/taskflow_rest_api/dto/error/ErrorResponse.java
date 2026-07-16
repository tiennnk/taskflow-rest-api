package io.github.tiennnk.taskflow_rest_api.dto.error;

import java.time.Instant;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {
  private Instant timestamp;
  private int status;
  private String error;
  private String message;
  private String path;
  private Map<String, String> fieldErrors;
}
