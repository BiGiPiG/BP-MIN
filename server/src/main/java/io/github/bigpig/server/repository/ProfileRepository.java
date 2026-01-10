package io.github.bigpig.server.repository;

import io.github.bigpig.server.entity.user.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}
