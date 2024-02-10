package org.opm.busybeaver.exceptions.Projects;

import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.opm.busybeaver.utils.Utils.generateExceptionResponse;

@ControllerAdvice
public class ProjectsExceptionHandler {
    @ExceptionHandler(ProjectAlreadyExistsForTeamException.class)
    public ResponseEntity<?> projectAlreadyExistsForTeam() {
        return new ResponseEntity<>(
                generateExceptionResponse(
                        ErrorMessageConstants.PROJECT_ALREADY_EXISTS_FOR_TEAM.getValue(),
                        HttpStatus.BAD_REQUEST.value()
                ),
                HttpStatus.BAD_REQUEST
        );
    }
}
