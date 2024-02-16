package org.opm.busybeaver.dto.Teams;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.opm.busybeaver.dto.Interfaces.TeamInterface;
import org.opm.busybeaver.enums.BusyBeavPaths;

import javax.annotation.Nullable;
import java.beans.ConstructorProperties;
import java.time.LocalDateTime;

public class ProjectByTeamDto implements TeamInterface {

    @JsonIgnore
    private final String teamName;
    @JsonIgnore
    private final int teamID;
    private final String projectName;
    @Nullable
    private final Integer projectID;
    private final LocalDateTime lastUpdated;
    private String projectLocation;

    @ConstructorProperties({"team_name", "team_id", "project_name", "project_id", "last_updated"})
    public ProjectByTeamDto(String teamName, int teamID, String projectName, @Nullable Integer projectID, LocalDateTime lastUpdated) {
        this.teamName = teamName;
        this.teamID = teamID;
        this.projectName = projectName;
        this.projectID = projectID;
        this.lastUpdated = lastUpdated;
    }

    @Override
    public void setLocations(String contextPath) {
        final String PATH = contextPath + BusyBeavPaths.V1.getValue();

        this.projectLocation = PATH +
                BusyBeavPaths.PROJECTS.getValue() +
                "/" + getProjectID();
    }

    @Override
    public String getTeamName() {
        return teamName;
    }

    @Override
    public int getTeamID() {
        return teamID;
    }

    public String getProjectName() {
        return projectName;
    }

    @Nullable
    public Integer getProjectID() {
        return projectID;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public String getProjectLocation() {
        return projectLocation;
    }
}
