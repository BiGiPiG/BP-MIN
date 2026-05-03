package io.github.bigpig.userservice.repositories;

import io.github.bigpig.userservice.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Profile findByUsername(String username);
    Profile findByUserId(Long userId);
    List<Profile> searchByUsernameStartingWith(String username);
}
