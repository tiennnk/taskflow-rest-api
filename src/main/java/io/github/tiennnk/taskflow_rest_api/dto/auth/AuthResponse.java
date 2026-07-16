package io.github.tiennnk.taskflow_rest_api.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AuthResponse {
  private String token;
  @Builder.Default
  private String tokenType = "Bearer";
  private String email;
  private String fullName;
}
