package org.opm.busybeaver.dto.Tasks;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.opm.busybeaver.dto.Columns.ColumnDto;
import org.opm.busybeaver.dto.Columns.ColumnInTaskDto;
import org.opm.busybeaver.dto.Interfaces.TaskBasicInterface;
import org.opm.busybeaver.dto.Sprints.SprintSummaryDto;
import org.opm.busybeaver.dto.Users.UserSummaryDto;
import org.opm.busybeaver.enums.BusyBeavPaths;

import javax.annotation.Nullable;
import java.beans.ConstructorProperties;
import java.time.LocalDate;

public final class TaskBasicInSprintDto implements TaskBasicInterface {
    private final String title;
    private final int taskID;
    private final String priority;
    @Nullable
    private final LocalDate dueDate;
    private final int comments;
    private final String description;
    private ColumnInTaskDto column;
    @Nullable
    private UserSummaryDto assignedTo;
    private String taskLocation;

    @ConstructorProperties({
            "title", "task_id", "priority", "due_date", "description", "comments",
            "assigned_to", "username", "column_index", "column_title", "column_id"})
    public TaskBasicInSprintDto(
            String title, int taskID, String priority, @Nullable LocalDate dueDate, String description, int comments,
            @Nullable Integer assignedToId, @Nullable String username,
            int columnIndex, String columnTitle, int columnID) {

        this.title = title;
        this.taskID = taskID;
        this.priority = priority;
        this.dueDate = dueDate;
        this.description = description;
        this.comments = comments;

        this.column = new ColumnInTaskDto(columnTitle, columnID, columnIndex);

        if (assignedToId != null && username != null) {
            this.assignedTo = new UserSummaryDto(username, assignedToId);
        }
    }

    public void setTaskLocation(String contextPath, int projectID) {
        final String PATH = contextPath + BusyBeavPaths.V1.getValue();

        this.taskLocation = PATH +
                BusyBeavPaths.PROJECTS.getValue() +
                "/" + projectID + BusyBeavPaths.TASKS.getValue() + "/" + getTaskID();

        column.setColumnLocation(contextPath, projectID);
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

    public String getDescription() {
        return description;
    }

    public ColumnInTaskDto getColumn() {
        return column;
    }

    @Nullable
    public UserSummaryDto getAssignedTo() {
        return assignedTo;
    }

    public int getComments() {
        return comments;
    }

    public String getTaskLocation() {
        return taskLocation;
    }

}
