package org.opm.busybeaver.exceptions.Sprints;

public final class SprintsExceptions {

    public static class SprintDoesNotExistInProject extends RuntimeException {
        public SprintDoesNotExistInProject(String errorMessage) {
            super(errorMessage);
        }
    }
}
