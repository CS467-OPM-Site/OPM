package org.opm.busybeaver.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.opm.busybeaver.dto.AuthenticatedUser;
import org.opm.busybeaver.dto.UserDto;
import org.opm.busybeaver.dto.UserRegisterDto;
import org.opm.busybeaver.enums.BusyBeavConstants;
import org.opm.busybeaver.exceptions.service.UserAlreadyExistsException;
import org.opm.busybeaver.service.FirebaseAuthenticationService;
import org.opm.busybeaver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@ApiPrefixController
@RestController
@CrossOrigin
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users/register")
    public AuthenticatedUser registerUser(HttpServletRequest request, @Valid @RequestBody UserRegisterDto userRegisterDto) throws UserAlreadyExistsException {
        FirebaseAuthenticationService firebaseAuthenticationService =
                (FirebaseAuthenticationService) request.getAttribute(BusyBeavConstants.USER_KEY_VAL.getValue());

        userRegisterDto.setEmail(
                firebaseAuthenticationService.getEmail()
        );
        userRegisterDto.setFirebase_id(
                firebaseAuthenticationService.getUid()
        );

        userService.registerUser(userRegisterDto);

        return new AuthenticatedUser(userRegisterDto.getUsername(), BusyBeavConstants.SUCCESS.getValue());
    }

    @PostMapping("/users/auth")
    public AuthenticatedUser authenticateUser(HttpServletRequest request) {
        UserDto userDto = parseToken(
                (FirebaseAuthenticationService) request.getAttribute(BusyBeavConstants.USER_KEY_VAL.getValue())
        );

        return userService.getUserByEmailAndId(userDto);
    }

    private UserDto parseToken(FirebaseAuthenticationService firebaseAuthenticationService) {
        return new UserDto(
                firebaseAuthenticationService.getEmail(),
                firebaseAuthenticationService.getUid()
        );
    }
}
