package org.opm.busybeaver.exceptions.Teams;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public final class TeamsExceptions {

    public static class TeamAlreadyExistsForUserException extends RuntimeException {
        public TeamAlreadyExistsForUserException(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class TeamCreatorCannotBeRemovedException extends RuntimeException {
        public TeamCreatorCannotBeRemovedException(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class TeamDoesNotExistException extends RuntimeException {
        public TeamDoesNotExistException(String errorMessage) {
            super(errorMessage);
        }
    }
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public static class TeamStillHasMembersException extends RuntimeException {

        public TeamStillHasMembersException(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class TeamStillHasProjectsException extends RuntimeException {
        public TeamStillHasProjectsException(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class UserAlreadyInTeamException extends RuntimeException {
        public UserAlreadyInTeamException(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class UserNotInTeamOrTeamDoesNotExistException extends RuntimeException {
        public UserNotInTeamOrTeamDoesNotExistException(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class UserNotTeamCreatorException extends RuntimeException {
        public UserNotTeamCreatorException(String errorMessage) {
            super(errorMessage);
        }
    }
}
