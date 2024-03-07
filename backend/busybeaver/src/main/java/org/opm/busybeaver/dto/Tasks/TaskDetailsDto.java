package org.opm.busybeaver.dto.Tasks;

import org.opm.busybeaver.dto.Columns.ColumnInTaskDto;
import org.opm.busybeaver.dto.Comments.CommentInTaskDto;
import org.opm.busybeaver.dto.Interfaces.TaskBasicInterface;
import org.opm.busybeaver.dto.ProjectUsers.ProjectUserShortDto;
import org.opm.busybeaver.dto.Sprints.SprintSummaryDto;
import org.opm.busybeaver.enums.BusyBeavPaths;

import javax.annotation.Nullable;
import java.beans.ConstructorProperties;
import java.time.LocalDate;
import java.util.List;

public final class TaskDetailsDto implements TaskBasicInterface {
    private final int taskID;
    private final String title;
    @Nullable
    private final String description;
    private final String priority;
    private final ColumnInTaskDto column;
    @Nullable
    private ProjectUserShortDto assignedTo;
    @Nullable
    private SprintSummaryDto sprint;
    private List<CommentInTaskDto> comments;

    private String taskLocation;

    private LocalDate dueDate;

    @ConstructorProperties({"task_id", "title", "description", "priority", "due_date",
                            "column_id", "column_title", "column_index",
                            "user_project_id", "user_id", "username",
                            "sprint_id", "begin_date", "end_date", "sprint_name"})
    public TaskDetailsDto(int taskID, String title, @Nullable String description, String priority, LocalDate dueDate,
                          int columnID, String columnTitle, int columnIndex,
                          Integer userProjectID, Integer userID, String username,
                          Integer sprintID, LocalDate startDate, LocalDate endDate, String sprintName) {
        this.taskID = taskID;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;

        this.column = new ColumnInTaskDto(columnTitle, columnID, columnIndex);

        if (userID != null && username != null) {
            this.assignedTo = new ProjectUserShortDto(username, userID, userProjectID);
        }

        if (sprintID != null) {
            this.sprint = new SprintSummaryDto(sprintID, sprintName, startDate, endDate);
        }
    }

    public void setComments(List<CommentInTaskDto> comments) {
        this.comments = comments;
    }

    public void setTaskLocation(String contextPath, int projectID) {
        final String PATH = contextPath + BusyBeavPaths.V1.getValue();

        this.taskLocation = PATH +
                BusyBeavPaths.PROJECTS.getValue() +
                "/" + projectID + BusyBeavPaths.TASKS.getValue() + "/" + getTaskID();

        column.setColumnLocation(contextPath, projectID);

        if (sprint != null) {
            sprint.setSprintLocation(contextPath, projectID);
        }

        if (!comments.isEmpty()) {
            comments.forEach(comment -> comment.setCommentLocation(contextPath, projectID, getTaskID()));
        }
    }

    @Override
    public String getTitle() {
        return title;
    }

    public int getTaskID() {
        return taskID;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    @Override
    public String getPriority() {
        return priority;
    }

    @Override
    public LocalDate getDueDate() {
        return dueDate;
    }

    public ColumnInTaskDto getColumn() {
        return column;
    }

    @Nullable
    public ProjectUserShortDto getAssignedTo() {
        return assignedTo;
    }

    @Nullable
    public SprintSummaryDto getSprint() {
        return sprint;
    }

    public List<CommentInTaskDto> getComments() {
        return comments;
    }

    public String getTaskLocation() {
        return taskLocation;
    }
}
