package org.opm.busybeaver.service;

import com.google.api.Http;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.opm.busybeaver.dto.Teams.*;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.dto.Users.UsernameDto;
import org.opm.busybeaver.enums.BusyBeavConstants;
import org.opm.busybeaver.enums.BusyBeavPaths;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.exceptions.Teams.*;
import org.opm.busybeaver.exceptions.Users.UsersExceptions;
import org.opm.busybeaver.jooq.tables.records.BeaverusersRecord;
import org.opm.busybeaver.jooq.tables.records.TeamsRecord;
import org.opm.busybeaver.repository.ProjectUsersRepository;
import org.opm.busybeaver.repository.ProjectsRepository;
import org.opm.busybeaver.repository.TeamsRepository;
import org.opm.busybeaver.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.http.HttpRequest;
import java.util.List;


@Service
@Slf4j
public class TeamsService {
    private final TeamsRepository teamsRepository;
    private final UsersRepository usersRepository;
    private final ProjectsRepository projectsRepository;
    private final ProjectUsersRepository projectUsersRepository;
    private static final String RID = BusyBeavConstants.REQUEST_ID.getValue();

    @Autowired
    public TeamsService(
            TeamsRepository teamsRepository,
            UsersRepository usersRepository,
            ProjectsRepository projectsRepository,
            ProjectUsersRepository projectUsersRepository
    ) {
        this.teamsRepository = teamsRepository;
        this.usersRepository = usersRepository;
        this.projectsRepository = projectsRepository;
        this.projectUsersRepository = projectUsersRepository;
    }

    public NewTeamDto makeNewTeam(UserDto userDto, @NotNull NewTeamDto newTeamDto, HttpServletRequest request) {
        BeaverusersRecord beaverusersRecord = usersRepository.getUserByEmailAndId(userDto, request);

        TeamsRecord newTeamRecord = teamsRepository.makeNewTeam(
                beaverusersRecord.getUserId(),
                newTeamDto.getTeamName(),
                request);

        newTeamDto.setTeamCreator(newTeamRecord.getTeamCreator());
        newTeamDto.setTeamID(newTeamRecord.getTeamId());
        newTeamDto.setLocations(request.getContextPath());
        newTeamDto.setTeamName(newTeamRecord.getTeamName());

        return newTeamDto;
    }

    public void deleteTeam(UserDto userDto, Integer teamID, HttpServletRequest request)
            throws TeamsExceptions.TeamDoesNotExistException,
            TeamsExceptions.UserNotTeamCreatorException {
        BeaverusersRecord beaverusersRecord = usersRepository.getUserByEmailAndId(userDto, request);

        TeamsRecord teamToDelete = teamsRepository.getSingleTeam(teamID);

        if (teamToDelete == null) {
            TeamsExceptions.TeamDoesNotExistException teamDoesNotExistException =
                    new TeamsExceptions.TeamDoesNotExistException(
                            ErrorMessageConstants.TEAM_DOES_NOT_EXIST.getValue());

            log.error("{}. | RID: {} {}",
                    ErrorMessageConstants.TEAM_DOES_NOT_EXIST.getValue(),
                    request.getAttribute(RID),
                    System.lineSeparator(),
                    teamDoesNotExistException);

            throw teamDoesNotExistException;
        }

        if (!teamToDelete.getTeamCreator().equals(beaverusersRecord.getUserId())) {
            TeamsExceptions.UserNotTeamCreatorException userNotTeamCreatorException =
                    new TeamsExceptions.UserNotTeamCreatorException(
                            ErrorMessageConstants.USER_NOT_CREATOR_OF_TEAM.getValue());

            log.error("{}. | RID: {} {}",
                    ErrorMessageConstants.USER_NOT_CREATOR_OF_TEAM.getValue(),
                    request.getAttribute(RID),
                    System.lineSeparator(),
                    userNotTeamCreatorException);

            throw userNotTeamCreatorException;
        }

        // Verify project has 1 team member left (user who is deleting), and no projects left associated with it
        teamsRepository.doesTeamStillHaveMembers(teamID, request);
        projectsRepository.doesTeamHaveProjectsAssociatedWithIt(teamID, request);

        teamsRepository.deleteSingleTeam(teamID);
    }

    public TeamsSummariesDto getUserHomePageTeams(UserDto userDto, HttpServletRequest request)
            throws UsersExceptions.UserDoesNotExistException {
        BeaverusersRecord beaverusersRecord = usersRepository.getUserByEmailAndId(userDto, request);

        List<TeamSummaryDto> teams =
                teamsRepository.getUserHomePageTeams(beaverusersRecord.getUserId());

        teams.forEach( team -> {
            team.setLocations(request.getContextPath());
            team.setIsTeamCreator(beaverusersRecord.getUserId());
        });

        return new TeamsSummariesDto(teams);
    }


