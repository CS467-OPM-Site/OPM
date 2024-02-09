package org.opm.busybeaver.exceptions.Teams;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class TeamCreatorCannotBeRemovedException extends RuntimeException {
    public TeamCreatorCannotBeRemovedException(String errorMessage) {
        super(errorMessage);
    }
}
