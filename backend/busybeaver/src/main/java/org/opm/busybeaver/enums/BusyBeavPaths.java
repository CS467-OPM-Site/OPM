package org.opm.busybeaver.enums;


public enum BusyBeavPaths {
    V1(Constants.V1),
    PROJECTS(Constants.PROJECTS),
    TEAMS(Constants.TEAMS),
    MEMBERS(Constants.MEMBERS),
    USERS(Constants.USERS),
    REGISTER(Constants.REGISTER),
    AUTH(Constants.AUTH);

    private final String value;

    BusyBeavPaths(String value) { this.value = value; }

    public String getValue() { return value; }

    public static class Constants {
        public static final String V1 = "/v1";
        public static final String PROJECTS = "/projects";
        public static final String TEAMS = "/teams";
        public static final String USERS = "/users";
        public static final String REGISTER = "/register";
        public static final String AUTH = "/auth";
        public static final String MEMBERS = "/members";
    }
}
