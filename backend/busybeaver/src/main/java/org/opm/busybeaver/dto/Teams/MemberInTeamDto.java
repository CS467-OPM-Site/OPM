package org.opm.busybeaver.dto.Teams;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.beans.ConstructorProperties;
import java.util.Objects;

public class MemberInTeamDto {
    @JsonIgnore
    private final String teamName;
    @JsonIgnore
    private final Integer teamID;
    private final String username;
    private final Integer userID;
    private Boolean isTeamCreator = false;

    @ConstructorProperties({"team_name", "team_id", "team_creator", "username", "user_id"})
    public MemberInTeamDto(String teamName, Integer teamID, Integer teamCreatorID, String username, Integer userID) {
        this.teamName = teamName;
        this.teamID = teamID;
        this.isTeamCreator = (Objects.equals(teamCreatorID, userID));
        this.username = username;
        this.userID = userID;
    }

    public String getTeamName() {
        return teamName;
    }

    public Integer getTeamID() {
        return teamID;
    }

    public String getUsername() {
        return username;
    }

    public Integer getUserID() {
        return userID;
    }

    public Boolean getIsTeamCreator() {
        return isTeamCreator;
    }
}
