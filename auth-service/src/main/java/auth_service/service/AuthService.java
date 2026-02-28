package auth_service.service;

import auth_service.dto.RefreshTokenRequest;
import auth_service.dto.SigninRequest;
import auth_service.dto.SignupRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    public ResponseEntity<?> signin(SigninRequest signinRequest) {
        return ResponseEntity.ok().body("signin");
    }

    public ResponseEntity<?> signup(SignupRequest signupRequest) {
        return ResponseEntity.ok().body("signup");
    }

    public ResponseEntity<?> refreshToken(RefreshTokenRequest refreshTokenRequest) {
        return ResponseEntity.ok().body("refresh token");
    }
}
