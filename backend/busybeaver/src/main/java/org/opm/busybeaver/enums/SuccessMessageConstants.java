package org.opm.busybeaver.enums;

public enum SuccessMessageConstants {
    TEAM_DELETED("Team deleted");

    private final String value;

    SuccessMessageConstants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
