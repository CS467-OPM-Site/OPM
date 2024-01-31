package org.opm.busybeaver.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.opm.busybeaver.dto.AuthenticatedUser;
import org.opm.busybeaver.enums.BusyBeavConstants;
import org.opm.busybeaver.service.FirebaseAuthenticationService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@ApiPrefixController
@RestController
@CrossOrigin
public class UserController {

    @PostMapping("/users/register")
    public AuthenticatedUser registerUser(HttpServletRequest request) {
        // Verify Authentication: Bearer token
        // Verify contains payload with Username in it
        // Send to service layer
        //      Verify email not in database, username unique
        FirebaseAuthenticationService firebaseAuthenticationService =
                (FirebaseAuthenticationService) request.getAttribute(BusyBeavConstants.USER_KEY_VAL.getValue());
        String email = firebaseAuthenticationService.getEmail();
        String uid = firebaseAuthenticationService.getUid();
        return new AuthenticatedUser(email, uid);
    }

    @PostMapping("/users/auth")
    public AuthenticatedUser authenticateUser() {
        // Verify Authentication: Bearer token
        // Send to service layer
        // Verify email or UID contained in database
        //      If not - send response indicating need to register
        //      Else - 200 with json containing username
        return new AuthenticatedUser("BillJoe", "Success");
    }
}
