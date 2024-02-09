package org.opm.busybeaver.enums;

public enum BusyBeavConstants {
    USER_KEY_VAL(Constants.USER_KEY_VAL),
    JSON_CONTENT_TYPE("application/json"),
    UTF_8_ENCODING("UTF-8"),
    LOCATION("Location"),
    RESOURCES_DIR("./src/main/resources/");

    private final String value;

    BusyBeavConstants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static class Constants {
        public static final String USER_KEY_VAL = "User";

    }

}
