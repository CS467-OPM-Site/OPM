package org.opm.busybeaver.enums;

public enum ErrorMessageConstants {
    ERROR_KEY_VAL("Error"),
    MESSAGE("message"),
    CODE("code"),
    INVALID_ARGUMENT("Invalid argument"),
    USER_ALREADY_EXISTS("User already exists with those details"),
    USER_DOES_NOT_EXIST("User does not exist"),
    USER_NOT_IN_TEAM_OR_TEAM_NOT_EXIST("User not in team, or team does not exist"),
    USER_NOT_CREATOR_OF_TEAM("Only team creators can delete their team"),
    TEAM_DOES_NOT_EXIST("That team does not exist"),
    TEAM_STILL_HAS_MEMBERS("Team still contains other members - remove them before deleting the team"),
    REQUIRED_REQUEST_BODY_IS_MISSING("Required request body is missing"),
    INVALID_HTTP_REQUEST("Invalid HTTP request made"),
    TEAM_ALREADY_EXISTS_FOR_USER("You have already made a team with this name"),
    USER_ALREADY_IN_TEAM("User already exists in this team"),
    TEAM_CREATOR_CANNOT_BE_REMOVED("Team creator cannot be removed"),
    PROJECT_ALREADY_EXISTS_FOR_TEAM("Project name for that team already exists, please choose another project name"),
    USER_NOT_IN_PROJECT_OR_PROJECT_NOT_EXIST("User not in project, or project does not exist"),
    TEAM_STILL_HAS_PROJECTS("Team still has associated projects - remove them before deleting the team"),
    MISSING_INVALID_HEADER_TOKEN("Missing or invalid authentication header and token.");

    private final String value;

    ErrorMessageConstants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
