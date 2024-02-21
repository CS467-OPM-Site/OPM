package org.opm.busybeaver.service.ServiceInterfaces;

import jakarta.servlet.http.HttpServletRequest;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.exceptions.Projects.ProjectsExceptions;
import org.opm.busybeaver.jooq.tables.records.BeaverusersRecord;

public interface ValidateUserAndProjectInterface {
    BeaverusersRecord validateUserValidAndInsideValidProject(UserDto userDto, int projectID, HttpServletRequest request);
}
