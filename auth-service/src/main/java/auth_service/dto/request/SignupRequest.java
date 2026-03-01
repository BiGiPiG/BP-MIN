package auth_service.dto.request;

public record SignupRequest(
        String nickname,
        String username,
        String email,
        String password
) {
}
