package org.opm.busybeaver.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;


public class FirebaseAuthenticationService  {
    private String email = null;
    private String uid = null;
    private boolean isAuthenticated = false;
    private final String token;

    public FirebaseAuthenticationService(String token) {
       this.token = token;
       authenticate();
    }

    public String getEmail() {
        return email;
    }

    public String getUid() {
        return uid;
    }

    private void authenticate() {
        try {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseToken firebaseToken = firebaseAuth.verifyIdToken(token, true);

            email = firebaseToken.getEmail();
            uid = firebaseToken.getUid();
            isAuthenticated = true;

        } catch (FirebaseAuthException e) {
            isAuthenticated = false;
        }
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }
}
