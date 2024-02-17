package org.opm.busybeaver.dto.Sprints;

import org.opm.busybeaver.dto.Interfaces.SprintInterface;
import org.opm.busybeaver.enums.BusyBeavPaths;

import java.beans.ConstructorProperties;
import java.time.LocalDate;

public final class SprintSummaryDto implements SprintInterface {
    private final int sprintID;
    private final String sprintName;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private String sprintLocation;

    @ConstructorProperties({"sprint_id", "sprint_name", "begin_date", "end_date"})
    public SprintSummaryDto(int sprintID, String sprintName, LocalDate startDate, LocalDate endDate) {
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

    @Override
    public LocalDate getStartDate() { return startDate; }

    @Override
    public LocalDate getEndDate() { return endDate; }

    @Override
    public String getSprintName() { return sprintName; }

    public int getSprintID() {
        return sprintID;
    }

    public String getSprintLocation() {
        return sprintLocation;
    }
}
