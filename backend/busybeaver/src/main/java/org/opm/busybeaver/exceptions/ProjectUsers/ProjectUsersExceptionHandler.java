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
    public ResponseEntity<?> userAlreadyInThisProject() {
        return new ResponseEntity<>(
                generateExceptionResponse(
                        ErrorMessageConstants.USER_ALREADY_IN_PROJECT.getValue(),
                        HttpStatus.CONFLICT.value()
                ),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(ProjectUsersExceptions.UserNotInProject.class)
    public ResponseEntity<?> userNotInThisProject() {
        return new ResponseEntity<>(
                generateExceptionResponse(
                        ErrorMessageConstants.USER_NOT_IN_PROJECT.getValue(),
                        HttpStatus.NOT_FOUND.value()
                ),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(ProjectUsersExceptions.ProjectCannotHaveZeroUsers.class)
    public ResponseEntity<?> projectCannotHaveZeroUsers() {
        return new ResponseEntity<>(
                generateExceptionResponse(
                        ErrorMessageConstants.PROJECT_CANNOT_HAVE_ZERO_USERS.getValue(),
                        HttpStatus.BAD_REQUEST.value()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ProjectUsersExceptions.AssignedToUserNotInProjectOrNonexistent.class)
    public ResponseEntity<?> assignedToNotInProjectOrNonexistent() {
        return new ResponseEntity<>(
                generateExceptionResponse(
                        ErrorMessageConstants.ASSIGNED_TO_USER_NOT_IN_PROJECT_OR_USER_NOT_EXIST.getValue(),
                        HttpStatus.NOT_FOUND.value()
                ),
                HttpStatus.NOT_FOUND
        );
    }
}
