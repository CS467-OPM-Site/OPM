package org.opm.busybeaver.enums;

public enum EnvVariables {
    DB_USERNAME("DB_USERNAME"),
    DB_PASSWORD("DB_PASSWORD"),
    DB_URL("DB_URL"),
    FIREBASE_ADMIN_SDK_KEY("FIREBASE_ADMIN_SDK_KEY");


    private final String value;

    EnvVariables(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
