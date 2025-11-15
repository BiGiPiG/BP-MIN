package io.github.bigpig.server.repository;

import io.github.bigpig.server.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByRefreshToken(String Token);
    void deleteByUserId(long id);
}
