package org.opm.busybeaver.exceptions.Teams;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UserNotInTeamOrTeamDoesNotExistException extends RuntimeException {
    public UserNotInTeamOrTeamDoesNotExistException(String errorMessage) {
        super(errorMessage);
    }
}
