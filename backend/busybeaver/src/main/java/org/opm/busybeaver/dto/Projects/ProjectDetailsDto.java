package org.opm.busybeaver.dto.Projects;

import org.opm.busybeaver.dto.Columns.ColumnDto;
import org.opm.busybeaver.dto.Interfaces.ProjectAndTeamInterface;
import org.opm.busybeaver.enums.BusyBeavPaths;

import java.beans.ConstructorProperties;
import java.time.OffsetDateTime;
import java.util.List;

public final class ProjectDetailsDto implements ProjectAndTeamInterface {
    private final String projectName;
    private final int projectID;
    private final TeamSummaryInProjectSummaryDto team;
    private String projectLocation;
    private List<ColumnDto> columns;
    public final OffsetDateTime lastUpdated;

    @ConstructorProperties({"project_name", "project_id", "team_id", "team_name", "last_updated"})
    public ProjectDetailsDto(String projectName, int projectID, int teamID, String teamName, OffsetDateTime lastUpdated) {
        this.projectName = projectName;
        this.projectID = projectID;
        this.team = new TeamSummaryInProjectSummaryDto(teamName, teamID);
        this.lastUpdated = lastUpdated;
    }

    @Override
    public void setLocations(String contextPath) {
        final String PATH = contextPath + BusyBeavPaths.V1.getValue();

        this.projectLocation = PATH +
                BusyBeavPaths.PROJECTS.getValue() +
                "/" + getProjectID();

        this.team.setTeamLocation(PATH);

        if (!columns.isEmpty()) {
            columns.forEach(column -> column.setColumnLocation(contextPath, projectID));
        }
    }
    public void setColumns(List<ColumnDto> columns) {
        this.columns = columns;
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
    public TeamSummaryInProjectSummaryDto getTeam() {
        return team;
    }

    @Override
    public OffsetDateTime getLastUpdated() {
        return lastUpdated;
    }

    public List<ColumnDto> getColumns() {
        return columns;
    }
}
