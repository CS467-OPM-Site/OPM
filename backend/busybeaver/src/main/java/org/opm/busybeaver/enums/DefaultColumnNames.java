package org.opm.busybeaver.enums;

public enum DefaultColumnNames {
    COLUMNS_DEFAULT_TODO("To-Do"),
    COLUMNS_DEFAULT_INPROGRESS("In Progress"),
    COLUMNS_DEFAULT_COMPLETED("Completed");

    private final String value;

    DefaultColumnNames(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
