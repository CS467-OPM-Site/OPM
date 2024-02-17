package org.opm.busybeaver.exceptions.Sprints;

import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.opm.busybeaver.utils.Utils.generateExceptionResponse;

@ControllerAdvice
public class SprintsExceptionHandler {
    @ExceptionHandler(SprintsExceptions.SprintDoesNotExistInProject.class)
    public ResponseEntity<?> sprintDoesNotExistInProject() {
        return new ResponseEntity<>(
                generateExceptionResponse(
                        ErrorMessageConstants.SPRINT_NOT_IN_PROJECT.getValue(),
                        HttpStatus.NOT_FOUND.value()
                ),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(SprintsExceptions.SprintDatesInvalid.class)
    public ResponseEntity<?> sprintDatesInvalid() {
        return new ResponseEntity<>(
                generateExceptionResponse(
                        ErrorMessageConstants.SPRINT_DATES_INVALID.getValue(),
                        HttpStatus.BAD_REQUEST.value()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(SprintsExceptions.SprintNameAlreadyInProject.class)
    public ResponseEntity<?> sprintNameAlreadyInProject() {
        return new ResponseEntity<>(
                generateExceptionResponse(
                        ErrorMessageConstants.PROJECT_CONTAINS_SPRINT_NAME.getValue(),
                        HttpStatus.CONFLICT.value()
                ),
                HttpStatus.CONFLICT
        );
    }
}
