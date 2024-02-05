package org.opm.busybeaver.dto;

public class UserDto {

    private String firebase_id;

    private String email;

    public String getEmail() {
        return email;
    }

    public String getFirebase_id() {
        return firebase_id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirebase_id(String firebase_id) {
        this.firebase_id = firebase_id;
    }

    public UserDto(String email, String firebase_id) {
        setEmail(email);
        setFirebase_id(firebase_id);
    }
}
