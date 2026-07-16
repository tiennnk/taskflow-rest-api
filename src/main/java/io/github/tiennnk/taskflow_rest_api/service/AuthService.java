package io.github.tiennnk.taskflow_rest_api.service;

import io.github.tiennnk.taskflow_rest_api.dto.auth.AuthResponse;
import io.github.tiennnk.taskflow_rest_api.dto.auth.LoginRequest;
import io.github.tiennnk.taskflow_rest_api.dto.auth.RegisterRequest;
import io.github.tiennnk.taskflow_rest_api.entity.Role;
import io.github.tiennnk.taskflow_rest_api.entity.User;
import io.github.tiennnk.taskflow_rest_api.exception.EmailAlreadyExistsException;
import io.github.tiennnk.taskflow_rest_api.repository.UserRepository;
import io.github.tiennnk.taskflow_rest_api.security.JwtUtil;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;
  private final AuthenticationManager authenticationManager;

  public AuthResponse register(RegisterRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new EmailAlreadyExistsException("Email already registered");
    }

    User user = User.builder()
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .fullName(request.getFullName())
        .role(Role.USER)
        .createdAt(Instant.now())
        .build();

    userRepository.save(user);

    String token = jwtUtil.generateToken(user.getEmail());
    return AuthResponse.builder()
        .token(token)
        .tokenType("Bearer")
        .email(user.getEmail())
        .fullName(user.getFullName())
        .build();
  }

  public AuthResponse login(LoginRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + request.getEmail()));

    String token = jwtUtil.generateToken(user.getEmail());
    return AuthResponse.builder()
        .token(token)
        .tokenType("Bearer")
        .email(user.getEmail())
        .fullName(user.getFullName())
        .build();
  }
}
