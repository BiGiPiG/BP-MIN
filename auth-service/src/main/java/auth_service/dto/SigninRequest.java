package auth_service.dto;

public record SigninRequest(
        String username,
        String password
) {
}
