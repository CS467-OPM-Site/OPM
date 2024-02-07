package org.opm.busybeaver.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.opm.busybeaver.dto.HomePageTeamsDto;
import org.opm.busybeaver.dto.UserDto;
import org.opm.busybeaver.enums.BusyBeavConstants;
import org.opm.busybeaver.enums.BusyBeavPaths;
import org.opm.busybeaver.service.FirebaseAuthenticationService;
import org.opm.busybeaver.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.opm.busybeaver.utils.Utils.parseToken;

@ApiPrefixController
@RestController
@CrossOrigin
public class TeamsController {
    private final TeamService teamService;

    @Autowired
    public TeamsController(TeamService teamService) { this.teamService = teamService; }

    @GetMapping(BusyBeavPaths.Constants.TEAMS)
    public HomePageTeamsDto getUserHomePageTeams(HttpServletRequest request) {
        UserDto userDto = parseToken(
                (FirebaseAuthenticationService) request.getAttribute(BusyBeavConstants.USER_KEY_VAL.getValue())
        );

        return teamService.getUserHomePageTeams(userDto, request.getContextPath());
    }
}
