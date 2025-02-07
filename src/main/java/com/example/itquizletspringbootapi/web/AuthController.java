package com.example.itquizletspringbootapi.web;

import com.example.itquizletspringbootapi.dto.user.AuthenticationDto;
import com.example.itquizletspringbootapi.dto.user.UserDto;
import com.example.itquizletspringbootapi.dto.user.UserLoginDto;
import com.example.itquizletspringbootapi.dto.user.UserRegisterDto;
import com.example.itquizletspringbootapi.repository.entity.UserEntity;
import com.example.itquizletspringbootapi.service.AuthService;
import com.example.itquizletspringbootapi.service.mapper.UserMapper;
import com.example.itquizletspringbootapi.web.decorators.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management API")
public class AuthController {
    private final AuthService authService;
    private final UserMapper userMapper;

    @Operation(
            summary = "Register new user",
            description = "Register a new user in the system"
    )
    @SecurityRequirements
    @ApiResponse(responseCode = "200", description = "Successfully registered")
    @PostMapping("/register")
    public ResponseEntity<AuthenticationDto> register(
            @Valid @RequestBody UserRegisterDto request
    ) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(
            summary = "Login user",
            description = "Authenticate existing user"
    )
    @SecurityRequirements
    @ApiResponse(responseCode = "200", description = "Successfully authenticated")
    @PostMapping("/login")
    public ResponseEntity<AuthenticationDto> login(
            @Valid @RequestBody UserLoginDto request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(
            summary = "Get current user",
            description = "Get details of currently authenticated user",
            security = @SecurityRequirement(name = "Bearer Token")
    )
    @ApiResponse(responseCode = "200", description = "User details retrieved successfully")
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(@CurrentUser UserEntity user) {
        return ResponseEntity.ok(userMapper.toDto(user));
    }
}