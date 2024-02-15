package org.opm.busybeaver.dto.Sprints;

import org.opm.busybeaver.enums.BusyBeavPaths;

import java.time.LocalDate;

public final class SprintInTaskDto {
    private final int sprintID;
    private final String sprintName;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private String sprintLocation;

    public SprintInTaskDto(int sprintID, String sprintName, LocalDate startDate, LocalDate endDate) {
        this.sprintID = sprintID;
        this.sprintName = sprintName;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void setSprintLocation(String contextPath, int projectID) {
        final String PATH = contextPath + BusyBeavPaths.V1.getValue();

        this.sprintLocation = PATH +
                BusyBeavPaths.PROJECTS.getValue() +
                "/" + projectID + BusyBeavPaths.SPRINTS.getValue() + "/" + getSprintID();
    }

    public LocalDate getStartDate () {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getSprintName() {
        return sprintName;
    }

    public int getSprintID() {
        return sprintID;
    }

    public String getSprintLocation() {
        return sprintLocation;
    }
}
