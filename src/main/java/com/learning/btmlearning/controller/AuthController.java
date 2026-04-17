    package com.learning.btmlearning.controller;


    import com.learning.btmlearning.dto.request.GoogleLoginRequest;
    import com.learning.btmlearning.dto.request.LoginRequest;
    import com.learning.btmlearning.dto.request.RefreshRequest;
    import com.learning.btmlearning.dto.request.RegisterRequest;
    import com.learning.btmlearning.dto.response.ApiResponse;
    import com.learning.btmlearning.dto.response.AuthResponse;
    import com.learning.btmlearning.service.impl.AuthService;
    import jakarta.validation.Valid;
    import lombok.AccessLevel;
    import lombok.RequiredArgsConstructor;
    import lombok.experimental.FieldDefaults;
    import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping("${api.prefix}/auth")
    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE ,makeFinal = true)
    public class AuthController {
        AuthService authService;

        @PostMapping("/register")
        public ApiResponse<AuthResponse> register(@RequestBody @Valid RegisterRequest registerRequest){
            return ApiResponse.<AuthResponse>builder()
                    .message("Register successful")
                    .result(authService.register(registerRequest))
                    .build();
        }

        @PostMapping("/log-in")
        public ApiResponse<AuthResponse> login(@RequestBody @Valid LoginRequest request){
            return ApiResponse.<AuthResponse>builder()
                    .message("Login successful")
                    .result(authService.login(request))
                    .build();
        }

        @PostMapping("/refresh")
        public ApiResponse<AuthResponse> refresh(@RequestBody RefreshRequest request) {
            return ApiResponse.<AuthResponse>builder()
                    .message("Refresh successful")
                    .result(authService.refreshToken(request))
                    .build();
        }

        @PostMapping("/log-out")
        public ApiResponse<String> logout(@RequestHeader("Authorization") String authHeader) {
            authService.logout(authHeader);
            return ApiResponse.<String>builder()
                    .message("Refresh successful")
                    .build();
        }

        @PostMapping("/google")
        public ApiResponse<AuthResponse> googleLogin(@RequestBody @Valid GoogleLoginRequest request) {
            return ApiResponse.<AuthResponse>builder()
                    .message("google login successful")
                    .result(authService.googleLogin(request))
                    .build();
        }
    }