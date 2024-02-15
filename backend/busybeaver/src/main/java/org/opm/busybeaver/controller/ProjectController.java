package org.opm.busybeaver.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.opm.busybeaver.controller.ControllerInterfaces.GetUserFromBearerTokenInterface;
import org.opm.busybeaver.dto.Projects.NewProjectDto;
import org.opm.busybeaver.dto.Projects.ProjectDetailsDto;
import org.opm.busybeaver.dto.Projects.ProjectsSummariesDto;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.enums.BusyBeavConstants;
import org.opm.busybeaver.enums.BusyBeavPaths;
import org.opm.busybeaver.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@ApiPrefixController
@RestController
@CrossOrigin
public final class ProjectController implements GetUserFromBearerTokenInterface {

    private final ProjectService projectService;
    private static final String PROJECTS_PATH = BusyBeavPaths.Constants.PROJECTS;

    @Autowired
    public ProjectController(ProjectService projectService) { this.projectService = projectService; }

    @PostMapping(PROJECTS_PATH)
    public NewProjectDto makeNewProject(
            HttpServletRequest request,
            @Valid @RequestBody NewProjectDto newProjectDto,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto,
            HttpServletResponse response
    ) {
        NewProjectDto projectDto = projectService.makeNewProject(userDto, newProjectDto, request.getContextPath());
        response.setHeader(BusyBeavConstants.LOCATION.getValue(), projectDto.getProjectLocation());
        response.setStatus(HttpStatus.CREATED.value());

        return projectDto;
    }

    @GetMapping(PROJECTS_PATH)
    public ProjectsSummariesDto getUserProjectsSummary(
            HttpServletRequest request,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) {
        return projectService.getUserProjectsSummary(userDto, request.getContextPath());
    }

    @GetMapping(PROJECTS_PATH + "/{projectID}")
    public ProjectDetailsDto getSpecificProjectDetails(
            HttpServletRequest request,
            @PathVariable int projectID,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) {
        return projectService.getSpecificProjectDetails(userDto, projectID, request.getContextPath());
    }

    @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL)
    @Override
    public UserDto getUserFromToken(HttpServletRequest request) {
        return (UserDto) request.getAttribute(BusyBeavConstants.USER_KEY_VAL.getValue());
    }
}
