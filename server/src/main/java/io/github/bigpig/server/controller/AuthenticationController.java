package io.github.bigpig.server.controller;

import io.github.bigpig.server.dto.AuthenticationResponseDto;
import io.github.bigpig.server.dto.LoginRequestDto;
import io.github.bigpig.server.dto.RefreshTokenDto;
import io.github.bigpig.server.dto.RegistrationRequestDto;
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
        log.info("Signup request received for user {}", registrationDto.getUsername());
        authenticationService.signup(registrationDto);
        return ResponseEntity.ok(Map.of("message", "Registration was successful"));
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthenticationResponseDto> signin(@RequestBody LoginRequestDto request) {
        log.info("Signin request received for user {}", request.username());
        return ResponseEntity.ok(authenticationService.signin(request));
    }

    @PostMapping("/refresh_token")
    public ResponseEntity<AuthenticationResponseDto> refreshToken(@RequestBody RefreshTokenDto refreshToken) {
        log.info("Refresh token request received");
        return authenticationService.refreshToken(refreshToken.refreshToken());
    }
}
