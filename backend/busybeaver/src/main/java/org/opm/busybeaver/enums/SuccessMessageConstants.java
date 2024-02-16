package org.opm.busybeaver.enums;

public enum SuccessMessageConstants {
    SUCCESS("Success"),
    USER_ADDED("User added"),
    TEAM_DELETED("Team deleted"),
    TEAM_MEMBER_REMOVED("User removed from team"),
    TASK_MOVED("Task moved"),
    TASK_DELETED("Task deleted"),
    COLUMN_DELETED("Column removed from project"),
    USER_WAS_ADDED_TO_PROJECT(" was added to the project"), // Place username in front
    USER_WAS_REMOVED_FROM_PROJECT(" was removed from the project"),
    PROJECT_DELETED("Project deleted"),
    COMMENT_MODIFIED("Comment modified"),
    COMMENT_DELETED("Comment deleted"); // Place username in front

    private final String value;

    SuccessMessageConstants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
