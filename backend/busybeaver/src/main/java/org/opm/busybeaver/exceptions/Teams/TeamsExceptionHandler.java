package org.opm.busybeaver.exceptions.Teams;

import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.opm.busybeaver.utils.Utils.generateExceptionResponse;

@ControllerAdvice
public class TeamsExceptionHandler {
    @ExceptionHandler(TeamAlreadyExistsForUserException.class)
    public ResponseEntity<?> teamAlreadyExistsForUser() {
        return new ResponseEntity<>(
                generateExceptionResponse(
                        ErrorMessageConstants.TEAM_ALREADY_EXISTS_FOR_USER.getValue(),
                        HttpStatus.BAD_REQUEST.value()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(UserAlreadyInTeamException.class)
    public ResponseEntity<?> userAlreadyInTeam() {
        return new ResponseEntity<>(
                generateExceptionResponse(
                        ErrorMessageConstants.USER_ALREADY_IN_TEAM.getValue(),
                        HttpStatus.BAD_REQUEST.value()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(UserNotInTeamOrTeamDoesNotExistException.class)
    public ResponseEntity<?> userNotInTeam() {
        return new ResponseEntity<>(
                generateExceptionResponse(
                        ErrorMessageConstants.USER_NOT_IN_TEAM_OR_TEAM_NOT_EXIST.getValue(),
                        HttpStatus.NOT_FOUND.value()
                ),
                HttpStatus.NOT_FOUND
        );
    }
}
