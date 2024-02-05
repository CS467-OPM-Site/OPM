package org.opm.busybeaver.enums;

public enum ErrorMessageConstants {

    MISSING_INVALID_HEADER_TOKEN("Missing or invalid authentication header and token.");

    private final String value;

    ErrorMessageConstants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
