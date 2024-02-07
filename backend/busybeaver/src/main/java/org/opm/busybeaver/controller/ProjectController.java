package org.opm.busybeaver.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.opm.busybeaver.dto.HomePageProjectsDto;
import org.opm.busybeaver.dto.UserDto;
import org.opm.busybeaver.enums.BusyBeavConstants;
import org.opm.busybeaver.enums.BusyBeavPaths;
import org.opm.busybeaver.service.FirebaseAuthenticationService;
import org.opm.busybeaver.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.opm.busybeaver.utils.Utils.parseToken;

@ApiPrefixController
@RestController
@CrossOrigin
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) { this.projectService = projectService; }

    @GetMapping(BusyBeavPaths.Constants.PROJECTS)
    public HomePageProjectsDto getUserHomePageProjects(HttpServletRequest request) {
        UserDto userDto = parseToken(
                (FirebaseAuthenticationService) request.getAttribute(BusyBeavConstants.USER_KEY_VAL.getValue())
        );

        return projectService.getUserHomePageProjects(userDto, request.getContextPath());
    }
}
