package org.opm.busybeaver.dto.Sprints;

import org.opm.busybeaver.dto.Interfaces.SprintInterface;
import org.opm.busybeaver.dto.Tasks.TaskBasicInSprintDto;
import org.opm.busybeaver.enums.BusyBeavPaths;

import java.beans.ConstructorProperties;
import java.time.LocalDate;
import java.util.List;

public class TasksInSprintDto implements SprintInterface {

    private final int sprintID;
    private final String sprintName;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private String sprintLocation;
    private List<TaskBasicInSprintDto> tasks;

    @ConstructorProperties({"sprint_id", "sprint_name", "begin_date", "end_date"})
    public TasksInSprintDto(int sprintID, String sprintName, LocalDate startDate, LocalDate endDate) {
        this.sprintID = sprintID;
        this.sprintName = sprintName;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void setTasks(List<TaskBasicInSprintDto> tasks) {
        this.tasks = tasks;
    }

    public void setSprintLocation(String contextPath, int projectID) {
        final String PATH = contextPath + BusyBeavPaths.V1.getValue();

        this.sprintLocation = PATH +
                BusyBeavPaths.PROJECTS.getValue() +
                "/" + projectID + BusyBeavPaths.SPRINTS.getValue() + "/" + getSprintID();

        tasks.forEach(task -> task.setTaskLocation(contextPath, projectID));
    }

    public int getSprintID() {
        return sprintID;
    }

    @Override
    public String getSprintName() {
        return sprintName;
    }

    @Override
    public LocalDate getStartDate() {
        return startDate;
    }

    @Override
    public LocalDate getEndDate() {
        return endDate;
    }

    public String getSprintLocation() {
        return sprintLocation;
    }

    public List<TaskBasicInSprintDto> getTasks() {
        return tasks;
    }
}
