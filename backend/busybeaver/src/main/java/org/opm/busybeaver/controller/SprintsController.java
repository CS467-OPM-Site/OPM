package org.opm.busybeaver.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.opm.busybeaver.controller.ControllerInterfaces.GetUserFromBearerTokenInterface;
import org.opm.busybeaver.dto.SmallJsonResponse;
import org.opm.busybeaver.dto.Sprints.*;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.enums.BusyBeavConstants;
import org.opm.busybeaver.enums.BusyBeavPaths;
import org.opm.busybeaver.enums.SuccessMessageConstants;
import org.opm.busybeaver.service.SprintsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@ApiPrefixController
@RestController
@CrossOrigin
@Slf4j
public final class SprintsController implements GetUserFromBearerTokenInterface {

    private final SprintsService sprintsService;
    private static final String PROJECTS_PATH = BusyBeavPaths.Constants.PROJECTS;
    private static final String SPRINT_PATH = BusyBeavPaths.Constants.SPRINTS;
    private static final String RID = BusyBeavConstants.REQUEST_ID.getValue();

    @Autowired
    public SprintsController(SprintsService sprintsService) { this.sprintsService = sprintsService; }

    @PostMapping(PROJECTS_PATH + "/{projectID}" + SPRINT_PATH)
    public @NotNull SprintSummaryDto addSprintToProject(
            @NotNull HttpServletRequest request,
            @PathVariable int projectID,
            @Valid @RequestBody NewSprintDto newSprintDto,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto,
            @NotNull HttpServletResponse response
    ) {

        SprintSummaryDto newSprint = sprintsService.addSprint(userDto, projectID, newSprintDto, request.getContextPath());
        response.setHeader(BusyBeavConstants.LOCATION.getValue(), newSprint.getSprintLocation());
        response.setStatus(HttpStatus.CREATED.value());
        log.info("Added sprint '{}' to a project. | RID: {}", newSprintDto.getSprintName(), request.getAttribute(RID));

        return newSprint;
    }

    @Contract("_, _, _, _ -> new")
    @DeleteMapping(PROJECTS_PATH + "/{projectID}" + SPRINT_PATH + "/{sprintID}")
    public @NotNull SmallJsonResponse deleteSprintFromProject(
            @NotNull HttpServletRequest request,
            @PathVariable int projectID,
            @PathVariable int sprintID,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) {
        sprintsService.removeSprintFromProject(userDto, projectID, sprintID);
        log.info("Removed sprint from a project. | RID: {}", request.getAttribute(RID));

        return new SmallJsonResponse(
                HttpStatus.OK.value(),
                SuccessMessageConstants.SPRINT_DELETED.getValue()
        );
    }

    @GetMapping(PROJECTS_PATH + "/{projectID}" + SPRINT_PATH)
    public @NotNull SprintsInProjectDto getAllSprintsForProject(
            @NotNull HttpServletRequest request,
            @PathVariable int projectID,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) {
        SprintsInProjectDto sprintsInProjectDto = sprintsService
                .getAllSprintsForProject(userDto, projectID, request.getContextPath());

        log.info("Retrieved all sprints for a project. | RID: {}", request.getAttribute(RID));

        return sprintsInProjectDto;
    }

    @GetMapping(PROJECTS_PATH + "/{projectID}" + SPRINT_PATH + "/{sprintID}")
    public @NotNull TasksInSprintDto getAllTasksInSprint(
            @NotNull HttpServletRequest request,
            @PathVariable int projectID,
            @PathVariable int sprintID,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) {
        TasksInSprintDto tasksInSprintDto = sprintsService
                .getAllTasksInSprint(userDto, projectID, sprintID, request.getContextPath());

        log.info("Retrieved all tasks for a sprint in a project. | RID: {}", request.getAttribute(RID));

        return tasksInSprintDto;
    }

    @Contract("_, _, _, _, _ -> new")
    @PutMapping(PROJECTS_PATH + "/{projectID}" + SPRINT_PATH + "/{sprintID}")
    public @NotNull SmallJsonResponse modifySprint(
            HttpServletRequest request,
            @PathVariable int projectID,
            @PathVariable int sprintID,
            @Valid @RequestBody EditSprintDto editSprintDto,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) {
        boolean sprintModified = sprintsService.modifySprint(userDto, projectID, sprintID, editSprintDto);
        if (sprintModified) {
            log.info("Sprint successfully modified. | RID: {}", request.getAttribute(RID));
            return new SmallJsonResponse(
                    HttpStatus.OK.value(),
                    SuccessMessageConstants.SPRINT_MODIFIED.getValue()
            );
        }
        log.info("Sprint was not modified, no changes found. | RID: {}", request.getAttribute(RID));
        return new SmallJsonResponse(
                HttpStatus.OK.value(),
                SuccessMessageConstants.SPRINT_NOT_MODIFIED.getValue()
        );
    }

    @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL)
    @Override
    public UserDto getUserFromToken(@NotNull HttpServletRequest request) {
        return (UserDto) request.getAttribute(BusyBeavConstants.USER_KEY_VAL.getValue());
    }
}
