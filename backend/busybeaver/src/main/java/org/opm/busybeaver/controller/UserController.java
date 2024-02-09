package org.opm.busybeaver.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.opm.busybeaver.dto.Users.AuthenticatedUser;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.dto.Users.UserRegisterDto;
import org.opm.busybeaver.enums.BusyBeavConstants;
import org.opm.busybeaver.enums.BusyBeavPaths;
import org.opm.busybeaver.exceptions.service.UserAlreadyExistsException;
import org.opm.busybeaver.service.FirebaseAuthenticationService;
import org.opm.busybeaver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.opm.busybeaver.utils.Utils.parseToken;

@ApiPrefixController
@RestController
@CrossOrigin
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(BusyBeavPaths.Constants.USERS + BusyBeavPaths.Constants.REGISTER)
    public AuthenticatedUser registerUser(
            HttpServletRequest request,
            @Valid @RequestBody UserRegisterDto userRegisterDto
    ) throws UserAlreadyExistsException {

        FirebaseAuthenticationService firebaseAuthenticationService =
                (FirebaseAuthenticationService) request.getAttribute(BusyBeavConstants.USER_KEY_VAL.getValue());

        userRegisterDto.setEmail(firebaseAuthenticationService.getEmail());
        userRegisterDto.setFirebase_id(firebaseAuthenticationService.getUid());

        userService.registerUser(userRegisterDto);

        return new AuthenticatedUser(userRegisterDto.getUsername(), BusyBeavConstants.SUCCESS.getValue());
    }

    @PostMapping(BusyBeavPaths.Constants.USERS + BusyBeavPaths.Constants.AUTH)
    public AuthenticatedUser authenticateUser(HttpServletRequest request) {
        UserDto userDto = parseToken(
                (FirebaseAuthenticationService) request.getAttribute(BusyBeavConstants.USER_KEY_VAL.getValue())
        );

        return userService.getUserByEmailAndId(userDto);
    }

}
