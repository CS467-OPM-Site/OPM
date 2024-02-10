package org.opm.busybeaver.dto.Projects;

import org.opm.busybeaver.enums.BusyBeavPaths;

import java.beans.ConstructorProperties;
import java.time.LocalDateTime;

public class ProjectSummaryDto {
    private final String projectName;
    private final Integer projectID;
    private final LocalDateTime lastUpdated;
    private String projectLocation;
    private final TeamSummaryInProjectSummaryDto team;

    @ConstructorProperties({"project_name", "project_id", "last_updated", "team_id", "team_name"})
    public ProjectSummaryDto(String projectName, int projectID, LocalDateTime lastUpdated, int teamID, String teamName) {
        this.projectName = projectName;
        this.projectID = projectID;
        this.lastUpdated = lastUpdated;
        this.team = new TeamSummaryInProjectSummaryDto(teamName, teamID);
    }

    public void setProjectAndTeamLocation(String contextPath) {
        final String PATH = contextPath + BusyBeavPaths.V1.getValue();

        this.projectLocation = PATH +
                BusyBeavPaths.PROJECTS.getValue() +
                "/" + getProjectID();

        this.team.setTeamLocation(PATH);
    }

    public TeamSummaryInProjectSummaryDto getTeam() {
        return team;
    }

    public String getProjectName() {
        return projectName;
    }

    public Integer getProjectID() {
        return projectID;
    }

    public String getProjectLocation() {
        return projectLocation;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
}
