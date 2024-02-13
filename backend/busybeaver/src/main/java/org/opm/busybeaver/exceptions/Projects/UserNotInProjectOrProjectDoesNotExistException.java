package org.opm.busybeaver.exceptions.Projects;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UserNotInProjectOrProjectDoesNotExistException extends RuntimeException {
    public UserNotInProjectOrProjectDoesNotExistException(String errorMessage) {
        super(errorMessage);
    }
}
