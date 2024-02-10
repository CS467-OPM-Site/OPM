package org.opm.busybeaver.exceptions.Projects;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ProjectAlreadyExistsForTeamException extends RuntimeException {
    public ProjectAlreadyExistsForTeamException(String errorMessage) {
        super(errorMessage);
    }
}
