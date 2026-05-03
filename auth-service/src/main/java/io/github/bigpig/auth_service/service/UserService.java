package io.github.bigpig.auth_service.service;

import io.github.bigpig.auth_service.entity.User;
import io.github.bigpig.auth_service.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public boolean existsByUsername(@NonNull String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(@NonNull String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public User save(@NonNull User user) {
        return userRepository.save(user);
    }
}
