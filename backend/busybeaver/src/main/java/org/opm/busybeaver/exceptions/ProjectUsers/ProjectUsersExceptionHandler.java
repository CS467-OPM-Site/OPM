package org.opm.busybeaver.exceptions.ProjectUsers;

import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.opm.busybeaver.utils.Utils.generateExceptionResponse;

@ControllerAdvice
public class ProjectUsersExceptionHandler {
    @ExceptionHandler(ProjectUsersExceptions.UserAlreadyInProject.class)
    public ResponseEntity<?> sprintDoesNotExistInProject() {
        return new ResponseEntity<>(
                generateExceptionResponse(
                        ErrorMessageConstants.USER_ALREADY_IN_PROJECT.getValue(),
                        HttpStatus.CONFLICT.value()
                ),
                HttpStatus.CONFLICT
        );
    }
}
