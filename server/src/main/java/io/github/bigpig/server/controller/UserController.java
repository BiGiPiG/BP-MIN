package io.github.bigpig.server.controller;

import io.github.bigpig.server.dto.UserDto;
import io.github.bigpig.server.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/search")
    public List<UserDto> searchUsers(@RequestParam String searchTerm) {
        log.info("Searching for users with term: {}", searchTerm);
        return userService.searchByUsername(searchTerm).stream().map(UserDto::of).toList();
    }
}
