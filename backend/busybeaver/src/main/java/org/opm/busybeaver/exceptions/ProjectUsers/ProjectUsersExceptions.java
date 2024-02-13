package org.opm.busybeaver.exceptions.ProjectUsers;

public final class ProjectUsersExceptions {

    public static class UserAlreadyInProject extends RuntimeException {
        public UserAlreadyInProject(String errorMessage) {
            super(errorMessage);
        }
    }
}
