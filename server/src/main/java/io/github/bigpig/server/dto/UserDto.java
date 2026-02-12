package io.github.bigpig.server.dto;

import io.github.bigpig.server.entity.user.User;

public record UserDto(String username, String nickname) {
    public static UserDto from(User user) {
        return new UserDto(user.getUsername(), user.getNickname());
    }
}
