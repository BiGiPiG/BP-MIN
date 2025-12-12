package io.github.bigpig.server.dto;

import io.github.bigpig.server.entity.auth.User;

public record UserDto(String username, String email) {
    public static UserDto from(User user) {
        return new UserDto(user.getUsername(), user.getEmail());
    }
}
