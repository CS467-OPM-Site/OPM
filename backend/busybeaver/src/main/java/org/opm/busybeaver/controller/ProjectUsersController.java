package org.opm.busybeaver.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.opm.busybeaver.controller.ControllerInterfaces.GetUserFromBearerTokenInterface;
import org.opm.busybeaver.dto.SmallJsonResponse;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.dto.Users.UsernameDto;
import org.opm.busybeaver.enums.BusyBeavConstants;
import org.opm.busybeaver.enums.BusyBeavPaths;
import org.opm.busybeaver.enums.SuccessMessageConstants;
import org.opm.busybeaver.service.ProjectUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@ApiPrefixController
@RestController
@CrossOrigin
public class ProjectUsersController implements GetUserFromBearerTokenInterface {

    private final ProjectUsersService projectUsersService;
    private static final String PROJECT_PATH = BusyBeavPaths.Constants.PROJECTS;
    private static final String USERS_PATH = BusyBeavPaths.Constants.USERS;

    @Autowired
    public ProjectUsersController(ProjectUsersService projectUsersService) {
        this.projectUsersService = projectUsersService;
    }

    @PostMapping(PROJECT_PATH + "/{projectID}" + USERS_PATH)
    public SmallJsonResponse addUserToProject(
            @PathVariable int projectID,
            @Valid @RequestBody UsernameDto usernameDto,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) {
        projectUsersService.addUserToProject(userDto, projectID, usernameDto);

        return new SmallJsonResponse(
                HttpStatus.OK.value(),
                usernameDto.username() + SuccessMessageConstants.USER_WAS_ADDED_TO_PROJECT.getValue()
        );
    }

    @DeleteMapping(PROJECT_PATH + "/{projectID}" + USERS_PATH)
    public SmallJsonResponse removeUserFromProject(
            @PathVariable int projectID,
            @Valid @RequestBody UsernameDto usernameDto,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) {
        projectUsersService.removeUserFromProject(userDto, projectID, usernameDto);

        return new SmallJsonResponse(
                HttpStatus.OK.value(),
                usernameDto.username() + SuccessMessageConstants.USER_WAS_REMOVED_FROM_PROJECT.getValue()
        );
    }

    @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL)
    @Override
    public UserDto getUserFromToken(HttpServletRequest request) {
        return (UserDto) request.getAttribute(BusyBeavConstants.USER_KEY_VAL.getValue());
    }
}
