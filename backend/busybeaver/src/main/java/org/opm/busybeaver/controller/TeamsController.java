package org.opm.busybeaver.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.opm.busybeaver.dto.Teams.MembersInTeamDto;
import org.opm.busybeaver.dto.Teams.NewTeamDto;
import org.opm.busybeaver.dto.Teams.ProjectsByTeamDto;
import org.opm.busybeaver.dto.Teams.TeamsSummariesDto;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.enums.BusyBeavConstants;
import org.opm.busybeaver.enums.BusyBeavPaths;
import org.opm.busybeaver.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@ApiPrefixController
@RestController
@CrossOrigin
public class TeamsController {
    private final TeamService teamService;

    @Autowired
    public TeamsController(TeamService teamService) { this.teamService = teamService; }

    @GetMapping(BusyBeavPaths.Constants.TEAMS)
    public TeamsSummariesDto getUserHomePageTeams(
            HttpServletRequest request,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) {
        return teamService.getUserHomePageTeams(userDto, request.getContextPath());
    }

    @PostMapping(BusyBeavPaths.Constants.TEAMS)
    public NewTeamDto makeNewTeam(
            HttpServletRequest request,
            @Valid @RequestBody NewTeamDto newTeamOnlyTeamNameDto,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto,
            HttpServletResponse response
    ) {
        NewTeamDto newTeam = teamService.makeNewTeam(userDto, newTeamOnlyTeamNameDto, request.getContextPath());
        response.setHeader(BusyBeavConstants.LOCATION.getValue(), newTeam.getTeamLocation());
        return newTeam;
    }

    @GetMapping(BusyBeavPaths.Constants.TEAMS + "/{teamID}")
    public ProjectsByTeamDto getProjectsAssociatedWithTeam(
            HttpServletRequest request,
            @PathVariable Integer teamID,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) {
        return teamService.getProjectsAssociatedWithTeam(userDto, teamID, request.getContextPath());
    }

    @GetMapping(BusyBeavPaths.Constants.TEAMS + "/{teamID}" + BusyBeavPaths.Constants.MEMBERS)
    public MembersInTeamDto getMembersInTeam(
            HttpServletRequest request,
            @PathVariable Integer teamID,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) {
        return teamService.getMembersInTeam(userDto, teamID, request.getContextPath());
    }

    @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL)
    public UserDto user(HttpServletRequest request) {
        return (UserDto) request.getAttribute(BusyBeavConstants.USER_KEY_VAL.getValue());
    }
}
