package org.opm.busybeaver.exceptions.Sprints;

public final class SprintsExceptions {

    public static class SprintDoesNotExistInProject extends RuntimeException {
        public SprintDoesNotExistInProject(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class SprintDatesInvalid extends RuntimeException {
        public SprintDatesInvalid(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class SprintNameAlreadyInProject extends RuntimeException {
        public SprintNameAlreadyInProject(String errorMessage) {
            super(errorMessage);
        }
    }
}
