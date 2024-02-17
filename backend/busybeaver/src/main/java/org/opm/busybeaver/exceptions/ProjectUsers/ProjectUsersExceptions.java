package org.opm.busybeaver.exceptions.ProjectUsers;

public final class ProjectUsersExceptions {

    public static class UserAlreadyInProject extends RuntimeException {
        public UserAlreadyInProject(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class UserNotInProject extends RuntimeException {
        public UserNotInProject(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class ProjectCannotHaveZeroUsers extends RuntimeException {
        public ProjectCannotHaveZeroUsers(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class AssignedToUserNotInProjectOrNonexistent extends RuntimeException {
        public AssignedToUserNotInProjectOrNonexistent(String errorMessage) {
            super(errorMessage);
        }
    }
}
