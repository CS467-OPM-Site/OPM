package org.opm.busybeaver.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.opm.busybeaver.controller.ControllerInterfaces.GetUserFromBearerTokenInterface;
import org.opm.busybeaver.dto.Sprints.NewSprintDto;
import org.opm.busybeaver.dto.Sprints.SprintSummaryDto;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.enums.BusyBeavConstants;
import org.opm.busybeaver.enums.BusyBeavPaths;
import org.opm.busybeaver.service.SprintsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@ApiPrefixController
@RestController
@CrossOrigin
public final class SprintsController implements GetUserFromBearerTokenInterface {

    private final SprintsService sprintsService;
    private static final String PROJECTS_PATH = BusyBeavPaths.Constants.PROJECTS;
    private static final String SPRINT_PATH = BusyBeavPaths.Constants.SPRINTS;

    @Autowired
    public SprintsController(SprintsService sprintsService) { this.sprintsService = sprintsService; }

    @PostMapping(PROJECTS_PATH + "/{projectID}" + SPRINT_PATH)
    public SprintSummaryDto addSprintToProject(
            HttpServletRequest request,
            @PathVariable int projectID,
            @Valid @RequestBody NewSprintDto newSprintDto,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto,
            HttpServletResponse response
    ) {

        SprintSummaryDto newSprint = sprintsService.addSprint(userDto, projectID, newSprintDto, request.getContextPath());
        response.setHeader(BusyBeavConstants.LOCATION.getValue(), newSprint.getSprintLocation());
        response.setStatus(HttpStatus.CREATED.value());

        return newSprint;
    }

    @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL)
    @Override
    public UserDto getUserFromToken(HttpServletRequest request) {
        return (UserDto) request.getAttribute(BusyBeavConstants.USER_KEY_VAL.getValue());
    }
}
