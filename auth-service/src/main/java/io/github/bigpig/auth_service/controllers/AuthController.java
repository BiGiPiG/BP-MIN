package io.github.bigpig.auth_service.controllers;

import io.github.bigpig.auth_service.dto.request.RefreshTokenRequest;
import io.github.bigpig.auth_service.dto.request.SigninRequest;
import io.github.bigpig.auth_service.dto.request.SignupRequest;
import io.github.bigpig.auth_service.dto.response.AuthResponse;
import io.github.bigpig.auth_service.dto.response.SignupResponse;
import io.github.bigpig.auth_service.entity.User;
import io.github.bigpig.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@RequestBody @Validated SigninRequest signinRequest) {
        AuthResponse authResponse = authService.signin(signinRequest);
        return ResponseEntity.ok().body(authResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody @Validated SignupRequest signupRequest) {
        User user = authService.signup(signupRequest);

        SignupResponse response = SignupResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody @Validated RefreshTokenRequest refreshTokenRequest) {
        AuthResponse authResponse = authService.refreshToken(refreshTokenRequest.refreshToken());
        return ResponseEntity.ok().body(authResponse);
    }

}
