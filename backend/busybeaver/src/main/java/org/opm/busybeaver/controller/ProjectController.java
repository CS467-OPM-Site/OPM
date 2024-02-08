package org.opm.busybeaver.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.opm.busybeaver.dto.Projects.NewProjectDto;
import org.opm.busybeaver.dto.Projects.ProjectsSummariesDto;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.enums.BusyBeavConstants;
import org.opm.busybeaver.enums.BusyBeavPaths;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;


@ApiPrefixController
@RestController
@CrossOrigin
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) { this.projectService = projectService; }

    @PostMapping(BusyBeavPaths.Constants.PROJECTS)
    public NewProjectDto makeNewProject(
            HttpServletRequest request,
            @Valid @RequestBody NewProjectDto newProjectDto,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto,
            HttpServletResponse response
    ) {
        // Double-check an integer is passed as TeamID, or pass it back to the User
        try {
            String teamIDtoValidate = newProjectDto.getTeamID();
            Integer.parseInt(teamIDtoValidate);
        } catch (NumberFormatException e) {
            throw new HttpMessageNotReadableException(ErrorMessageConstants.INVALID_HTTP_REQUEST.getValue());
        }

        newProjectDto.setTeamIDInt(Integer.parseInt(newProjectDto.getTeamID()));

        NewProjectDto projectDto = projectService.makeNewProject(userDto, newProjectDto, request.getContextPath());
        response.setHeader(BusyBeavConstants.LOCATION.getValue(), projectDto.getProjectLocation());

        return projectDto;
    }

    @GetMapping(BusyBeavPaths.Constants.PROJECTS)
    public ProjectsSummariesDto getUserHomePageProjects(
            HttpServletRequest request,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) {
        return projectService.getUserHomePageProjects(userDto, request.getContextPath());
    }

    @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL)
    public UserDto user(HttpServletRequest request) {
        return (UserDto) request.getAttribute(BusyBeavConstants.USER_KEY_VAL.getValue());
    }
}
