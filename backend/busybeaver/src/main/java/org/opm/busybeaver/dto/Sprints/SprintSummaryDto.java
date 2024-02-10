package org.opm.busybeaver.dto.Sprints;

import org.opm.busybeaver.enums.BusyBeavPaths;

import java.time.LocalDate;

public final class SprintSummaryDto {
    private final int sprintID;
    private final String sprintName;
    private final LocalDate endDate;
    private String sprintLocation;

    public SprintSummaryDto(int sprintID, String sprintName, LocalDate endDate) {
        this.sprintID = sprintID;
        this.sprintName = sprintName;
        this.endDate = endDate;
    }

    public void setSprintLocation(String contextPath, int projectID) {
        final String PATH = contextPath + BusyBeavPaths.V1.getValue();

        this.sprintLocation = PATH +
                BusyBeavPaths.PROJECTS.getValue() +
                "/" + projectID + BusyBeavPaths.SPRINTS.getValue() + "/" + getSprintID();
    }

    public int getSprintID() {
        return sprintID;
    }

    public String getSprintName() {
        return sprintName;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getSprintLocation() {
        return sprintLocation;
    }
}
