package io.github.bigpig.server.controller;

import io.github.bigpig.server.dto.AuthenticationResponseDto;
import io.github.bigpig.server.dto.LoginRequestDto;
import io.github.bigpig.server.dto.RegistrationRequestDto;
import io.github.bigpig.server.service.AuthenticationService;
import io.github.bigpig.server.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> register(
            @RequestBody RegistrationRequestDto registrationDto) {

        if(userService.existsByUsername(registrationDto.getUsername())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username is already taken"));
        }

        if(userService.existsByEmail(registrationDto.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email is already taken"));
        }

        authenticationService.register(registrationDto);

        return ResponseEntity.ok(Map.of("message", "Registration was successful"));
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthenticationResponseDto> authenticate(
            @RequestBody LoginRequestDto request) {

        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/refresh_token")
    public ResponseEntity<AuthenticationResponseDto> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) {

        log.info("🔄 Refresh token request received");
        return authenticationService.refreshToken(request, response);
    }
}
