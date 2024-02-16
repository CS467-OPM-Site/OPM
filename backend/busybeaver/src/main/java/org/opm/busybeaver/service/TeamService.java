package org.opm.busybeaver.service;

import org.opm.busybeaver.dto.Teams.*;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.dto.Users.UsernameDto;
import org.opm.busybeaver.enums.BusyBeavPaths;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.exceptions.Teams.*;
import org.opm.busybeaver.exceptions.Users.UsersExceptions;
import org.opm.busybeaver.jooq.tables.records.BeaverusersRecord;
import org.opm.busybeaver.jooq.tables.records.TeamsRecord;
import org.opm.busybeaver.repository.ProjectRepository;
import org.opm.busybeaver.repository.TeamRepository;
import org.opm.busybeaver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TeamService {
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    @Autowired
    public TeamService(
            TeamRepository teamRepository,
            UserRepository userRepository,
            ProjectRepository projectRepository
    ) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    public NewTeamDto makeNewTeam(UserDto userDto, NewTeamDto newTeamDto, String contextPath)
            throws UsersExceptions.UserDoesNotExistException, TeamsExceptions.TeamAlreadyExistsForUserException  {
        BeaverusersRecord beaverusersRecord = userRepository.verifyUserExistsAndReturn(userDto);

        TeamsRecord newTeamRecord = teamRepository.makeNewTeam(beaverusersRecord.getUserId(), newTeamDto.getTeamName());

        newTeamDto.setTeamCreator(newTeamRecord.getTeamCreator());
        newTeamDto.setTeamID(newTeamRecord.getTeamId());
        newTeamDto.setLocations(contextPath);
        newTeamDto.setTeamName(newTeamRecord.getTeamName());

        return newTeamDto;
    }

    public void deleteTeam(UserDto userDto, Integer teamID)
            throws UsersExceptions.UserDoesNotExistException,
            TeamsExceptions.TeamDoesNotExistException,
            TeamsExceptions.UserNotTeamCreatorException,
            TeamsExceptions.TeamStillHasMembersException,
            TeamsExceptions.TeamStillHasProjectsException {
        BeaverusersRecord beaverusersRecord = userRepository.verifyUserExistsAndReturn(userDto);

        TeamsRecord teamToDelete = teamRepository.getSingleTeam(teamID);

        if (teamToDelete == null) {
            throw new TeamsExceptions.TeamDoesNotExistException(ErrorMessageConstants.TEAM_DOES_NOT_EXIST.getValue());
        }

        if (!teamToDelete.getTeamCreator().equals(beaverusersRecord.getUserId())) {
            throw new TeamsExceptions.UserNotTeamCreatorException(ErrorMessageConstants.USER_NOT_CREATOR_OF_TEAM.getValue());
        }

        if (teamRepository.doesTeamStillHaveMembers(teamID)) {
            throw new TeamsExceptions.TeamStillHasMembersException(ErrorMessageConstants.TEAM_STILL_HAS_MEMBERS.getValue());
        }

        if (projectRepository.doesTeamHaveProjectsAssociatedWithIt(teamID)) {
            throw new TeamsExceptions.TeamStillHasProjectsException(ErrorMessageConstants.TEAM_STILL_HAS_PROJECTS.getValue());
        }

        teamRepository.deleteSingleTeam(teamID);
    }

    public TeamsSummariesDto getUserHomePageTeams(UserDto userDto, String contextPath)
            throws UsersExceptions.UserDoesNotExistException {
        BeaverusersRecord beaverusersRecord = userRepository.verifyUserExistsAndReturn(userDto);

        List<TeamSummaryDto> teams =
                teamRepository.getUserHomePageTeams(beaverusersRecord.getUserId());

        teams.forEach( team -> {
            team.setLocations(contextPath);
            team.setIsTeamCreator(beaverusersRecord.getUserId());
        });

        return new TeamsSummariesDto(teams);
    }


    public ProjectsByTeamDto getProjectsAssociatedWithTeam(
            UserDto userDto,
            Integer teamID,
            String contextPath) throws UsersExceptions.UserDoesNotExistException,
            TeamsExceptions.UserNotInTeamOrTeamDoesNotExistException {
        BeaverusersRecord beaverusersRecord = userRepository.verifyUserExistsAndReturn(userDto);

        if (!teamRepository.isUserInTeamAndDoesTeamExist(beaverusersRecord.getUserId(), teamID)) {
            throw new TeamsExceptions.UserNotInTeamOrTeamDoesNotExistException(ErrorMessageConstants.USER_NOT_IN_TEAM_OR_TEAM_NOT_EXIST.getValue());
        }

        List<ProjectByTeamDto> homePageFilterProjectByTeamDtos =
                teamRepository.getAllProjectsAssociatedWithTeam(beaverusersRecord.getUserId(), teamID);

        ProjectsByTeamDto homePageFilterProjectsByTeamDto = new ProjectsByTeamDto(
                homePageFilterProjectByTeamDtos.getFirst().getTeamName(),
                homePageFilterProjectByTeamDtos.getFirst().getTeamID(),
                homePageFilterProjectByTeamDtos
        );

        homePageFilterProjectsByTeamDto.setLocations(contextPath);

        return homePageFilterProjectsByTeamDto;
    }

    public MembersInTeamDto getMembersInTeam(
            UserDto userDto,
            Integer teamID,
            String contextPath) throws UsersExceptions.UserDoesNotExistException,
            TeamsExceptions.UserNotInTeamOrTeamDoesNotExistException {
        BeaverusersRecord beaverusersRecord = userRepository.verifyUserExistsAndReturn(userDto);

        if (!teamRepository.isUserInTeamAndDoesTeamExist(beaverusersRecord.getUserId(), teamID)) {
            throw new TeamsExceptions.UserNotInTeamOrTeamDoesNotExistException(ErrorMessageConstants.USER_NOT_IN_TEAM_OR_TEAM_NOT_EXIST.getValue());
        }

        List<MemberInTeamDto> memberInTeamDtos = teamRepository.getAllMembersInTeam(teamID);

        MembersInTeamDto membersInTeamDto = new MembersInTeamDto(
                memberInTeamDtos.getFirst().getTeamName(),
                memberInTeamDtos.getFirst().getTeamID(),
                memberInTeamDtos
        );

        membersInTeamDto.setLocations(contextPath);

        return membersInTeamDto;
    }

    public String addMemberToTeam(UserDto userDto, Integer teamID, UsernameDto usernameToAdd, String contextPath)
            throws UsersExceptions.UserDoesNotExistException,
            TeamsExceptions.UserNotInTeamOrTeamDoesNotExistException,
            TeamsExceptions.UserAlreadyInTeamException {
        BeaverusersRecord beaverusersRecord = userRepository.verifyUserExistsAndReturn(userDto);

        if (!teamRepository.isUserInTeamAndDoesTeamExist(beaverusersRecord.getUserId(), teamID)) {
            throw new TeamsExceptions.UserNotInTeamOrTeamDoesNotExistException(ErrorMessageConstants.USER_NOT_IN_TEAM_OR_TEAM_NOT_EXIST.getValue());
        }

        BeaverusersRecord userToAdd = userRepository.verifyUserExistsAndReturn(usernameToAdd.username());

        teamRepository.addMemberToTeam(userToAdd, teamID);

        return contextPath + BusyBeavPaths.V1.getValue() + BusyBeavPaths.TEAMS.getValue() +
                "/" + teamID + BusyBeavPaths.MEMBERS.getValue() + "/" + userToAdd.getUserId();
    }

    public void removeMemberFromTeam(UserDto userDto, Integer userIDtoDelete, Integer teamID)
            throws UsersExceptions.UserDoesNotExistException {
        BeaverusersRecord beaverusersRecord = userRepository.verifyUserExistsAndReturn(userDto);

        if (!teamRepository.isUserInTeamAndDoesTeamExist(beaverusersRecord.getUserId(), teamID)) {
            throw new TeamsExceptions.UserNotInTeamOrTeamDoesNotExistException(ErrorMessageConstants.USER_NOT_IN_TEAM_OR_TEAM_NOT_EXIST.getValue());
        }

        // Check if user to delete is in team, but short circuit if user to delete is also the requesting user
        if (!beaverusersRecord.getUserId().equals(userIDtoDelete) && !teamRepository.isUserInTeamAndDoesTeamExist(userIDtoDelete, teamID)) {
            throw new TeamsExceptions.UserNotInTeamOrTeamDoesNotExistException(ErrorMessageConstants.USER_NOT_IN_TEAM_OR_TEAM_NOT_EXIST.getValue());
        }

        if (teamRepository.isUserCreatorOfTeam(userIDtoDelete, teamID)) {
            throw new TeamsExceptions.TeamCreatorCannotBeRemovedException(ErrorMessageConstants.TEAM_CREATOR_CANNOT_BE_REMOVED.getValue());
        }

       teamRepository.deleteSingleTeamMember(userIDtoDelete, teamID);
    }
}
