package org.opm.busybeaver.dto.Users;

import jakarta.validation.constraints.*;

public record UsernameDto(
        @NotBlank(message = "Missing 'username' attribute")
        @Size(min = 3, max = 100, message = "Username must be 3 to 100 characters")
        String username
) {
    @Override
    public String username() {
        return username;
    }
}
