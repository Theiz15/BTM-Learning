package com.learning.btmlearning.controller;


import com.learning.btmlearning.dto.request.LoginRequest;
import com.learning.btmlearning.dto.request.RefreshRequest;
import com.learning.btmlearning.dto.request.RegisterRequest;
import com.learning.btmlearning.dto.response.ApiResponse;
import com.learning.btmlearning.dto.response.AuthResponse;
import com.learning.btmlearning.service.impl.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody @Valid RegisterRequest registerRequest){
        authService.register(registerRequest);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message("Register successful")
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/log-in")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody @Valid LoginRequest request){
        ApiResponse<AuthResponse> apiResponse = ApiResponse.<AuthResponse>builder()
                .message("Login successful")
                .result(authService.login(request))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@RequestBody RefreshRequest request) {
        ApiResponse<AuthResponse> apiResponse = ApiResponse.<AuthResponse>builder()
                .message("Refresh successful")
                .result(authService.refreshToken(request))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/log-out")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String authHeader) {
        authService.logout(authHeader);
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .message("Logout successful")
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}