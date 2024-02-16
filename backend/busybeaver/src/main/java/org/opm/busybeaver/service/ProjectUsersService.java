package org.opm.busybeaver.service;

import org.opm.busybeaver.dto.ProjectUsers.ProjectUserSummaryDto;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.dto.Users.UsernameDto;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.exceptions.ProjectUsers.ProjectUsersExceptions;
import org.opm.busybeaver.exceptions.Projects.ProjectsExceptions;
import org.opm.busybeaver.jooq.tables.records.BeaverusersRecord;
import org.opm.busybeaver.repository.ProjectRepository;
import org.opm.busybeaver.repository.ProjectUsersRepository;
import org.opm.busybeaver.repository.UserRepository;
import org.opm.busybeaver.service.ServiceInterfaces.ValidateUserAndProjectInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public final class ProjectUsersService implements ValidateUserAndProjectInterface {
    private final ProjectRepository projectRepository;
    private final ProjectUsersRepository projectUsersRepository;
    private final UserRepository userRepository;

    @Autowired
    public ProjectUsersService(
            ProjectRepository projectRepository,
            ProjectUsersRepository projectUsersRepository,
            UserRepository userRepository
    ) {
        this.projectRepository = projectRepository;
        this.projectUsersRepository = projectUsersRepository;
        this.userRepository = userRepository;
    }

    public ProjectUserSummaryDto getAllUsersInProject(UserDto userDto, int projectID, String contextPath) {
       validateUserValidAndInsideValidProject(userDto, projectID);

       ProjectUserSummaryDto projectUserSummaryDto = projectUsersRepository.getAllUsersInProject(projectID);
       projectUserSummaryDto.setLocations(contextPath);

       return projectUserSummaryDto;
    }

    public void addUserToProject(UserDto userDto, int projectID, UsernameDto usernameDto)
        throws ProjectUsersExceptions.UserAlreadyInProject
    {
        // Validate user exists, validate user in project
        validateUserValidAndInsideValidProject(userDto, projectID);

        // Validate user to add exists
        BeaverusersRecord userToAdd = userRepository.verifyUserExistsAndReturn(usernameDto.username());

        // Verify user is not in project
        if (projectUsersRepository.isUserInProjectAndDoesProjectExist(userToAdd.getUserId(), projectID)) {
            throw new ProjectUsersExceptions.UserAlreadyInProject(ErrorMessageConstants.USER_ALREADY_IN_PROJECT.getValue());
        }

        // Add user to project
        projectUsersRepository.addUserToProject(projectID, userToAdd.getUserId());

        // Update last updated for project
        projectRepository.updateLastUpdatedForProject(projectID);
    }

    public void removeUserFromProject(UserDto userDto, int projectID, UsernameDto usernameDto)
        throws ProjectUsersExceptions.UserNotInProject,
            ProjectUsersExceptions.ProjectCannotHaveZeroUsers
    {
        // Validate user exists, validate user in project
        validateUserValidAndInsideValidProject(userDto, projectID);

        // Validate user to remove exists
        BeaverusersRecord userToRemove = userRepository.verifyUserExistsAndReturn(usernameDto.username());

        // Verify user to remove exists in Project, skip if removing user wants to remove themselves as already verified
        if (!userDto.getEmail().equals(userToRemove.getEmail()) &&
                !projectUsersRepository.isUserInProjectAndDoesProjectExist(userToRemove.getUserId(), projectID)) {
            throw new ProjectUsersExceptions.UserNotInProject(ErrorMessageConstants.USER_NOT_IN_PROJECT.getValue());
        }

        // Ensure not the last user in the project
        if (!projectUsersRepository.doesProjectStillHaveUsers(projectID)) {
            throw new ProjectUsersExceptions.ProjectCannotHaveZeroUsers(ErrorMessageConstants.PROJECT_CANNOT_HAVE_ZERO_USERS.getValue());
        }

        // Remove user from project
        projectUsersRepository.removeUserFromProject(projectID, userToRemove.getUserId());

        // Update last updated for project
        projectRepository.updateLastUpdatedForProject(projectID);
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