    public ProjectsByTeamDto getProjectsAssociatedWithTeam(UserDto userDto, Integer teamID, HttpServletRequest request) {
        BeaverusersRecord beaverusersRecord = usersRepository.getUserByEmailAndId(userDto, request);

        teamsRepository.isUserInTeamAndDoesTeamExist(beaverusersRecord.getUserId(), teamID, request);

        List<ProjectByTeamDto> homePageFilterProjectByTeamDtos =
                teamsRepository.getAllProjectsAssociatedWithTeam(beaverusersRecord.getUserId(), teamID);

        ProjectsByTeamDto homePageFilterProjectsByTeamDto = new ProjectsByTeamDto(
                homePageFilterProjectByTeamDtos.getFirst().getTeamName(),
                homePageFilterProjectByTeamDtos.getFirst().getTeamID(),
                homePageFilterProjectByTeamDtos
        );

        homePageFilterProjectsByTeamDto.setLocations(request.getContextPath());

        return homePageFilterProjectsByTeamDto;
    }

    public MembersInTeamDto getMembersInTeam(UserDto userDto, Integer teamID, HttpServletRequest request) {
        BeaverusersRecord beaverusersRecord = usersRepository.getUserByEmailAndId(userDto, request);

        teamsRepository.isUserInTeamAndDoesTeamExist(beaverusersRecord.getUserId(), teamID, request);

        List<MemberInTeamDto> memberInTeamDtos = teamsRepository.getAllMembersInTeam(teamID);

        MembersInTeamDto membersInTeamDto = new MembersInTeamDto(
                memberInTeamDtos.getFirst().getTeamName(),
                memberInTeamDtos.getFirst().getTeamID(),
                memberInTeamDtos
        );

        membersInTeamDto.setLocations(request.getContextPath());

        return membersInTeamDto;
    }

    public String addMemberToTeam(UserDto userDto, Integer teamID, @NotNull UsernameDto usernameToAdd, HttpServletRequest request) {
        BeaverusersRecord beaverusersRecord = usersRepository.getUserByEmailAndId(userDto, request);

        teamsRepository.isUserInTeamAndDoesTeamExist(beaverusersRecord.getUserId(), teamID, request);

        BeaverusersRecord userToAdd = usersRepository.getUserByUsername(usernameToAdd.username(), request);

        teamsRepository.addMemberToTeam(userToAdd, teamID, request);

        // Add user to all projects of that team
        projectUsersRepository.addUserToAllProjectsOfTeam(userToAdd.getUserId(), teamID);

        return request.getContextPath() + BusyBeavPaths.V1.getValue() + BusyBeavPaths.TEAMS.getValue() +
                "/" + teamID + BusyBeavPaths.MEMBERS.getValue() + "/" + userToAdd.getUserId();
    }

    public void removeMemberFromTeam(UserDto userDto, Integer userIDtoDelete, Integer teamID, HttpServletRequest request)
        throws TeamsExceptions.TeamCreatorCannotBeRemovedException {
        BeaverusersRecord beaverusersRecord = usersRepository.getUserByEmailAndId(userDto, request);

        teamsRepository.isUserInTeamAndDoesTeamExist(beaverusersRecord.getUserId(), teamID, request);

        // Check if user to delete is in team, but short circuit if user to delete is also the requesting user
        if (!beaverusersRecord.getUserId().equals(userIDtoDelete)) {
            teamsRepository.isUserInTeamAndDoesTeamExist(userIDtoDelete, teamID, request);
        }

        if (teamsRepository.isUserCreatorOfTeam(userIDtoDelete, teamID)) {
            TeamsExceptions.TeamCreatorCannotBeRemovedException teamCreatorCannotBeRemovedException =
                    new TeamsExceptions.TeamCreatorCannotBeRemovedException(
                            ErrorMessageConstants.TEAM_CREATOR_CANNOT_BE_REMOVED.getValue());

            log.error("{}. | RID: {} {}",
                    ErrorMessageConstants.TEAM_CREATOR_CANNOT_BE_REMOVED.getValue(),
                    request.getAttribute(RID),
                    System.lineSeparator(),
                    teamCreatorCannotBeRemovedException);

            throw teamCreatorCannotBeRemovedException;
        }

       teamsRepository.deleteSingleTeamMember(userIDtoDelete, teamID);
    }
}
