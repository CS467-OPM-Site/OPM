package org.opm.busybeaver.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.opm.busybeaver.dto.Projects.*;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.enums.BusyBeavConstants;
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
@Slf4j
public final class ProjectsService implements ValidateUserAndProjectInterface {

    private final ProjectsRepository projectsRepository;
    private final UsersRepository usersRepository;
    private final TeamsRepository teamsRepository;
    private final ProjectUsersRepository projectUsersRepository;
    private final TasksRepository tasksRepository;
    private static final String RID = BusyBeavConstants.REQUEST_ID.getValue();

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

    @Contract("_, _, _ -> param2")
    public @NotNull NewProjectDto makeNewProject(
            UserDto userDto,
            @NotNull NewProjectDto newProjectDto,
            HttpServletRequest request) {
        BeaverusersRecord beaverusersRecord = usersRepository.getUserByEmailAndId(userDto, request);

        teamsRepository.isUserInTeamAndDoesTeamExist(beaverusersRecord.getUserId(), newProjectDto.getTeamID(), request);

        ProjectsRecord newProject = projectsRepository.makeNewProject(
                newProjectDto.getProjectName(),
                newProjectDto.getTeamID(),
                request
        );

        newProjectDto.setProjectID(newProject.getProjectId());
        newProjectDto.setLocations(request.getContextPath());
        return newProjectDto;
    }

    public void deleteProject(UserDto userDto, int projectID, HttpServletRequest request) {
        // Validate user exists and in a valid project
        validateUserValidAndInsideValidProject(userDto, projectID, request);

        // Validate if tasks still exist in project
        boolean projectHasZeroTasksLeft = tasksRepository.doesProjectHaveZeroTasks(projectID);

        if (!projectHasZeroTasksLeft) {
            ProjectsExceptions.ProjectMustHaveZeroTasksBeforeDeletion projectMustHaveZeroTasksBeforeDeletion =
                    new ProjectsExceptions.ProjectMustHaveZeroTasksBeforeDeletion(
                            ErrorMessageConstants.PROJECT_STILL_HAS_TASKS.getValue());

            log.error("{}. | RID: {} {}",
                    ErrorMessageConstants.PROJECT_STILL_HAS_TASKS.getValue(),
                    request.getAttribute(RID),
                    System.lineSeparator(),
                    projectMustHaveZeroTasksBeforeDeletion);

            throw projectMustHaveZeroTasksBeforeDeletion;
        }

        // Delete the project
        projectsRepository.deleteProject(projectID);
    }

    @Contract("_, _ -> new")
    public @NotNull ProjectsSummariesDto getUserProjectsSummary(UserDto userDto, HttpServletRequest request) {
        BeaverusersRecord beaverusersRecord = usersRepository.getUserByEmailAndId(userDto, request);

        List<ProjectSummaryDto> projects =
                projectsRepository.getUserProjectsSummary(beaverusersRecord.getUserId());

        projects.forEach( project -> project.setLocations(request.getContextPath()));

        return new ProjectsSummariesDto(projects);
    }

    public @NotNull ProjectDetailsDto getSpecificProjectDetails(UserDto userDto, int projectID, HttpServletRequest request) {
        BeaverusersRecord beaverusersRecord = usersRepository.getUserByEmailAndId(userDto, request);

        projectUsersRepository.isUserInProjectAndDoesProjectExist(beaverusersRecord.getUserId(), projectID, request);

        ProjectDetailsDto projectDetails = projectsRepository.getSpecificProjectDetails(projectID);
        projectDetails.setLocations(request.getContextPath());

        return projectDetails;
    }

    public void modifyProjectName(
            UserDto userDto,
            int projectID,
            @NotNull NewProjectNameDto newProjectNameDto,
            HttpServletRequest request) {
        validateUserValidAndInsideValidProject(userDto, projectID, request);
        String newProjectName = newProjectNameDto.projectName();
        projectsRepository.modifyProjectName(projectID, newProjectName, request);
        projectsRepository.updateLastUpdatedForProject(projectID);
    }

    @Override
    public @NotNull BeaverusersRecord validateUserValidAndInsideValidProject(
            UserDto userDto,
            int projectID,
            HttpServletRequest request) {
        BeaverusersRecord beaverusersRecord = usersRepository.getUserByEmailAndId(userDto, request);

        // Validate user in project and project exists
        projectUsersRepository.isUserInProjectAndDoesProjectExist(beaverusersRecord.getUserId(), projectID, request);

        return beaverusersRecord;
    }
}
