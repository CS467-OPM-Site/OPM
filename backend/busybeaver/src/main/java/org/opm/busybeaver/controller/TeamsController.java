package org.opm.busybeaver.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
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
@Slf4j
public final class TeamsController implements GetUserFromBearerTokenInterface {
    private final TeamsService teamsService;
    private static final String TEAMS_PATH = BusyBeavPaths.Constants.TEAMS;
    private static final String MEMBERS_PATH = BusyBeavPaths.Constants.MEMBERS;
    private static final String RID = BusyBeavConstants.REQUEST_ID.getValue();

    @Autowired
    public TeamsController(TeamsService teamsService) { this.teamsService = teamsService; }

    @GetMapping(TEAMS_PATH)
    public TeamsSummariesDto getUserHomePageTeams(
            @NotNull HttpServletRequest request,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) {
        TeamsSummariesDto teamsSummariesDto = teamsService.getUserHomePageTeams(userDto, request);
        log.info("Retrieved user's team summaries. | RID: {}", request.getAttribute(RID));

        return teamsSummariesDto;
    }

    @PostMapping(TEAMS_PATH)
    public @NotNull NewTeamDto makeNewTeam(
            @NotNull HttpServletRequest request,
            @Valid @RequestBody NewTeamDto newTeamOnlyTeamNameDto,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto,
            @NotNull HttpServletResponse response
    ) {
        NewTeamDto newTeam = teamsService.makeNewTeam(userDto, newTeamOnlyTeamNameDto, request);
        response.setHeader(BusyBeavConstants.LOCATION.getValue(), newTeam.getTeamLocation());
        response.setStatus(HttpStatus.CREATED.value());
        log.info("Created a new team with team name '{}'. | RID: {}",
                newTeamOnlyTeamNameDto.getTeamName(),
                request.getAttribute(RID));

        return newTeam;
    }

    @Contract("_, _, _ -> new")
    @DeleteMapping(TEAMS_PATH + "/{teamID}")
    public @NotNull SmallJsonResponse deleteTeam(
            @NotNull HttpServletRequest request,
            @PathVariable int teamID,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) {
        teamsService.deleteTeam(userDto, teamID, request);
        log.info("Deleted a team. | RID: {}", request.getAttribute(RID));

        return new SmallJsonResponse(
                HttpStatus.OK.value(),
                SuccessMessageConstants.TEAM_DELETED.getValue()
        );
    }

    @GetMapping(TEAMS_PATH + "/{teamID}")
    public ProjectsByTeamDto getProjectsAssociatedWithTeam(
            @NotNull HttpServletRequest request,
            @PathVariable int teamID,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) {
        ProjectsByTeamDto projectsByTeamDto = teamsService.getProjectsAssociatedWithTeam(userDto, teamID, request);
        log.info("Successfully retrieved all projects for a team. | RID: {}", request.getAttribute(RID));

        return projectsByTeamDto;
    }

    @GetMapping(TEAMS_PATH + "/{teamID}" + MEMBERS_PATH)
    public MembersInTeamDto getMembersInTeam(
            @NotNull HttpServletRequest request,
            @PathVariable int teamID,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) {
        MembersInTeamDto membersInTeamDto = teamsService.getMembersInTeam(userDto, teamID, request);
        log.info("Successfully retrieved all members for a team. | RID: {}", request.getAttribute(RID));

        return membersInTeamDto;
    }

    @Contract("_, _, _, _, _ -> new")
    @PostMapping(TEAMS_PATH + "/{teamID}" + MEMBERS_PATH)
    public @NotNull SmallJsonResponse addMemberToTeam(
            @NotNull HttpServletRequest request,
            @PathVariable int teamID,
            @Valid @RequestBody UsernameDto usernameToAdd,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto,
            @NotNull HttpServletResponse response
    ) {
        String newUserInTeamLocation = teamsService.addMemberToTeam(userDto, teamID, usernameToAdd, request);
        response.setHeader(
                BusyBeavConstants.LOCATION.getValue(),
                newUserInTeamLocation
                );

        log.info("Added a member to a team. | RID: {}", request.getAttribute(RID));

        return new SmallJsonResponse(
                HttpStatus.OK.value(),
                SuccessMessageConstants.USER_ADDED.getValue()
        );
    }

    @Contract("_, _, _, _ -> new")
    @DeleteMapping(TEAMS_PATH + "/{teamID}" + MEMBERS_PATH + "/{userID}")
    public @NotNull SmallJsonResponse deleteMemberFromTeam(
            @NotNull HttpServletRequest request,
            @PathVariable int userID,
            @PathVariable int teamID,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) {
       teamsService.removeMemberFromTeam(userDto, userID, teamID, request);

       log.info("Removed a member from a team. | RID: {}", request.getAttribute(RID));
       return new SmallJsonResponse(
               HttpStatus.OK.value(),
               SuccessMessageConstants.TEAM_MEMBER_REMOVED.getValue()
       );
    }

    @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL)
    @Override
    public UserDto getUserFromToken(@NotNull HttpServletRequest request) {
        return (UserDto) request.getAttribute(BusyBeavConstants.USER_KEY_VAL.getValue());
    }
}
