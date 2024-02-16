package org.opm.busybeaver.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.opm.busybeaver.controller.ControllerInterfaces.GetUserFromBearerTokenInterface;
import org.opm.busybeaver.dto.Users.AuthenticatedUser;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.dto.Users.UsernameDto;
import org.opm.busybeaver.enums.BusyBeavConstants;
import org.opm.busybeaver.enums.BusyBeavPaths;
import org.opm.busybeaver.exceptions.Users.UsersExceptions;
import org.opm.busybeaver.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@ApiPrefixController
@RestController
@CrossOrigin
public final class UsersController implements GetUserFromBearerTokenInterface {

    private final UsersService usersService;
    private static final String USERS_PATH = BusyBeavPaths.Constants.USERS;
    private static final String REGISTER_PATH  = BusyBeavPaths.Constants.REGISTER;
    private static final String AUTH_PATH  = BusyBeavPaths.Constants.AUTH;

    @Autowired
    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @PostMapping(USERS_PATH + REGISTER_PATH)
    public AuthenticatedUser registerUser(
            @Valid @RequestBody UsernameDto usernameRegisterDto,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto,
            HttpServletResponse response
    ) throws UsersExceptions.UserAlreadyExistsException {

        userDto.setUsername(usernameRegisterDto.username());
        AuthenticatedUser newUser = usersService.registerUser(userDto);
        response.setStatus(HttpStatus.CREATED.value());

        return newUser;
    }

    @PostMapping(USERS_PATH + AUTH_PATH)
    public AuthenticatedUser authenticateUser(
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto)
    throws UsersExceptions.UserDoesNotExistException {

        return usersService.getUserByEmailAndId(userDto);
    }

    @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL)
    @Override
    public UserDto getUserFromToken(HttpServletRequest request) {
        return (UserDto) request.getAttribute(BusyBeavConstants.USER_KEY_VAL.getValue());
    }

}
