package org.opm.busybeaver.dto.Users;

public class UserDto {

    private String username;
    private String firebase_id;
    private String email;

    public UserDto(String email, String firebase_id) {
        setEmail(email);
        setFirebase_id(firebase_id);
    }

    public String getEmail() {
        return email;
    }

    public String getFirebase_id() {
        return firebase_id;
    }

    public String getUsername() {
        return username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirebase_id(String firebase_id) {
        this.firebase_id = firebase_id;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
