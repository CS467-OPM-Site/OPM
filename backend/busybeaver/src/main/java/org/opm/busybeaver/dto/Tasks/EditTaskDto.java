package org.opm.busybeaver.dto.Tasks;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.opm.busybeaver.annotations.TaskPriorityValidation;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.Optional;

public final class EditTaskDto {
    private final String title;

    private String description;
    @Nullable
    private final LocalDate dueDate;
    @TaskPriorityValidation()
    private final String priority;
    @Nullable
    private final Integer sprintID;
    @Nullable
    private final Integer assignedTo;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private final int columnID;

    public EditTaskDto(String title, @Nullable String description, @Nullable LocalDate dueDate, String priority, @Nullable Integer sprintID, @Nullable Integer assignedTo, int columnID) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.sprintID = sprintID;
        this.assignedTo = assignedTo;
        this.columnID = columnID;
    }


    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public String getPriority() {
        return priority;
    }

    public Integer getSprintID() {
        return sprintID;
    }

    public Integer getAssignedTo() {
        return assignedTo;
    }

    public Integer getColumnID() {
        return columnID;
    }
}
