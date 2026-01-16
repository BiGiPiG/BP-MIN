package io.github.bigpig.server.service;

import io.github.bigpig.server.entity.user.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);
    List<User> searchByUsername(String nick);
    void save(User user);
}
