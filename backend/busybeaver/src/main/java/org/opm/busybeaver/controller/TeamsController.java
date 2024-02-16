package org.opm.busybeaver.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.opm.busybeaver.controller.ControllerInterfaces.GetUserFromBearerTokenInterface;
import org.opm.busybeaver.dto.SmallJsonResponse;
import org.opm.busybeaver.dto.Teams.MembersInTeamDto;
import org.opm.busybeaver.dto.Teams.NewTeamDto;
import org.opm.busybeaver.dto.Teams.ProjectsByTeamDto;
import org.opm.busybeaver.dto.Teams.TeamsSummariesDto;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.dto.Users.UsernameDto;
import org.opm.busybeaver.enums.BusyBeavConstants;
import org.opm.busybeaver.enums.BusyBeavPaths;
import org.opm.busybeaver.enums.SuccessMessageConstants;
import org.opm.busybeaver.service.TeamsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@ApiPrefixController
@RestController
@CrossOrigin
public final class TeamsController implements GetUserFromBearerTokenInterface {
    private final TeamsService teamsService;
    private static final String TEAMS_PATH = BusyBeavPaths.Constants.TEAMS;
    private static final String MEMBERS_PATH = BusyBeavPaths.Constants.MEMBERS;

    @Autowired
    public TeamsController(TeamsService teamsService) { this.teamsService = teamsService; }

    @GetMapping(TEAMS_PATH)
    public TeamsSummariesDto getUserHomePageTeams(
            HttpServletRequest request,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) {
        return teamsService.getUserHomePageTeams(userDto, request.getContextPath());
    }

    @PostMapping(TEAMS_PATH)
    public NewTeamDto makeNewTeam(
            HttpServletRequest request,
            @Valid @RequestBody NewTeamDto newTeamOnlyTeamNameDto,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto,
            HttpServletResponse response
    ) {
        NewTeamDto newTeam = teamsService.makeNewTeam(userDto, newTeamOnlyTeamNameDto, request.getContextPath());
        response.setHeader(BusyBeavConstants.LOCATION.getValue(), newTeam.getTeamLocation());
        response.setStatus(HttpStatus.CREATED.value());

        return newTeam;
    }

    @DeleteMapping(TEAMS_PATH + "/{teamID}")
    public SmallJsonResponse deleteTeam(
            @PathVariable int teamID,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) {
        teamsService.deleteTeam(userDto, teamID);
        return new SmallJsonResponse(
                HttpStatus.OK.value(),
                SuccessMessageConstants.TEAM_DELETED.getValue()
        );
    }

    @GetMapping(TEAMS_PATH + "/{teamID}")
    public ProjectsByTeamDto getProjectsAssociatedWithTeam(
            HttpServletRequest request,
            @PathVariable int teamID,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) {
        return teamsService.getProjectsAssociatedWithTeam(userDto, teamID, request.getContextPath());
    }

    @GetMapping(TEAMS_PATH + "/{teamID}" + MEMBERS_PATH)
    public MembersInTeamDto getMembersInTeam(
            HttpServletRequest request,
            @PathVariable int teamID,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) {
        return teamsService.getMembersInTeam(userDto, teamID, request.getContextPath());
    }

    @PostMapping(TEAMS_PATH + "/{teamID}" + MEMBERS_PATH)
    public SmallJsonResponse addMemberToTeam(
            HttpServletRequest request,
            @PathVariable int teamID,
            @Valid @RequestBody UsernameDto usernameToAdd,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto,
            HttpServletResponse response
    ) {
        String newUserInTeamLocation = teamsService.addMemberToTeam(userDto, teamID, usernameToAdd, request.getContextPath());
        response.setHeader(
                BusyBeavConstants.LOCATION.getValue(),
                newUserInTeamLocation
                );

        return new SmallJsonResponse(
                HttpStatus.OK.value(),
                SuccessMessageConstants.USER_ADDED.getValue()
        );
    }

    @DeleteMapping(TEAMS_PATH + "/{teamID}" + MEMBERS_PATH + "/{userID}")
    public SmallJsonResponse deleteMemberFromTeam(
            @PathVariable int userID,
            @PathVariable int teamID,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) {
       teamsService.removeMemberFromTeam(userDto, userID, teamID);
       return new SmallJsonResponse(
               HttpStatus.OK.value(),
               SuccessMessageConstants.TEAM_MEMBER_REMOVED.getValue()
       );
    }

    @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL)
    @Override
    public UserDto getUserFromToken(HttpServletRequest request) {
        return (UserDto) request.getAttribute(BusyBeavConstants.USER_KEY_VAL.getValue());
    }
}
