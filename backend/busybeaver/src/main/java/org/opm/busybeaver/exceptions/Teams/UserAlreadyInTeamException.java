package org.opm.busybeaver.exceptions.Teams;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserAlreadyInTeamException extends RuntimeException {
    public UserAlreadyInTeamException(String errorMessage) {
        super(errorMessage);
    }
}
