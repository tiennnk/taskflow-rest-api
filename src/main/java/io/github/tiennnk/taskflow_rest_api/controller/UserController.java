package io.github.tiennnk.taskflow_rest_api.controller;

import io.github.tiennnk.taskflow_rest_api.dto.user.UserResponse;
import io.github.tiennnk.taskflow_rest_api.entity.User;
import io.github.tiennnk.taskflow_rest_api.exception.ResourceNotFoundException;
import io.github.tiennnk.taskflow_rest_api.repository.UserRepository;
import io.github.tiennnk.taskflow_rest_api.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserRepository userRepository;

  @GetMapping("/me")
  public UserResponse me(@AuthenticationPrincipal UserPrincipal principal) {
    User user = userRepository.findById(principal.getId())
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    return UserResponse.builder()
        .id(user.getId())
        .email(user.getEmail())
        .fullName(user.getFullName())
        .role(user.getRole())
        .build();
  }
}
