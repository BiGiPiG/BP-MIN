package auth_service.dto.request;

public record SigninRequest(
        String username,
        String password
) {
}
