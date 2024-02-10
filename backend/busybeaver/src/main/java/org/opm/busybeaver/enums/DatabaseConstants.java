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
    PROJECTUSERS_LEAD_ROLE("Lead");

    private final String value;

    DatabaseConstants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
