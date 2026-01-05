package io.github.bigpig.server.service;

import io.github.bigpig.server.entity.auth.User;
import io.github.bigpig.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User - " + username + " not found"));
    }

    @Override
    public boolean existsByUsername(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        return user != null;
    }

    @Override
    public boolean existsByEmail(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        return user != null;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> searchByUsername(String username) {
        return userRepository.findByUsernameStartingWithIgnoreCase(username);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }
}