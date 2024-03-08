package org.opm.busybeaver.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.opm.busybeaver.dto.ProjectUsers.ProjectUserShortDto;
import org.opm.busybeaver.dto.ProjectUsers.ProjectUserSummaryDto;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.dto.Users.UsernameDto;
import org.opm.busybeaver.enums.BusyBeavConstants;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.exceptions.ProjectUsers.ProjectUsersExceptions;
import org.opm.busybeaver.jooq.tables.records.BeaverusersRecord;
import org.opm.busybeaver.repository.ProjectsRepository;
import org.opm.busybeaver.repository.ProjectUsersRepository;
import org.opm.busybeaver.repository.UsersRepository;
import org.opm.busybeaver.service.ServiceInterfaces.ValidateUserAndProjectInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public final class ProjectUsersService implements ValidateUserAndProjectInterface {
    private final ProjectsRepository projectsRepository;
    private final ProjectUsersRepository projectUsersRepository;
    private final UsersRepository usersRepository;
    private static final String RID = BusyBeavConstants.REQUEST_ID.getValue();

    @Autowired
    public ProjectUsersService(
            ProjectsRepository projectsRepository,
            ProjectUsersRepository projectUsersRepository,
            UsersRepository usersRepository
    ) {
        this.projectsRepository = projectsRepository;
        this.projectUsersRepository = projectUsersRepository;
        this.usersRepository = usersRepository;
    }

    public @NotNull ProjectUserSummaryDto getAllUsersInProject(UserDto userDto, int projectID, HttpServletRequest request) {
       BeaverusersRecord requestingUser = validateUserValidAndInsideValidProject(userDto, projectID, request);

       ProjectUserSummaryDto projectUserSummaryDto = projectUsersRepository.getAllUsersInProject(projectID);

       Optional<ProjectUserShortDto> currentUser = projectUserSummaryDto.getUsers().stream()
                       .filter(user -> user.userID() == requestingUser.getUserId())
                       .findFirst();

       currentUser.ifPresent(projectUserSummaryDto::setCurrentUser);

       projectUserSummaryDto.setLocations(request.getContextPath());

       return projectUserSummaryDto;
    }

    public void addUserToProject(
            UserDto userDto,
            int projectID,
            @NotNull UsernameDto usernameDto,
            HttpServletRequest request)
        throws ProjectUsersExceptions.UserAlreadyInProject
    {
        // Validate user exists, validate user in project
        validateUserValidAndInsideValidProject(userDto, projectID, request);

        // Validate user to add exists
        BeaverusersRecord userToAdd = usersRepository.getUserByUsername(usernameDto.username(), request);

        // Verify user is not in project
        if (projectUsersRepository.isUserInProjectAndDoesProjectExist(userToAdd.getUserId(), projectID, request)) {
            ProjectUsersExceptions.UserAlreadyInProject userAlreadyInProject =
                    new ProjectUsersExceptions.UserAlreadyInProject(
                            ErrorMessageConstants.USER_ALREADY_IN_PROJECT.getValue());

            log.error("{}. | RID: {} {}",
                    ErrorMessageConstants.USER_ALREADY_IN_PROJECT.getValue(),
                    request.getAttribute(RID),
                    System.lineSeparator(),
                    userAlreadyInProject);

            throw userAlreadyInProject;
        }

        // Add user to project
        projectUsersRepository.addUserToProject(projectID, userToAdd.getUserId());

        // Update last updated for project
        projectsRepository.updateLastUpdatedForProject(projectID);
    }

    public void removeUserFromProject(
            UserDto userDto,
            int projectID,
            @NotNull UsernameDto usernameDto,
            HttpServletRequest request)
        throws ProjectUsersExceptions.ProjectCannotHaveZeroUsers {
        // Validate user exists, validate user in project
        validateUserValidAndInsideValidProject(userDto, projectID, request);

        // Validate user to remove exists
        BeaverusersRecord userToRemove = usersRepository.getUserByUsername(usernameDto.username(), request);

        // Verify user to remove exists in Project, skip if removing user wants to remove themselves as already verified
        if (!userDto.getEmail().equals(userToRemove.getEmail())) {
            projectUsersRepository.isUserInProjectAndDoesProjectExist(userToRemove.getUserId(), projectID, request);
        }

        // Ensure not the last user in the project
        if (!projectUsersRepository.doesProjectStillHaveUsers(projectID)) {
            ProjectUsersExceptions.ProjectCannotHaveZeroUsers projectCannotHaveZeroUsers =
                    new ProjectUsersExceptions.ProjectCannotHaveZeroUsers(
                            ErrorMessageConstants.PROJECT_CANNOT_HAVE_ZERO_USERS.getValue());

            log.error("{}. | RID: {} {}",
                    ErrorMessageConstants.PROJECT_CANNOT_HAVE_ZERO_USERS.getValue(),
                    request.getAttribute(RID),
                    System.lineSeparator(),
                    projectCannotHaveZeroUsers);

            throw projectCannotHaveZeroUsers;
        }

        // Remove user from project
        projectUsersRepository.removeUserFromProject(projectID, userToRemove.getUserId());

        // Update last updated for project
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
