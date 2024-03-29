package org.opm.busybeaver.dto.Projects;

import org.opm.busybeaver.dto.Interfaces.ProjectAndTeamInterface;
import org.opm.busybeaver.enums.BusyBeavPaths;

import java.beans.ConstructorProperties;
import java.time.OffsetDateTime;

public final class ProjectSummaryDto implements ProjectAndTeamInterface {
    private final String projectName;
    private final int projectID;
    private final OffsetDateTime lastUpdated;
    private String projectLocation;
    private final TeamSummaryInProjectSummaryDto team;

    @ConstructorProperties({"project_name", "project_id", "last_updated", "team_id", "team_name"})
    public ProjectSummaryDto(String projectName, int projectID, OffsetDateTime lastUpdated, int teamID, String teamName) {
        this.projectName = projectName;
        this.projectID = projectID;
        this.lastUpdated = lastUpdated;
        this.team = new TeamSummaryInProjectSummaryDto(teamName, teamID);
    }

    @Override
    public void setLocations(String contextPath) {
        final String PATH = contextPath + BusyBeavPaths.V1.getValue();

        this.projectLocation = PATH +
                BusyBeavPaths.PROJECTS.getValue() +
                "/" + getProjectID();

        this.team.setTeamLocation(PATH);
    }

    @Override
    public TeamSummaryInProjectSummaryDto getTeam() {
        return team;
    }

    @Override
    public String getProjectName() {
        return projectName;
    }

    @Override
    public int getProjectID() {
        return projectID;
    }

    @Override
    public String getProjectLocation() {
        return projectLocation;
    }

    @Override
    public OffsetDateTime getLastUpdated() {
        return lastUpdated;
    }
}
