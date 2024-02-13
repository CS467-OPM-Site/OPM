package org.opm.busybeaver.service.ServiceInterfaces;

import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.exceptions.Projects.ProjectsExceptions;

public interface ValidateUserAndProjectInterface {
    void validateUserValidAndInsideValidProject(UserDto userDto, int projectID)
            throws ProjectsExceptions.UserNotInProjectOrProjectDoesNotExistException;
}
