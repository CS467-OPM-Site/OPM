package org.opm.busybeaver.exceptions.Tasks;

public final class TasksExceptions {

    public static class TaskDoesNotExistInProject extends RuntimeException {
        public TaskDoesNotExistInProject(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class TaskAlreadyInColumn extends RuntimeException {
        public TaskAlreadyInColumn(String errorMessage) { super(errorMessage); }
    }
}
