package io.github.bigpig.userservice.utils;

import io.github.bigpig.userservice.dto.request.CreateProfileRequest;
import org.apache.avro.generic.GenericRecord;
import org.springframework.stereotype.Component;

@Component
public class GenericRecordConverter {

    public static CreateProfileRequest toCreateProfileRequest(GenericRecord genericRecord) {
        String nickname = genericRecord.get("nickname").toString();
        String username = genericRecord.get("username").toString();
        String email = genericRecord.get("email").toString();
        Long id = (Long) genericRecord.get("id");

        return CreateProfileRequest.builder()
                .nickname(nickname)
                .id(id)
                .username(username)
                .email(email)
                .build();
    }
}
