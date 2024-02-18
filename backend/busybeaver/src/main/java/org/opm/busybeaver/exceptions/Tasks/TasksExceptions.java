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

    public static class TaskFieldNotFound extends RuntimeException {
        public TaskFieldNotFound(String errorMessage) { super(errorMessage); }
    }

    public static class InvalidTaskPriority extends RuntimeException {
        public InvalidTaskPriority(String errorMessage) { super(errorMessage); }
    }

    public static class InvalidTaskDueDate extends RuntimeException {
        public InvalidTaskDueDate(String errorMessage) { super(errorMessage); }
    }

    public static class InvalidTaskTitle extends RuntimeException {
        public InvalidTaskTitle(String errorMessage) { super(errorMessage); }
    }

    public static class InvalidTaskDescription extends RuntimeException {
        public InvalidTaskDescription(String errorMessage) { super(errorMessage); }
    }
}
