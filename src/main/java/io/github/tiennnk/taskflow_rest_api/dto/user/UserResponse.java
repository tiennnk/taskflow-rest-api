package io.github.tiennnk.taskflow_rest_api.dto.user;

import io.github.tiennnk.taskflow_rest_api.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserResponse {
  private Long id;
  private String email;
  private String fullName;
  private Role role;
}
