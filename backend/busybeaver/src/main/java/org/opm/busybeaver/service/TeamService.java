package org.opm.busybeaver.service;

import org.opm.busybeaver.dto.*;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.exceptions.service.UserDoesNotExistException;
import org.opm.busybeaver.exceptions.service.UserNotInTeamOrTeamDoesNotExistException;
import org.opm.busybeaver.jooq.tables.records.BeaverusersRecord;
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

    public HomePageTeamsDto getUserHomePageTeams(UserDto userDto, String contextPath) throws UserDoesNotExistException {
        BeaverusersRecord beaverusersRecord = verifyUserExistsAndReturn(userDto, userRepository);

        List<HomePageTeamDto> teams =
                teamRepository.getUserHomePageTeams(beaverusersRecord.getUserId());

        teams.forEach( team -> {
            team.setTeamLocation(contextPath);
            team.setIsTeamCreator(beaverusersRecord.getUserId());
        });

        return new HomePageTeamsDto(teams);
    }

    public HomePageFilterProjectsByTeamDto getProjectsAssociatedWithTeam(
            UserDto userDto,
            Integer teamID,
            String contextPath) throws UserNotInTeamOrTeamDoesNotExistException {
        BeaverusersRecord beaverusersRecord = verifyUserExistsAndReturn(userDto, userRepository);

        if (!teamRepository.isUserInTeamAndDoesTeamExist(beaverusersRecord.getUserId(), teamID)) {
            throw new UserNotInTeamOrTeamDoesNotExistException(ErrorMessageConstants.USER_NOT_IN_TEAM_OR_TEAM_NOT_EXIST.getValue());
        }

        List<HomePageFilterProjectByTeamDto> homePageFilterProjectByTeamDtos =
                teamRepository.getAllProjectsAssociatedWithTeam(beaverusersRecord.getUserId(), teamID);

        HomePageFilterProjectsByTeamDto homePageFilterProjectsByTeamDto = new HomePageFilterProjectsByTeamDto(
                homePageFilterProjectByTeamDtos.getFirst().getTeamName(),
                homePageFilterProjectByTeamDtos.getFirst().getTeamID(),
                homePageFilterProjectByTeamDtos
        );

        homePageFilterProjectsByTeamDto.setProjectAndTeamLocations(contextPath);

        return homePageFilterProjectsByTeamDto;
    }
}
