package io.github.bigpig.chatservice.service;

import io.github.bigpig.chatservice.dto.ParticipantInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceClient {

    private final RestClient restClient;

    public Map<Long, ParticipantInfo> fetchParticipantInfos(List<Long> participantIds) {
        if (participantIds.isEmpty()) {
            return Map.of();
        }

        try {
            List<ParticipantInfo> infos = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/users")
                            .queryParam("userIds", participantIds)
                            .build())
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        log.warn("Failed to fetch users: {}", res.getStatusCode());
                        throw new RuntimeException("User service error");
                    })
                    .body(new ParameterizedTypeReference<>() {});

            if (infos == null) {
                log.warn("User service returned null");
                return Map.of();
            }

            return infos.stream().collect(Collectors.toMap(ParticipantInfo::userId, Function.identity()));
        } catch (Exception e) {
            log.error("Error calling user-service", e);
            return Map.of();
        }
    }
}
