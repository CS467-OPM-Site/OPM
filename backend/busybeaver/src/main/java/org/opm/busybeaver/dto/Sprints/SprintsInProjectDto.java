package org.opm.busybeaver.dto.Sprints;


import org.opm.busybeaver.dto.Interfaces.ProjectBasicInterface;
import org.opm.busybeaver.enums.BusyBeavPaths;

import java.beans.ConstructorProperties;
import java.util.List;

public final class SprintsInProjectDto implements ProjectBasicInterface {
    private final int projectID;
    private final String projectName;
    private String projectLocation;
    private List<SprintSummaryDto> sprints;

    @ConstructorProperties({"project_name", "project_id"})
    public SprintsInProjectDto(String projectName, int projectID) {
        this.projectName = projectName;
        this.projectID = projectID;
    }

    public void setSprints(List<SprintSummaryDto> sprints) {
        this.sprints = sprints;
    }

    @Override
    public void setLocations(String contextPath) {
        final String PATH = contextPath + BusyBeavPaths.V1.getValue();

        this.projectLocation = PATH +
                BusyBeavPaths.PROJECTS.getValue() + "/" + projectID;

        sprints.forEach(sprint -> sprint.setSprintLocation(contextPath, projectID));
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

    public List<SprintSummaryDto> getSprints() {
        return sprints;
    }
}
