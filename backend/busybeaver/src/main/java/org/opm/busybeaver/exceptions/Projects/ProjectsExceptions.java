package org.opm.busybeaver.exceptions.Projects;

public final class ProjectsExceptions {
    public static class ProjectAlreadyExistsForTeamException extends RuntimeException {
        public ProjectAlreadyExistsForTeamException(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class UserNotInProjectOrProjectDoesNotExistException extends RuntimeException {
        public UserNotInProjectOrProjectDoesNotExistException(String errorMessage) {
            super(errorMessage);
        }
    }
}
