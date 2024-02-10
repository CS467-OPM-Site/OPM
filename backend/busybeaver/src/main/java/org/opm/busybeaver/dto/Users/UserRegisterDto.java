package org.opm.busybeaver.dto.Users;

import jakarta.validation.constraints.*;

public class UserRegisterDto {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 100, message = "Username must be 3 to 100 characters")
    private String username;

    private String firebase_id;

    private String email;

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFirebase_id() {
        return firebase_id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirebase_id(String firebase_id) {
        this.firebase_id = firebase_id;
    }
}
