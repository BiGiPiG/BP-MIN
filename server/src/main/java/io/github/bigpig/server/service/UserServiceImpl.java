package io.github.bigpig.server.service;

import io.github.bigpig.server.entity.user.User;
import io.github.bigpig.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Optional<User> findById(long id) {
        return userRepository.findById(id);
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