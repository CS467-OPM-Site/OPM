package org.opm.busybeaver.enums;

public enum DatabaseConstants {
    TEAMUSERS_USER_ROLE("User"),
    TEAMUSERS_CREATOR_ROLE("Creator"),
    TEAMUSERS_MOD_ROLE("Mod"),
    PROJECTUSERS_MEMBER_ROLE("Member"),
    PROJECTUSERS_DEV_ROLE("Dev"),
    PROJECTUSERS_PROJECTMANAGER_ROLE("Project Manager"),
    PROJECTUSERS_MANAGER_ROLE("Manager"),
    PROJECTUSERS_EXTERNAL_ROLE("External"),
    PROJECTUSERS_LEAD_ROLE("Lead"),
    PRIORITY_LOW(Constants.PRIORITY_LOW),
    PRIORITY_MEDIUM(Constants.PRIORITY_MEDIUM),
    PRIORITY_HIGH(Constants.PRIORITY_HIGH),
    PRIORITY_NONE(Constants.PRIORITY_NONE);

    private final String value;

    DatabaseConstants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static class Constants {
        public static final String PRIORITY_NONE = "None";
        public static final String PRIORITY_LOW = "Low";
        public static final String PRIORITY_MEDIUM = "Medium";
        public static final String PRIORITY_HIGH = "High";
    }

}
