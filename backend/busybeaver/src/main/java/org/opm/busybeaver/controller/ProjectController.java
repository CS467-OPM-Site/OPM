package org.opm.busybeaver.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.opm.busybeaver.dto.Projects.ProjectsSummariesDto;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.enums.BusyBeavConstants;
import org.opm.busybeaver.enums.BusyBeavPaths;
import org.opm.busybeaver.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;


@ApiPrefixController
@RestController
@CrossOrigin
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) { this.projectService = projectService; }

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
