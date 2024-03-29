package org.opm.busybeaver.enums;

public enum ErrorMessageConstants {
    ERROR_KEY_VAL("Error"),
    MESSAGE("message"),
    CODE("code"),
    INVALID_RESOURCE("Resource or URL not found"),
    INVALID_ARGUMENT("Invalid argument"),
    USER_ALREADY_EXISTS("User already exists with those details"),
    USER_DOES_NOT_EXIST("User does not exist"),
    USER_NOT_IN_TEAM_OR_TEAM_NOT_EXIST("User not in team, or team does not exist"),
    USER_NOT_CREATOR_OF_TEAM("Only team creators can delete their team"),
    TEAM_DOES_NOT_EXIST("That team does not exist"),
    TEAM_STILL_HAS_MEMBERS("Team still contains other members - remove them before deleting the team"),
    REQUIRED_REQUEST_BODY_IS_MISSING("Required request body is missing"),
    INVALID_INTEGER_VALUE("Integer values, such as IDs, must contain positive non-zero integers, with no decimals"),
    INVALID_HTTP_REQUEST("Invalid HTTP request made, please check all input and request body arguments"),
    TEAM_ALREADY_EXISTS_FOR_USER("You have already made a team with this name"),
    USER_ALREADY_IN_TEAM("User already exists in this team"),
    TEAM_CREATOR_CANNOT_BE_REMOVED("Team creator cannot be removed"),
    PROJECT_ALREADY_EXISTS_FOR_TEAM("Project name for that team already exists, please choose another project name"),
    USER_NOT_IN_PROJECT_OR_PROJECT_NOT_EXIST("User not in project, or project does not exist"),
    ASSIGNED_TO_USER_NOT_IN_PROJECT_OR_USER_NOT_EXIST("Assigned-To user not in project, or user does not exist"),
    TEAM_STILL_HAS_PROJECTS("Team still has associated projects - remove them before deleting the team"),
    COLUMN_NOT_IN_PROJECT("Given column does not exist in this project"),
    COLUMN_TITLE_ALREADY_IN_PROJECT("Given column title already exists in this project"),
    SPRINT_NOT_IN_PROJECT("Given sprint does not exist in this project"),
    TASK_NOT_IN_PROJECT("Given task does not exist in this project"),
    TASK_ALREADY_IN_COLUMN("Task already in given column"),
    COLUMN_CONTAINS_TASKS("Column still contains tasks, cannot be deleted"),
    MISSING_INVALID_HEADER_TOKEN("Missing or invalid authentication header and token."),
    USER_ALREADY_IN_PROJECT("User already in this project"),
    USER_NOT_IN_PROJECT("User not in this project"),
    PROJECT_CANNOT_HAVE_ZERO_USERS("The last member of a project cannot remove themselves"),
    PROJECT_STILL_HAS_TASKS("Projects must have no tasks left before they can be deleted"),
    COMMENT_NOT_FOUND_ON_TASK("Comment not found on task"),
    USER_DID_NOT_LEAVE_THIS_COMMENT("User did not leave this comment"),
    COMMENT_EQUIVALENT_NOT_MODIFIED("Comment body was not modified"),
    COLUMN_POSITION_THE_SAME("New column index given is same as current column index"),
    COLUMN_INDEX_OUT_OF_BOUNDS("Column index given is out of bounds for the given project"),
    COLUMN_TITLE_EQUIVALENT_NOT_MODIFIED("Column title not changed, title identical to previous"),
    SPRINT_DATES_INVALID("Sprint dates are invalid"),
    PROJECT_CONTAINS_SPRINT_NAME("Project contains sprint with that name already"),
    TASK_FIELD_NOT_FOUND("Task field to modify not valid"),
    TASK_PRIORITY_INVALID("Task priority invalid"),
    TASK_DUE_DATE_INVALID("Task due date invalid"),
    TASK_TITLE_INVALID("Title length must be a string from 3 to 50 characters"),
    TASK_DESCRIPTION_INVALID("Title description must be a string up to 500 characters long"),
    PROJECT_NAME_EQUIVALENT_NOT_MODIFIED("Project name not changed, name identical to previous");

    private final String value;

    ErrorMessageConstants(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
