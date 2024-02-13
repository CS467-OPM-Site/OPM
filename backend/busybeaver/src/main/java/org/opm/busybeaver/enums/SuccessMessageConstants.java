package org.opm.busybeaver.enums;

public enum SuccessMessageConstants {
    SUCCESS("Success"),
    USER_ADDED("User added"),
    TEAM_DELETED("Team deleted"),
    TEAM_MEMBER_REMOVED("User removed from team");

    private final String value;

    SuccessMessageConstants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
