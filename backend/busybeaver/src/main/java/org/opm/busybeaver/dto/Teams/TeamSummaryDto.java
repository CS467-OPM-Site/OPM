package org.opm.busybeaver.dto.Teams;

import org.opm.busybeaver.enums.BusyBeavPaths;

import java.beans.ConstructorProperties;
import java.util.Objects;

public class TeamSummaryDto {
    private final Integer teamID;
    private final String teamName;
    private String teamLocation;
    private Boolean isTeamCreator;
    private final Integer teamCreatorId;

    @ConstructorProperties({"team_id", "team_name", "team_creator"})
    public TeamSummaryDto(Integer teamID, String teamName, Integer teamCreatorId) {
        this.teamID = teamID;
        this.teamName = teamName;
        this.teamCreatorId = teamCreatorId;
    }

    public void setTeamLocation(String contextPath) {
        final String PATH = contextPath + BusyBeavPaths.V1.getValue();

        this.teamLocation = PATH + BusyBeavPaths.TEAMS.getValue() + "/" + getTeamID();
    }

    public void setIsTeamCreator(Integer userID) {
        isTeamCreator = (Objects.equals(userID, getTeamCreatorId()));
    }

    public Integer getTeamID() {
        return teamID;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getTeamLocation() {
        return teamLocation;
    }

    public Boolean getIsTeamCreator() {
        return isTeamCreator;
    }

    private Integer getTeamCreatorId() {
        return teamCreatorId;
    }
}
