package org.opm.busybeaver.exceptions.Users;

public final class UsersExceptions {
    public static class UserAlreadyExistsException extends RuntimeException {
        public UserAlreadyExistsException(String errorMessage) {
            super(errorMessage);
        }
    }
    public static class UserDoesNotExistException extends RuntimeException {
        public UserDoesNotExistException(String errorMessage) {
            super(errorMessage);
        }
    }
}
