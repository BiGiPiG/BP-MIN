package io.github.bigpig.server.repository;

import io.github.bigpig.server.entity.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    @NonNull Optional<User> findById(@NonNull String id);
    List<User> findByUsernameStartingWithIgnoreCase(String name);
}
