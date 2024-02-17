package org.opm.busybeaver.service;

import org.opm.busybeaver.dto.Projects.NewProjectDto;
import org.opm.busybeaver.dto.Projects.ProjectDetailsDto;
import org.opm.busybeaver.dto.Projects.ProjectSummaryDto;
import org.opm.busybeaver.dto.Projects.ProjectsSummariesDto;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.exceptions.Projects.ProjectsExceptions;
import org.opm.busybeaver.jooq.tables.records.BeaverusersRecord;
import org.opm.busybeaver.jooq.tables.records.ProjectsRecord;
import org.opm.busybeaver.repository.*;
import org.opm.busybeaver.service.ServiceInterfaces.ValidateUserAndProjectInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public final class ProjectsService implements ValidateUserAndProjectInterface {

    private final ProjectsRepository projectsRepository;
    private final UsersRepository usersRepository;
    private final TeamsRepository teamsRepository;
    private final ProjectUsersRepository projectUsersRepository;
    private final TasksRepository tasksRepository;

    @Autowired
    public ProjectsService(
            ProjectsRepository projectsRepository,
            UsersRepository usersRepository,
            TeamsRepository teamsRepository,
            ProjectUsersRepository projectUsersRepository,
            TasksRepository tasksRepository
    ) {
        this.projectsRepository = projectsRepository;
        this.usersRepository = usersRepository;
        this.teamsRepository = teamsRepository;
        this.projectUsersRepository = projectUsersRepository;
        this.tasksRepository = tasksRepository;
    }

    public NewProjectDto makeNewProject(UserDto userDto, NewProjectDto newProjectDto, String contextPath) {
        BeaverusersRecord beaverusersRecord = usersRepository.getUserByEmailAndId(userDto);

        teamsRepository.isUserInTeamAndDoesTeamExist(beaverusersRecord.getUserId(), newProjectDto.getTeamID());

        ProjectsRecord newProject = projectsRepository.makeNewProject(
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
        boolean projectHasZeroTasksLeft = tasksRepository.doesProjectHaveZeroTasks(projectID);

        if (!projectHasZeroTasksLeft) {
            throw new ProjectsExceptions.ProjectMustHaveZeroTasksBeforeDeletion(
                    ErrorMessageConstants.PROJECT_STILL_HAS_TASKS.getValue());
        }

        // Delete the project
        projectsRepository.deleteProject(projectID);
    }

    public ProjectsSummariesDto getUserProjectsSummary(UserDto userDto, String contextPath) {
        BeaverusersRecord beaverusersRecord = usersRepository.getUserByEmailAndId(userDto);

        List<ProjectSummaryDto> projects =
                projectsRepository.getUserProjectsSummary(beaverusersRecord.getUserId());

        projects.forEach( project -> project.setLocations(contextPath));

        return new ProjectsSummariesDto(projects);
    }

    public ProjectDetailsDto getSpecificProjectDetails(UserDto userDto, int projectID, String contextPath) {
        BeaverusersRecord beaverusersRecord = usersRepository.getUserByEmailAndId(userDto);

        projectUsersRepository.isUserInProjectAndDoesProjectExist(beaverusersRecord.getUserId(), projectID);

        ProjectDetailsDto projectDetails = projectsRepository.getSpecificProjectDetails(projectID);
        projectDetails.setLocations(contextPath);

        return projectDetails;
    }

    @Override
    public BeaverusersRecord validateUserValidAndInsideValidProject(UserDto userDto, int projectID) {
        BeaverusersRecord beaverusersRecord = usersRepository.getUserByEmailAndId(userDto);

        // Validate user in project and project exists
        projectUsersRepository.isUserInProjectAndDoesProjectExist(beaverusersRecord.getUserId(), projectID);

        return beaverusersRecord;
    }
}
