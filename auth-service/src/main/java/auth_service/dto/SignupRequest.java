package auth_service.dto;

public record SignupRequest(
        String nickname,
        String username,
        String email,
        String password
) {
}
