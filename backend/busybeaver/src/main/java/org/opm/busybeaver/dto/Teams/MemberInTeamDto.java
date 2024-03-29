package org.opm.busybeaver.dto.Teams;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.opm.busybeaver.dto.Interfaces.TeamInterface;

import java.beans.ConstructorProperties;
import java.util.Objects;

public final class MemberInTeamDto implements TeamInterface {
    @JsonIgnore
    private final String teamName;
    @JsonIgnore
    private final int teamID;
    private final String username;
    private final int userID;
    private Boolean isTeamCreator = false;

    @ConstructorProperties({"team_name", "team_id", "team_creator", "username", "user_id"})
    public MemberInTeamDto(String teamName, int teamID, int teamCreatorID, String username, int userID) {
        this.teamName = teamName;
        this.teamID = teamID;
        this.isTeamCreator = (Objects.equals(teamCreatorID, userID));
        this.username = username;
        this.userID = userID;
    }

    @Override
    public void setLocations(String contextPath) {}

    @Override
    public String getTeamName() {
        return teamName;
    }

    @Override
    public int getTeamID() {
        return teamID;
    }

    public String getUsername() {
        return username;
    }

    public int getUserID() {
        return userID;
    }

    public Boolean getIsTeamCreator() {
        return isTeamCreator;
    }
}
