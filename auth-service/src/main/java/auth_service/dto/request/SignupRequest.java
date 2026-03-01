package auth_service.dto.request;

import jakarta.validation.constraints.*;

public record SignupRequest(
        @NotBlank(message = "Nickname is required")
        String nickname,

        @NotBlank(message = "Username is required")
        String username,

        @Email(message = "invalid email")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password length must be more than 6")
        String password
) {
}
