package org.opm.busybeaver.enums;

public enum DatabaseConstants {
    TEAMUSERS_USER_ROLE("User"),
    TEAMUSERS_CREATOR_ROLE("Creator"),
    TEAMUSERS_MOD_ROLE("Mod");

    private final String value;

    DatabaseConstants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
