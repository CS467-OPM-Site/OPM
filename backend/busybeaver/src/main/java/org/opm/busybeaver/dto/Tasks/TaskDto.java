package org.opm.busybeaver.dto.Tasks;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import org.opm.busybeaver.annotations.PriorityValidation;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.ArrayList;

public final class TaskDto {

    @NotBlank(message = "Missing 'username' attribute to make a new user")
    @Size(min = 3, max = 100, message = "Task title must be 3 to 100 characters")
    private final String title;

    @Size(max = 500, message = "Description can be max 500 characters")
    private final String description;

    @Min(value = 1, message = "columnID must be a positive non-zero integer ID of the associated column")
    private final Integer columnID;

    @Min(value = 1, message = "assignedTo must be a positive non-zero integer ID of the associated user")
    private final Integer assignedTo;

    @FutureOrPresent(message="Due date must be today, or a future date, in the 'yyyy-MM-dd' format")
    @JsonFormat(pattern="yyyy-MM-dd")
    private final LocalDate dueDate;

    @PriorityValidation()
    private String priority;

    @Min(value = 1, message = "sprintID must be a positive non-zero integer ID of the associated sprint")
    private final Integer sprintID;

    private final ArrayList<String> customFields;

    public TaskDto(
            String title,
            @Nullable String description,
            @Nullable Integer columnID,
            @Nullable Integer assignedTo,
            @Nullable LocalDate dueDate,
            @Nullable String priority,
            @Nullable Integer sprintID,
            @Nullable ArrayList<String> customFields) {

        this.title = title;
        this.description = description;
        this.columnID = columnID;
        this.assignedTo = assignedTo;
        this.dueDate = dueDate;
        this.priority = priority;
        this.sprintID = sprintID;
        this.customFields = customFields;
    }

    @JsonProperty("priority")
    public void setPriority(String priority) {
        if ("foo".equals(priority)) {
            System.out.println("Found FOO!");
        }
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Integer getColumnID() {
        return columnID;
    }

    public Integer getAssignedTo() {
        return assignedTo;
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

    public ArrayList<String> getCustomFields() {
        return customFields;
    }
}
