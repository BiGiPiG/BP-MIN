package io.github.bigpig.server.service;

import io.github.bigpig.server.dto.AuthenticationResponseDto;
import io.github.bigpig.server.dto.LoginRequestDto;
import io.github.bigpig.server.entity.Role;
import io.github.bigpig.server.entity.Token;
import io.github.bigpig.server.entity.User;
import io.github.bigpig.server.exceptions.AuthException;
import io.github.bigpig.server.exceptions.ErrorCode;
import io.github.bigpig.server.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import io.github.bigpig.server.dto.RegistrationRequestDto;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    private final UserService userService;

    public void signup(RegistrationRequestDto request) {
        if (userService.existsByUsername(request.getUsername())) {
            throw new AuthException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }

        if (userService.existsByEmail(request.getEmail())) {
            throw new AuthException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userService.save(user);
    }

    void deleteRefreshToken(User user) {
        tokenRepository.deleteByUserId(user.getId());
    }

    private void saveRefreshToken(String refreshToken, User user) {
        Token token = Token.builder()
                .refreshToken(refreshToken)
                .user(user)
                .build();
        tokenRepository.save(token);
    }

    @Transactional
    public AuthenticationResponseDto signin(LoginRequestDto request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        User user = userService.findByUsername(request.username()).orElseThrow();
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        deleteRefreshToken(user);
        saveRefreshToken(refreshToken, user);
        return new AuthenticationResponseDto(accessToken, refreshToken);
    }

    @Transactional
    public ResponseEntity<AuthenticationResponseDto> refreshToken(String refreshToken) {

        String username = jwtService.extractUsername(refreshToken);
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No user found"));

        if (jwtService.isValidRefresh(refreshToken, user)) {
            String newAccessToken = jwtService.generateAccessToken(user);
            String newRefreshToken = jwtService.generateRefreshToken(user);
            deleteRefreshToken(user);
            saveRefreshToken(refreshToken, user);
            return ResponseEntity.ok(
                new AuthenticationResponseDto(newAccessToken,
                newRefreshToken)
            );
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
