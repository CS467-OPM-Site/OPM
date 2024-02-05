package org.opm.busybeaver.enums;

public enum EnvVariables {
    FIREBASE_ADMIN_SDK_KEY("FIREBASE_ADMIN_SDK_KEY");

    private final String value;

    EnvVariables(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
