package org.opm.busybeaver.dto.Tasks;

import org.opm.busybeaver.enums.BusyBeavPaths;

import javax.annotation.Nullable;
import java.beans.ConstructorProperties;
import java.time.LocalDate;

public final class TaskCreatedDto {
    private final String title;
    private final int taskID;
    private final int columnID;
    private final String priority;
    private final String description;
    private final LocalDate dueDate;
    private final Integer sprintID;
    private final Integer assignedTo;
    private String taskLocation;

    @ConstructorProperties({
            "title", "task_id", "column_id", "priority",
            "description", "due_date",
            "sprint_id", "assigned_to" })
    public TaskCreatedDto(String title, int taskID, int columnID, String priority,
                          @Nullable String description, @Nullable LocalDate dueDate,
                          @Nullable Integer sprintID, @Nullable Integer assignedTo) {
        this.title = title;
        this.taskID = taskID;
        this.columnID = columnID;
        this.priority = priority;
        this.description = description;
        this.dueDate = dueDate;
        this.sprintID = sprintID;
        this.assignedTo = assignedTo;
    }

    public void setTaskLocation(String contextPath, int projectID) {
        final String PATH = contextPath + BusyBeavPaths.V1.getValue();

        this.taskLocation = PATH +
                BusyBeavPaths.PROJECTS.getValue() +
                "/" + projectID + BusyBeavPaths.TASKS.getValue() + "/" + getTaskID();
    }

    public String getTitle() {
        return title;
    }

    public int getTaskID() {
        return taskID;
    }

    public int getColumnID() {
        return columnID;
    }

    public String getPriority() {
        return priority;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Integer getSprintID() {
        return sprintID;
    }

    public Integer getAssignedTo() {
        return assignedTo;
    }

    public String getTaskLocation() {
        return taskLocation;
    }
}
