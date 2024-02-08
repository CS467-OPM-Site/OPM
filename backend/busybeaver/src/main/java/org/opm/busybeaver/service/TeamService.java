package org.opm.busybeaver.service;

import org.opm.busybeaver.dto.Teams.*;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.dto.Users.UsernameDto;
import org.opm.busybeaver.enums.BusyBeavPaths;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.exceptions.service.TeamAlreadyExistsForUserException;
import org.opm.busybeaver.exceptions.service.UserAlreadyInTeamException;
import org.opm.busybeaver.exceptions.service.UserDoesNotExistException;
import org.opm.busybeaver.exceptions.service.UserNotInTeamOrTeamDoesNotExistException;
import org.opm.busybeaver.jooq.tables.records.BeaverusersRecord;
import org.opm.busybeaver.jooq.tables.records.TeamsRecord;
import org.opm.busybeaver.repository.TeamRepository;
import org.opm.busybeaver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.opm.busybeaver.utils.Utils.verifyUserExistsAndReturn;

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
        BeaverusersRecord beaverusersRecord = verifyUserExistsAndReturn(userDto, userRepository);

        TeamsRecord newTeamRecord = teamRepository.makeNewTeam(beaverusersRecord.getUserId(), newTeamDto.getTeamName());

        newTeamDto.setTeamCreator(newTeamRecord.getTeamCreator());
        newTeamDto.setTeamID(newTeamRecord.getTeamId());
        newTeamDto.setTeamLocation(contextPath);
        newTeamDto.setTeamName(newTeamRecord.getTeamName());

        return newTeamDto;
    }

    public TeamsSummariesDto getUserHomePageTeams(UserDto userDto, String contextPath) throws UserDoesNotExistException {
        BeaverusersRecord beaverusersRecord = verifyUserExistsAndReturn(userDto, userRepository);

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
            String contextPath) throws UserNotInTeamOrTeamDoesNotExistException, UserDoesNotExistException {
        BeaverusersRecord beaverusersRecord = verifyUserExistsAndReturn(userDto, userRepository);

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
            String contextPath) throws UserNotInTeamOrTeamDoesNotExistException, UserDoesNotExistException {
        BeaverusersRecord beaverusersRecord = verifyUserExistsAndReturn(userDto, userRepository);

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

    public ProjectsByTeamDto getProjectsAssociatedWithTeam(
            UserDto userDto,
            Integer teamID,
            String contextPath) throws UserNotInTeamOrTeamDoesNotExistException {
        BeaverusersRecord beaverusersRecord = verifyUserExistsAndReturn(userDto, userRepository);

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
}
