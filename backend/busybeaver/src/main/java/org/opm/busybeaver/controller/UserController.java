package org.opm.busybeaver.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.opm.busybeaver.dto.Users.AuthenticatedUser;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.dto.Users.UsernameDto;
import org.opm.busybeaver.enums.BusyBeavConstants;
import org.opm.busybeaver.enums.BusyBeavPaths;
import org.opm.busybeaver.exceptions.Users.UserAlreadyExistsException;
import org.opm.busybeaver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


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
            @Valid @RequestBody UsernameDto usernameRegisterDto,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) throws UserAlreadyExistsException {

        userDto.setUsername(usernameRegisterDto.username());
        return userService.registerUser(userDto);
    }

    @PostMapping(BusyBeavPaths.Constants.USERS + BusyBeavPaths.Constants.AUTH)
    public AuthenticatedUser authenticateUser(
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto) {

        return userService.getUserByEmailAndId(userDto);
    }

    @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL)
    public UserDto user(HttpServletRequest request) {
        return (UserDto) request.getAttribute(BusyBeavConstants.USER_KEY_VAL.getValue());
    }

}
