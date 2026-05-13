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

    public Map<Long, ParticipantInfo> fetchParticipantInfosMap(List<Long> participantIds) {
        if (participantIds.isEmpty()) {
            return Map.of();
        }

        try {
            List<ParticipantInfo> infos = fetchInfos(participantIds);
            if (infos == null) {
                return Map.of();
            }

            return infos.stream().collect(Collectors.toMap(ParticipantInfo::userId, Function.identity()));
        } catch (Exception e) {
            log.error("Error calling user-service", e);
            return Map.of();
        }
    }

    public List<ParticipantInfo> fetchParticipantInfosList(List<Long> participantIds) {
        if (participantIds.isEmpty()) {
            return List.of();
        }

        try {
            List<ParticipantInfo> infos = fetchInfos(participantIds);
            if (infos == null) {
                return List.of();
            }

            return infos;
        } catch (Exception e) {
            log.error("Error calling user-service", e);
            return List.of();
        }
    }

    private List<ParticipantInfo> fetchInfos(List<Long> participantIds) {
        List<ParticipantInfo> infos = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/users/participant-infos")
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
        }

        return infos;
    }
}
