package auth_service.service;

import auth_service.dto.request.SigninRequest;
import auth_service.dto.request.SignupRequest;
import auth_service.dto.response.AuthResponse;
import auth_service.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthResponse signin(SigninRequest signinRequest) {
        Authentication authentication = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(signinRequest.username(), signinRequest.password())
                );

        User curUser = (User) authentication.getPrincipal();
        return generateTokens(curUser);
    }

    public User signup(SignupRequest signupRequest) {

        if (userService.existsByUsername(signupRequest.username())) {
            throw new RuntimeException("Username is already in use");
        }

        if (userService.existsByEmail(signupRequest.email())) {
            throw new RuntimeException("Email is already in use");
        }

        String encodedPassword = passwordEncoder.encode(signupRequest.password());

        User newUser = User.builder()
                .nickname(signupRequest.nickname())
                .username(signupRequest.username())
                .email(signupRequest.email())
                .password(encodedPassword)
                .build();

        return userService.save(newUser);
    }

    public AuthResponse refreshToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);

        if (username == null) {
            throw new RuntimeException("Invalid refresh token");
        }

        User curUser = (User) userDetailsService.loadUserByUsername(username);

        if (!jwtService.isValid(refreshToken, curUser)) {
            throw new RuntimeException("Invalid refresh token");
        }

        return generateTokens(curUser);
    }

    private AuthResponse generateTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return new AuthResponse(accessToken, refreshToken);
    }
}
