package auth_service.controllers;

import auth_service.dto.request.RefreshTokenRequest;
import auth_service.dto.request.SigninRequest;
import auth_service.dto.request.SignupRequest;
import auth_service.dto.response.AuthResponse;
import auth_service.dto.response.SignupResponse;
import auth_service.entity.User;
import auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@RequestBody SigninRequest signinRequest) {
        AuthResponse authResponse = authService.signin(signinRequest);
        return ResponseEntity.ok().body(authResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody SignupRequest signupRequest) {
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
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        AuthResponse authResponse = authService.refreshToken(refreshTokenRequest.refreshToken());
        return ResponseEntity.ok().body(authResponse);
    }

}
