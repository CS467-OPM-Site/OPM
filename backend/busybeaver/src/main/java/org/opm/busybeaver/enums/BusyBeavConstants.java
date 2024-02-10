package org.opm.busybeaver.enums;

public enum BusyBeavConstants {
    USER_KEY_VAL("User"),
    ERROR_KEY_VAL("Error"),
    SUCCESS("Success"),
    JSON_CONTENT_TYPE("application/json"),
    UTF_8_ENCODING("UTF-8"),
    RESOURCES_DIR("./src/main/resources/");

    private final String value;

    BusyBeavConstants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
