package auth_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SigninRequest(
        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password length must be more than 6")
        String password
) {
}
