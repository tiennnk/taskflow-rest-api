package io.github.tiennnk.taskflow_rest_api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import io.github.tiennnk.taskflow_rest_api.dto.auth.AuthResponse;
import io.github.tiennnk.taskflow_rest_api.dto.auth.LoginRequest;
import io.github.tiennnk.taskflow_rest_api.dto.auth.RegisterRequest;
import io.github.tiennnk.taskflow_rest_api.entity.Role;
import io.github.tiennnk.taskflow_rest_api.entity.User;
import io.github.tiennnk.taskflow_rest_api.exception.EmailAlreadyExistsException;
import io.github.tiennnk.taskflow_rest_api.repository.UserRepository;
import io.github.tiennnk.taskflow_rest_api.security.JwtUtil;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private JwtUtil jwtUtil;

  @Mock
  private AuthenticationManager authenticationManager;

  @InjectMocks
  private AuthService authService;

  @Test
  void register_whenEmailAlreadyExists_throws() {
    RegisterRequest request = new RegisterRequest();
    request.setEmail("existing@test.com");
    request.setPassword("password123");
    request.setFullName("Existing User");

    when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);

    assertThatThrownBy(() -> authService.register(request))
        .isInstanceOf(EmailAlreadyExistsException.class);
  }

  @Test
  void register_whenEmailIsNew_savesUserAndReturnsToken() {
    RegisterRequest request = new RegisterRequest();
    request.setEmail("new@test.com");
    request.setPassword("password123");
    request.setFullName("New User");

    when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
    when(passwordEncoder.encode("password123")).thenReturn("hashed-password");
    when(jwtUtil.generateToken("new@test.com")).thenReturn("fake-jwt-token");

    AuthResponse response = authService.register(request);

    assertThat(response.getToken()).isEqualTo("fake-jwt-token");
    assertThat(response.getEmail()).isEqualTo("new@test.com");
  }

  @Test
  void login_withValidCredentials_returnsToken() {
    LoginRequest request = new LoginRequest();
    request.setEmail("user@test.com");
    request.setPassword("password123");

    User user = User.builder()
        .id(1L)
        .email("user@test.com")
        .fullName("Some User")
        .role(Role.USER)
        .build();

    when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
    when(jwtUtil.generateToken("user@test.com")).thenReturn("fake-jwt-token");

    AuthResponse response = authService.login(request);

    assertThat(response.getToken()).isEqualTo("fake-jwt-token");
    assertThat(response.getFullName()).isEqualTo("Some User");
  }
}
