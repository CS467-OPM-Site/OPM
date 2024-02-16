package org.opm.busybeaver.service;

import org.opm.busybeaver.dto.Projects.NewProjectDto;
import org.opm.busybeaver.dto.Projects.ProjectDetailsDto;
import org.opm.busybeaver.dto.Projects.ProjectSummaryDto;
import org.opm.busybeaver.dto.Projects.ProjectsSummariesDto;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.exceptions.Projects.ProjectsExceptions;
import org.opm.busybeaver.exceptions.Teams.TeamsExceptions;
import org.opm.busybeaver.exceptions.Users.UsersExceptions;
import org.opm.busybeaver.jooq.tables.records.BeaverusersRecord;
import org.opm.busybeaver.jooq.tables.records.ProjectsRecord;
import org.opm.busybeaver.repository.*;
import org.opm.busybeaver.service.ServiceInterfaces.ValidateUserAndProjectInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public final class ProjectService implements ValidateUserAndProjectInterface {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final ProjectUsersRepository projectUsersRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public ProjectService(
            ProjectRepository projectRepository,
            UserRepository userRepository,
            TeamRepository teamRepository,
            ProjectUsersRepository projectUsersRepository,
            TaskRepository taskRepository
    ) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.projectUsersRepository = projectUsersRepository;
        this.taskRepository = taskRepository;
    }

    public NewProjectDto makeNewProject(UserDto userDto, NewProjectDto newProjectDto, String contextPath)
            throws UsersExceptions.UserDoesNotExistException,
            TeamsExceptions.UserNotInTeamOrTeamDoesNotExistException,
            ProjectsExceptions.ProjectAlreadyExistsForTeamException {
        BeaverusersRecord beaverusersRecord = userRepository.verifyUserExistsAndReturn(userDto);

        if (!teamRepository.isUserInTeamAndDoesTeamExist(beaverusersRecord.getUserId(), newProjectDto.getTeamID())) {
            throw new TeamsExceptions.UserNotInTeamOrTeamDoesNotExistException(
                    ErrorMessageConstants.USER_NOT_IN_TEAM_OR_TEAM_NOT_EXIST.getValue());
        }

        ProjectsRecord newProject = projectRepository.makeNewProject(
                newProjectDto.getProjectName(),
                newProjectDto.getTeamID()
        );

        newProjectDto.setProjectID(newProject.getProjectId());
        newProjectDto.setLocations(contextPath);
        return newProjectDto;
    }

    public void deleteProject(UserDto userDto, int projectID) {
        // Validate user exists and in a valid project
        validateUserValidAndInsideValidProject(userDto, projectID);

        // Validate if tasks still exist in project
        boolean projectHasZeroTasksLeft = taskRepository.doesProjectHaveZeroTasks(projectID);

        if (!projectHasZeroTasksLeft) {
            throw new ProjectsExceptions.ProjectMustHaveZeroTasksBeforeDeletion(
                    ErrorMessageConstants.PROJECT_STILL_HAS_TASKS.getValue());
        }

        // Delete the project
        projectRepository.deleteProject(projectID);
    }

    public ProjectsSummariesDto getUserProjectsSummary(UserDto userDto, String contextPath)
            throws UsersExceptions.UserDoesNotExistException {
        BeaverusersRecord beaverusersRecord = userRepository.verifyUserExistsAndReturn(userDto);

        List<ProjectSummaryDto> projects =
                projectRepository.getUserProjectsSummary(beaverusersRecord.getUserId());

        projects.forEach( project -> project.setLocations(contextPath));

        return new ProjectsSummariesDto(projects);
    }

    public ProjectDetailsDto getSpecificProjectDetails(UserDto userDto, int projectID, String contextPath)
        throws UsersExceptions.UserDoesNotExistException, ProjectsExceptions.UserNotInProjectOrProjectDoesNotExistException {
        BeaverusersRecord beaverusersRecord = userRepository.verifyUserExistsAndReturn(userDto);

        if (!projectUsersRepository.isUserInProjectAndDoesProjectExist(beaverusersRecord.getUserId(), projectID)) {
            throw new ProjectsExceptions
                    .UserNotInProjectOrProjectDoesNotExistException(
                            ErrorMessageConstants.USER_NOT_IN_PROJECT_OR_PROJECT_NOT_EXIST.getValue()
            );
        }

        ProjectDetailsDto projectDetails = projectRepository.getSpecificProjectDetails(projectID);
        projectDetails.setLocations(contextPath);

        return projectDetails;
    }

    @Override
    public void validateUserValidAndInsideValidProject(UserDto userDto, int projectID) {
        BeaverusersRecord beaverusersRecord = userRepository.verifyUserExistsAndReturn(userDto);

        // Validate user in project and project exists
        if (!projectUsersRepository.isUserInProjectAndDoesProjectExist(beaverusersRecord.getUserId(), projectID)) {
            throw new ProjectsExceptions.UserNotInProjectOrProjectDoesNotExistException(
                    ErrorMessageConstants.USER_NOT_IN_PROJECT_OR_PROJECT_NOT_EXIST.getValue());
        }
    }
}
