package io.github.bigpig.server.controller;

import io.github.bigpig.server.dto.RefreshTokenRequest;
import io.github.bigpig.server.dto.auth.AuthenticationResponseDto;
import io.github.bigpig.server.dto.auth.LoginRequestDto;
import io.github.bigpig.server.dto.auth.RegistrationRequestDto;
import io.github.bigpig.server.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody RegistrationRequestDto registrationDto) {
        log.info("Signup request received for user {}", registrationDto.nickname());
        authenticationService.signup(registrationDto);
        log.info("Registration for user {} was successful", registrationDto.nickname());
        return ResponseEntity.ok(Map.of("message", "Registration was successful"));
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthenticationResponseDto> signin(@RequestBody LoginRequestDto request) {
        log.info("Signin request received for user {}", request.username());
        return ResponseEntity.ok(authenticationService.signin(request));
    }

    @PostMapping("/refresh_token")
    public ResponseEntity<AuthenticationResponseDto> refreshToken(@RequestBody RefreshTokenRequest refreshToken) {
        log.info("Refresh token request received");
        return ResponseEntity.ok(authenticationService.refreshToken(refreshToken.refreshToken()));
    }
}
