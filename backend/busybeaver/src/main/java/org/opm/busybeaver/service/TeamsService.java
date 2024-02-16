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
import org.opm.busybeaver.repository.ProjectsRepository;
import org.opm.busybeaver.repository.TeamsRepository;
import org.opm.busybeaver.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TeamsService {
    private final TeamsRepository teamsRepository;
    private final UsersRepository usersRepository;
    private final ProjectsRepository projectsRepository;

    @Autowired
    public TeamsService(
            TeamsRepository teamsRepository,
            UsersRepository usersRepository,
            ProjectsRepository projectsRepository
    ) {
        this.teamsRepository = teamsRepository;
        this.usersRepository = usersRepository;
        this.projectsRepository = projectsRepository;
    }

    public NewTeamDto makeNewTeam(UserDto userDto, NewTeamDto newTeamDto, String contextPath) {
        BeaverusersRecord beaverusersRecord = usersRepository.getUserByEmailAndId(userDto);

        TeamsRecord newTeamRecord = teamsRepository.makeNewTeam(beaverusersRecord.getUserId(), newTeamDto.getTeamName());

        newTeamDto.setTeamCreator(newTeamRecord.getTeamCreator());
        newTeamDto.setTeamID(newTeamRecord.getTeamId());
        newTeamDto.setLocations(contextPath);
        newTeamDto.setTeamName(newTeamRecord.getTeamName());

        return newTeamDto;
    }

    public void deleteTeam(UserDto userDto, Integer teamID) throws TeamsExceptions.TeamDoesNotExistException,
            TeamsExceptions.UserNotTeamCreatorException {
        BeaverusersRecord beaverusersRecord = usersRepository.getUserByEmailAndId(userDto);

        TeamsRecord teamToDelete = teamsRepository.getSingleTeam(teamID);

        if (teamToDelete == null) {
            throw new TeamsExceptions.TeamDoesNotExistException(ErrorMessageConstants.TEAM_DOES_NOT_EXIST.getValue());
        }

        if (!teamToDelete.getTeamCreator().equals(beaverusersRecord.getUserId())) {
            throw new TeamsExceptions.UserNotTeamCreatorException(ErrorMessageConstants.USER_NOT_CREATOR_OF_TEAM.getValue());
        }

        // Verify project has 1 team member left (user who is deleting), and no projects left associated with it
        teamsRepository.doesTeamStillHaveMembers(teamID);
        projectsRepository.doesTeamHaveProjectsAssociatedWithIt(teamID);

        teamsRepository.deleteSingleTeam(teamID);
    }

    public TeamsSummariesDto getUserHomePageTeams(UserDto userDto, String contextPath)
            throws UsersExceptions.UserDoesNotExistException {
        BeaverusersRecord beaverusersRecord = usersRepository.getUserByEmailAndId(userDto);

        List<TeamSummaryDto> teams =
                teamsRepository.getUserHomePageTeams(beaverusersRecord.getUserId());

        teams.forEach( team -> {
            team.setLocations(contextPath);
            team.setIsTeamCreator(beaverusersRecord.getUserId());
        });

        return new TeamsSummariesDto(teams);
    }


    public ProjectsByTeamDto getProjectsAssociatedWithTeam(UserDto userDto, Integer teamID, String contextPath) {
        BeaverusersRecord beaverusersRecord = usersRepository.getUserByEmailAndId(userDto);

        teamsRepository.isUserInTeamAndDoesTeamExist(beaverusersRecord.getUserId(), teamID);

        List<ProjectByTeamDto> homePageFilterProjectByTeamDtos =
                teamsRepository.getAllProjectsAssociatedWithTeam(beaverusersRecord.getUserId(), teamID);

        ProjectsByTeamDto homePageFilterProjectsByTeamDto = new ProjectsByTeamDto(
                homePageFilterProjectByTeamDtos.getFirst().getTeamName(),
                homePageFilterProjectByTeamDtos.getFirst().getTeamID(),
                homePageFilterProjectByTeamDtos
        );

        homePageFilterProjectsByTeamDto.setLocations(contextPath);

        return homePageFilterProjectsByTeamDto;
    }

    public MembersInTeamDto getMembersInTeam(UserDto userDto, Integer teamID, String contextPath) {
        BeaverusersRecord beaverusersRecord = usersRepository.getUserByEmailAndId(userDto);

        teamsRepository.isUserInTeamAndDoesTeamExist(beaverusersRecord.getUserId(), teamID);

        List<MemberInTeamDto> memberInTeamDtos = teamsRepository.getAllMembersInTeam(teamID);

        MembersInTeamDto membersInTeamDto = new MembersInTeamDto(
                memberInTeamDtos.getFirst().getTeamName(),
                memberInTeamDtos.getFirst().getTeamID(),
                memberInTeamDtos
        );

        membersInTeamDto.setLocations(contextPath);

        return membersInTeamDto;
    }

    public String addMemberToTeam(UserDto userDto, Integer teamID, UsernameDto usernameToAdd, String contextPath) {
        BeaverusersRecord beaverusersRecord = usersRepository.getUserByEmailAndId(userDto);

        teamsRepository.isUserInTeamAndDoesTeamExist(beaverusersRecord.getUserId(), teamID);

        BeaverusersRecord userToAdd = usersRepository.getUserByUsername(usernameToAdd.username());

        teamsRepository.addMemberToTeam(userToAdd, teamID);

        return contextPath + BusyBeavPaths.V1.getValue() + BusyBeavPaths.TEAMS.getValue() +
                "/" + teamID + BusyBeavPaths.MEMBERS.getValue() + "/" + userToAdd.getUserId();
    }

    public void removeMemberFromTeam(UserDto userDto, Integer userIDtoDelete, Integer teamID)
        throws TeamsExceptions.TeamCreatorCannotBeRemovedException {
        BeaverusersRecord beaverusersRecord = usersRepository.getUserByEmailAndId(userDto);

        teamsRepository.isUserInTeamAndDoesTeamExist(beaverusersRecord.getUserId(), teamID);

        // Check if user to delete is in team, but short circuit if user to delete is also the requesting user
        if (!beaverusersRecord.getUserId().equals(userIDtoDelete)) {
            teamsRepository.isUserInTeamAndDoesTeamExist(userIDtoDelete, teamID);
        }

        if (teamsRepository.isUserCreatorOfTeam(userIDtoDelete, teamID)) {
            throw new TeamsExceptions.TeamCreatorCannotBeRemovedException(ErrorMessageConstants.TEAM_CREATOR_CANNOT_BE_REMOVED.getValue());
        }

       teamsRepository.deleteSingleTeamMember(userIDtoDelete, teamID);
    }
}
