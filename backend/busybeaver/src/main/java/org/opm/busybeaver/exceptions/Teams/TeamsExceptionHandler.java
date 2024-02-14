package org.opm.busybeaver.exceptions.Teams;

import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.opm.busybeaver.utils.Utils.generateExceptionResponse;

@ControllerAdvice
public class TeamsExceptionHandler {
    @ExceptionHandler(TeamsExceptions.TeamAlreadyExistsForUserException.class)
    public ResponseEntity<?> teamAlreadyExistsForUser() {
        return new ResponseEntity<>(
                generateExceptionResponse(
                        ErrorMessageConstants.TEAM_ALREADY_EXISTS_FOR_USER.getValue(),
                        HttpStatus.BAD_REQUEST.value()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(TeamsExceptions.UserAlreadyInTeamException.class)
    public ResponseEntity<?> userAlreadyInTeam() {
        return new ResponseEntity<>(
                generateExceptionResponse(
                        ErrorMessageConstants.USER_ALREADY_IN_TEAM.getValue(),
                        HttpStatus.BAD_REQUEST.value()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(TeamsExceptions.UserNotInTeamOrTeamDoesNotExistException.class)
    public ResponseEntity<?> userNotInTeam() {
        return new ResponseEntity<>(
                generateExceptionResponse(
                        ErrorMessageConstants.USER_NOT_IN_TEAM_OR_TEAM_NOT_EXIST.getValue(),
                        HttpStatus.NOT_FOUND.value()
                ),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(TeamsExceptions.TeamDoesNotExistException.class)
    public ResponseEntity<?> userNotCreatorOrTeamNotExist() {
        return new ResponseEntity<>(
                generateExceptionResponse(
                        ErrorMessageConstants.TEAM_DOES_NOT_EXIST.getValue(),
                        HttpStatus.NOT_FOUND.value()
                ),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(TeamsExceptions.UserNotTeamCreatorException.class)
    public ResponseEntity<?> userNotCreatorOfTeam() {
        return new ResponseEntity<>(
                generateExceptionResponse(
                        ErrorMessageConstants.USER_NOT_CREATOR_OF_TEAM.getValue(),
                        HttpStatus.FORBIDDEN.value()
                ),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(TeamsExceptions.TeamStillHasMembersException.class)
    public ResponseEntity<?> teamStillHasMembers() {
        return new ResponseEntity<>(
                generateExceptionResponse(
                        ErrorMessageConstants.TEAM_STILL_HAS_MEMBERS.getValue(),
                        HttpStatus.FORBIDDEN.value()
                ),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(TeamsExceptions.TeamCreatorCannotBeRemovedException.class)
    public ResponseEntity<?> teamCreatorCannotBeRemoved() {
        return new ResponseEntity<>(
                generateExceptionResponse(
                        ErrorMessageConstants.TEAM_CREATOR_CANNOT_BE_REMOVED.getValue(),
                        HttpStatus.FORBIDDEN.value()
                ),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(TeamsExceptions.TeamStillHasProjectsException.class)
    public ResponseEntity<?> teamStillHasProjects() {
        return new ResponseEntity<>(
                generateExceptionResponse(
                        ErrorMessageConstants.TEAM_STILL_HAS_PROJECTS.getValue(),
                        HttpStatus.FORBIDDEN.value()
                ),
                HttpStatus.FORBIDDEN
        );
    }
}
