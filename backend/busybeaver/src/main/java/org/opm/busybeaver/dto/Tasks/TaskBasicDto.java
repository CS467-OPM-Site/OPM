package org.opm.busybeaver.dto.Tasks;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.opm.busybeaver.dto.Interfaces.TaskBasicInterface;
import org.opm.busybeaver.dto.Sprints.SprintSummaryDto;
import org.opm.busybeaver.dto.Users.UserSummaryDto;
import org.opm.busybeaver.enums.BusyBeavPaths;

import javax.annotation.Nullable;
import java.beans.ConstructorProperties;
import java.time.LocalDate;

public final class TaskBasicDto implements TaskBasicInterface {
    private final String title;
    private final int taskID;
    private final String priority;
    @Nullable
    private final LocalDate dueDate;
    private final int comments;
    private final int taskIndex;
    @Nullable
    private UserSummaryDto assignedTo;
    @Nullable
    private SprintSummaryDto sprint;
    private String taskLocation;
    @JsonIgnore
    private final int columnIndex;

    @ConstructorProperties({
            "title", "task_id", "priority", "due_date", "task_index","comments",
            "sprint_name", "end_date", "sprint_id", "assigned_to", "username", "column_index"})
    public TaskBasicDto(
            String title, int taskID, String priority, @Nullable LocalDate dueDate, int taskIndex, int comments,
            @Nullable String sprintName, @Nullable LocalDate endDate, @Nullable Integer sprintID,
            @Nullable Integer assignedToId, @Nullable String username, int columnIndex) {

        this.title = title;
        this.taskID = taskID;
        this.priority = priority;
        this.dueDate = dueDate;
        this.taskIndex = taskIndex;
        this.comments = comments;
        this.columnIndex = columnIndex;

        // Add sprint if not any of sprint properties are null
        if (sprintName != null && endDate != null && sprintID != null) {
            this.sprint = new SprintSummaryDto(sprintID, sprintName, endDate);
        }

        // Add assigned to if not any of the assignedTo properties are null
        if (assignedToId != null && username != null) {
            this.assignedTo = new UserSummaryDto(username, assignedToId);
        }
    }

    public void setTaskLocation(String contextPath, int projectID) {
        final String PATH = contextPath + BusyBeavPaths.V1.getValue();

        this.taskLocation = PATH +
                BusyBeavPaths.PROJECTS.getValue() +
                "/" + projectID + BusyBeavPaths.TASKS.getValue() + "/" + getTaskID();

        if (sprint != null) {
            sprint.setSprintLocation(contextPath, projectID);
        }
    }

    @Override
    public String getTitle() {
        return title;
    }

    public int getTaskID() {
        return taskID;
    }

    @Override
    public String getPriority() {
        return priority;
    }

    @Nullable
    @Override
    public LocalDate getDueDate() {
        return dueDate;
    }

    public int getComments() {
        return comments;
    }

    public int getTaskIndex() {
        return taskIndex;
    }

    @Nullable
    public UserSummaryDto getAssignedTo() {
        return assignedTo;
    }

    @Nullable
    public SprintSummaryDto getSprint() {
        return sprint;
    }

    public String getTaskLocation() {
        return taskLocation;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

}
