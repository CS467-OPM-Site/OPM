package org.opm.busybeaver.service;

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

    public void addUserToProject(UserDto userDto, int projectID, UsernameDto usernameDto) {
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
