package org.opm.busybeaver.service;

import org.opm.busybeaver.dto.Projects.NewProjectDto;
import org.opm.busybeaver.dto.Projects.ProjectDetailsDto;
import org.opm.busybeaver.dto.Projects.ProjectSummaryDto;
import org.opm.busybeaver.dto.Projects.ProjectsSummariesDto;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.exceptions.Projects.ProjectAlreadyExistsForTeamException;
import org.opm.busybeaver.exceptions.Projects.UserNotInProjectOrProjectDoesNotExistException;
import org.opm.busybeaver.exceptions.Users.UserDoesNotExistException;
import org.opm.busybeaver.exceptions.Teams.UserNotInTeamOrTeamDoesNotExistException;
import org.opm.busybeaver.jooq.tables.records.BeaverusersRecord;
import org.opm.busybeaver.jooq.tables.records.ProjectsRecord;
import org.opm.busybeaver.repository.ProjectRepository;
import org.opm.busybeaver.repository.TeamRepository;
import org.opm.busybeaver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    @Autowired
    public ProjectService(
            ProjectRepository projectRepository,
            UserRepository userRepository,
            TeamRepository teamRepository
    ) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
    }

    public NewProjectDto makeNewProject(UserDto userDto, NewProjectDto newProjectDto, String contextPath)
            throws UserDoesNotExistException,
            UserNotInTeamOrTeamDoesNotExistException,
            ProjectAlreadyExistsForTeamException {
        BeaverusersRecord beaverusersRecord = userRepository.verifyUserExistsAndReturn(userDto);

        if (!teamRepository.isUserInTeamAndDoesTeamExist(beaverusersRecord.getUserId(), newProjectDto.getTeamIDInt())) {
            throw new UserNotInTeamOrTeamDoesNotExistException(
                    ErrorMessageConstants.USER_NOT_IN_TEAM_OR_TEAM_NOT_EXIST.getValue());
        }

        ProjectsRecord newProject = projectRepository.makeNewProject(newProjectDto.getProjectName(), newProjectDto.getTeamIDInt(), beaverusersRecord.getUserId());

        newProjectDto.setProjectID(newProject.getProjectId());
        newProjectDto.setProjectLocation(contextPath);
        return newProjectDto;
    }

    public ProjectsSummariesDto getUserProjectsSummary(UserDto userDto, String contextPath) throws UserDoesNotExistException {
        BeaverusersRecord beaverusersRecord = userRepository.verifyUserExistsAndReturn(userDto);

        List<ProjectSummaryDto> projects =
                projectRepository.getUserProjectsSummary(beaverusersRecord.getUserId());

        projects.forEach( project -> project.setProjectAndTeamLocation(contextPath));

        return new ProjectsSummariesDto(projects);
    }

    public ProjectDetailsDto getSpecificProjectDetails(UserDto userDto, int projectID, String contextPath)
        throws UserNotInProjectOrProjectDoesNotExistException, UserDoesNotExistException {
        BeaverusersRecord beaverusersRecord = userRepository.verifyUserExistsAndReturn(userDto);

        if (!projectRepository.isUserInProjectAndDoesProjectExist(beaverusersRecord.getUserId(), projectID)) {
            throw new UserNotInProjectOrProjectDoesNotExistException(ErrorMessageConstants.USER_NOT_IN_PROJECT_OR_PROJECT_NOT_EXIST.getValue());
        }

        ProjectDetailsDto projectDetails = projectRepository.getSpecificProjectDetails(projectID);
        projectDetails.setProjectTeamColumnTaskLocation(contextPath);

        return projectDetails;
    }
}
