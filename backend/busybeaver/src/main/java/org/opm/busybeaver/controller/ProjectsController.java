package org.opm.busybeaver.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.opm.busybeaver.controller.ControllerInterfaces.GetUserFromBearerTokenInterface;
import org.opm.busybeaver.dto.Projects.NewProjectDto;
import org.opm.busybeaver.dto.Projects.NewProjectNameDto;
import org.opm.busybeaver.dto.Projects.ProjectDetailsDto;
import org.opm.busybeaver.dto.Projects.ProjectsSummariesDto;
import org.opm.busybeaver.dto.SmallJsonResponse;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.enums.BusyBeavConstants;
import org.opm.busybeaver.enums.BusyBeavPaths;
import org.opm.busybeaver.enums.SuccessMessageConstants;
import org.opm.busybeaver.service.ProjectsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@ApiPrefixController
@RestController
@CrossOrigin
@Slf4j
public final class ProjectsController implements GetUserFromBearerTokenInterface {

    private final ProjectsService projectsService;
    private static final String PROJECTS_PATH = BusyBeavPaths.Constants.PROJECTS;
    private static final String RID = BusyBeavConstants.REQUEST_ID.getValue();

    @Autowired
    public ProjectsController(ProjectsService projectsService) { this.projectsService = projectsService; }

    @PostMapping(PROJECTS_PATH)
    public @NotNull NewProjectDto makeNewProject(
            @NotNull HttpServletRequest request,
            @Valid @RequestBody NewProjectDto newProjectDto,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto,
            @NotNull HttpServletResponse response
    ) {
        NewProjectDto projectDto = projectsService.makeNewProject(userDto, newProjectDto, request);
        response.setHeader(BusyBeavConstants.LOCATION.getValue(), projectDto.getProjectLocation());
        response.setStatus(HttpStatus.CREATED.value());

        log.info("Created a new project called {}. | RID: {}",
                newProjectDto.getProjectName(),
                request.getAttribute(RID));

        return projectDto;
    }

    @Contract("_, _, _ -> new")
    @DeleteMapping(PROJECTS_PATH + "/{projectID}")
    public @NotNull SmallJsonResponse deleteProject(
            @NotNull HttpServletRequest request,
            @PathVariable int projectID,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) {
        projectsService.deleteProject(userDto, projectID, request);
        log.info("Deleted a project. | RID: {}", request.getAttribute(RID));

        return new SmallJsonResponse(
                HttpStatus.OK.value(),
                SuccessMessageConstants.PROJECT_DELETED.getValue()
        );
    }

    @GetMapping(PROJECTS_PATH)
    public @NotNull ProjectsSummariesDto getUserProjectsSummary(
            @NotNull HttpServletRequest request,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) {
        ProjectsSummariesDto projectsSummariesDto = projectsService.getUserProjectsSummary(userDto, request);
        log.info("Retrieved user's project summaries. | RID {}", request.getAttribute(RID));

        return projectsSummariesDto;
    }

    @GetMapping(PROJECTS_PATH + "/{projectID}")
    public @NotNull ProjectDetailsDto getSpecificProjectDetails(
            @NotNull HttpServletRequest request,
            @PathVariable int projectID,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) {
        ProjectDetailsDto projectDetailsDto = projectsService.getSpecificProjectDetails(userDto, projectID, request);
        log.info("Retrieved project details. | RID: {}", request.getAttribute(RID));

        return projectDetailsDto;
    }

    @Contract("_, _, _, _ -> new")
    @PutMapping(PROJECTS_PATH +"/{projectID}")
    public @NotNull SmallJsonResponse modifyProjectName(
            @NotNull HttpServletRequest request,
            @PathVariable int projectID,
            @Valid @RequestBody NewProjectNameDto newProjectNameDto,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) {
        projectsService.modifyProjectName(userDto, projectID, newProjectNameDto, request);
        log.info("Modified a project name. | RID: {}", request.getAttribute(RID));

        return new SmallJsonResponse(
                HttpStatus.OK.value(),
                SuccessMessageConstants.PROJECT_NAME_MODIFIED.getValue()
        );
    }

    @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL)
    @Override
    public UserDto getUserFromToken(@NotNull HttpServletRequest request) {
        return (UserDto) request.getAttribute(BusyBeavConstants.USER_KEY_VAL.getValue());
    }
}
