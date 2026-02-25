package io.github.bigpig.server.util;

import io.github.bigpig.server.dto.chat.InterlocutorInfoDto;
import io.github.bigpig.server.entity.user.User;
import org.springframework.stereotype.Component;

@Component
public class InterlocutorInfoMapper {

    public InterlocutorInfoDto getInterlocutorInfo(User user, String status) {
        return InterlocutorInfoDto.builder()
                .nickname(user.getNickname())
                .username(user.getUsername())
                .bio(user.getProfile().getBio())
                .birthDate(user.getProfile().getBirthDate())
                .profileColor(user.getProfile().getProfileColor())
                .status(status).build();
    }
}
