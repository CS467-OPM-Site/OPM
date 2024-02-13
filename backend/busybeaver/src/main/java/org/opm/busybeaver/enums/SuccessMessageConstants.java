package org.opm.busybeaver.enums;

public enum SuccessMessageConstants {
    SUCCESS("Success"),
    USER_ADDED("User added"),
    TEAM_DELETED("Team deleted"),
    TEAM_MEMBER_REMOVED("User removed from team"),
    TASK_MOVED("Task moved"),
    TASK_DELETED("Task deleted"),
    COLUMN_DELETED("Column removed from project");

    private final String value;

    SuccessMessageConstants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
