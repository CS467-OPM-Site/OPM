package org.opm.busybeaver.enums;

public enum ErrorMessageConstants {

    MESSAGE("message"),
    CODE("code"),
    INVALID_ARGUMENT("Invalid argument"),
    USER_ALREADY_EXISTS("User already exists with those details"),
    USER_DOES_NOT_EXIST("User does not exist"),
    USER_NOT_IN_TEAM_OR_TEAM_NOT_EXIST("User not in team, or team does not exist"),
    MISSING_INVALID_HEADER_TOKEN("Missing or invalid authentication header and token.");

    private final String value;

    ErrorMessageConstants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
