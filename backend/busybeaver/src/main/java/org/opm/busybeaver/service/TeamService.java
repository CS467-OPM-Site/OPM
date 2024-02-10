package org.opm.busybeaver.service;

import org.opm.busybeaver.dto.Teams.*;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.dto.Users.UsernameDto;
import org.opm.busybeaver.enums.BusyBeavPaths;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.exceptions.Teams.*;
import org.opm.busybeaver.exceptions.Users.UserDoesNotExistException;
import org.opm.busybeaver.jooq.tables.records.BeaverusersRecord;
import org.opm.busybeaver.jooq.tables.records.TeamsRecord;
import org.opm.busybeaver.repository.TeamRepository;
import org.opm.busybeaver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TeamService {
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    @Autowired
    public TeamService(
            TeamRepository teamRepository,
            UserRepository userRepository
    ) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
    }

    public NewTeamDto makeNewTeam(UserDto userDto, NewTeamDto newTeamDto, String contextPath)
            throws TeamAlreadyExistsForUserException, UserDoesNotExistException {
        BeaverusersRecord beaverusersRecord = userRepository.verifyUserExistsAndReturn(userDto);

        TeamsRecord newTeamRecord = teamRepository.makeNewTeam(beaverusersRecord.getUserId(), newTeamDto.getTeamName());

        newTeamDto.setTeamCreator(newTeamRecord.getTeamCreator());
        newTeamDto.setTeamID(newTeamRecord.getTeamId());
        newTeamDto.setTeamLocation(contextPath);
        newTeamDto.setTeamName(newTeamRecord.getTeamName());

        return newTeamDto;
    }

    public void deleteTeam(UserDto userDto, Integer teamID)
            throws TeamDoesNotExistException,
            UserNotTeamCreatorException,
            TeamStillHasMembersException,
            UserDoesNotExistException {
        BeaverusersRecord beaverusersRecord = userRepository.verifyUserExistsAndReturn(userDto);

        TeamsRecord teamToDelete = teamRepository.getSingleTeam(teamID);

        if (teamToDelete == null) {
            throw new TeamDoesNotExistException(ErrorMessageConstants.TEAM_DOES_NOT_EXIST.getValue());
        }

        if (!teamToDelete.getTeamCreator().equals(beaverusersRecord.getUserId())) {
            throw new UserNotTeamCreatorException(ErrorMessageConstants.USER_NOT_CREATOR_OF_TEAM.getValue());
        }

        if (teamRepository.doesTeamStillHaveMembers(teamID)) {
            throw new TeamStillHasMembersException(ErrorMessageConstants.TEAM_STILL_HAS_MEMBERS.getValue());
        }

        teamRepository.deleteSingleTeam(teamID);
    }

    public TeamsSummariesDto getUserHomePageTeams(UserDto userDto, String contextPath) throws UserDoesNotExistException {
        BeaverusersRecord beaverusersRecord = userRepository.verifyUserExistsAndReturn(userDto);

        List<TeamSummaryDto> teams =
                teamRepository.getUserHomePageTeams(beaverusersRecord.getUserId());

        teams.forEach( team -> {
            team.setTeamLocation(contextPath);
            team.setIsTeamCreator(beaverusersRecord.getUserId());
        });

        return new TeamsSummariesDto(teams);
    }


    public ProjectsByTeamDto getProjectsAssociatedWithTeam(
            UserDto userDto,
            Integer teamID,
            String contextPath) throws UserNotInTeamOrTeamDoesNotExistException {
        BeaverusersRecord beaverusersRecord = userRepository.verifyUserExistsAndReturn(userDto);

        if (!teamRepository.isUserInTeamAndDoesTeamExist(beaverusersRecord.getUserId(), teamID)) {
            throw new UserNotInTeamOrTeamDoesNotExistException(ErrorMessageConstants.USER_NOT_IN_TEAM_OR_TEAM_NOT_EXIST.getValue());
        }

        List<ProjectByTeamDto> homePageFilterProjectByTeamDtos =
                teamRepository.getAllProjectsAssociatedWithTeam(beaverusersRecord.getUserId(), teamID);

        ProjectsByTeamDto homePageFilterProjectsByTeamDto = new ProjectsByTeamDto(
                homePageFilterProjectByTeamDtos.getFirst().getTeamName(),
                homePageFilterProjectByTeamDtos.getFirst().getTeamID(),
                homePageFilterProjectByTeamDtos
        );

        homePageFilterProjectsByTeamDto.setProjectAndTeamLocations(contextPath);

        return homePageFilterProjectsByTeamDto;
    }

    public MembersInTeamDto getMembersInTeam(
            UserDto userDto,
            Integer teamID,
            String contextPath) throws UserNotInTeamOrTeamDoesNotExistException {
        BeaverusersRecord beaverusersRecord = userRepository.verifyUserExistsAndReturn(userDto);

        if (!teamRepository.isUserInTeamAndDoesTeamExist(beaverusersRecord.getUserId(), teamID)) {
            throw new UserNotInTeamOrTeamDoesNotExistException(ErrorMessageConstants.USER_NOT_IN_TEAM_OR_TEAM_NOT_EXIST.getValue());
        }

        List<MemberInTeamDto> memberInTeamDtos = teamRepository.getAllMembersInTeam(teamID);

        MembersInTeamDto membersInTeamDto = new MembersInTeamDto(
                memberInTeamDtos.getFirst().getTeamName(),
                memberInTeamDtos.getFirst().getTeamID(),
                memberInTeamDtos
        );

        membersInTeamDto.setTeamLocation(contextPath);

        return membersInTeamDto;
    }

    public String addMemberToTeam(UserDto userDto, Integer teamID, UsernameDto usernameToAdd, String contextPath)
            throws UserNotInTeamOrTeamDoesNotExistException, UserDoesNotExistException, UserAlreadyInTeamException {
        BeaverusersRecord beaverusersRecord = userRepository.verifyUserExistsAndReturn(userDto);

        if (!teamRepository.isUserInTeamAndDoesTeamExist(beaverusersRecord.getUserId(), teamID)) {
            throw new UserNotInTeamOrTeamDoesNotExistException(ErrorMessageConstants.USER_NOT_IN_TEAM_OR_TEAM_NOT_EXIST.getValue());
        }

        BeaverusersRecord userToAdd = userRepository.verifyUserExistsAndReturn(usernameToAdd.username());

        teamRepository.addMemberToTeam(userToAdd, teamID);

        return contextPath + BusyBeavPaths.V1.getValue() + BusyBeavPaths.TEAMS.getValue() +
                "/" + teamID + BusyBeavPaths.MEMBERS.getValue() + "/" + userToAdd.getUserId();
    }

    public void removeMemberFromTeam(UserDto userDto, Integer userIDtoDelete, Integer teamID)
            throws UserDoesNotExistException {
        BeaverusersRecord beaverusersRecord = userRepository.verifyUserExistsAndReturn(userDto);

        if (!teamRepository.isUserInTeamAndDoesTeamExist(beaverusersRecord.getUserId(), teamID)) {
            throw new UserNotInTeamOrTeamDoesNotExistException(ErrorMessageConstants.USER_NOT_IN_TEAM_OR_TEAM_NOT_EXIST.getValue());
        }

        // Check if user to delete is in team, but short circuit if user to delete is also the requesting user
        if (!beaverusersRecord.getUserId().equals(userIDtoDelete) && !teamRepository.isUserInTeamAndDoesTeamExist(userIDtoDelete, teamID)) {
            throw new UserNotInTeamOrTeamDoesNotExistException(ErrorMessageConstants.USER_NOT_IN_TEAM_OR_TEAM_NOT_EXIST.getValue());
        }

        if (teamRepository.isUserCreatorOfTeam(userIDtoDelete, teamID)) {
            throw new TeamCreatorCannotBeRemovedException(ErrorMessageConstants.TEAM_CREATOR_CANNOT_BE_REMOVED.getValue());
        }

       teamRepository.deleteSingleTeamMember(userIDtoDelete, teamID);
    }
}
