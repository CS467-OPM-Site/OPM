package org.opm.busybeaver.exceptions.Teams;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserNotTeamCreatorException extends RuntimeException {
    public UserNotTeamCreatorException(String errorMessage) {
        super(errorMessage);
    }
}
