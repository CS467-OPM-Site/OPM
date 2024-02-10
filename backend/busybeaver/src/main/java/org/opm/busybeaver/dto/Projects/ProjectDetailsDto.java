package org.opm.busybeaver.dto.Projects;

import org.opm.busybeaver.dto.Columns.ColumnDto;
import org.opm.busybeaver.enums.BusyBeavPaths;

import java.beans.ConstructorProperties;
import java.util.List;

public class ProjectDetailsDto {
    private final String projectName;
    private final int projectID;
    private final TeamSummaryInProjectSummaryDto team;
    private String projectLocation;

    private List<ColumnDto> columns;

    @ConstructorProperties({"project_name", "project_id", "team_id", "team_name"})
    public ProjectDetailsDto(String projectName, int projectID, int teamID, String teamName) {
        this.projectName = projectName;
        this.projectID = projectID;
        this.team = new TeamSummaryInProjectSummaryDto(teamName, teamID);
    }

    public void setProjectTeamColumnTaskLocation(String contextPath) {
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

    public String getProjectName() {
        return projectName;
    }

    public int getProjectID() {
        return projectID;
    }

    public String getProjectLocation() {
        return projectLocation;
    }

    public TeamSummaryInProjectSummaryDto getTeam() {
        return team;
    }

    public List<ColumnDto> getColumns() {
        return columns;
    }
}
