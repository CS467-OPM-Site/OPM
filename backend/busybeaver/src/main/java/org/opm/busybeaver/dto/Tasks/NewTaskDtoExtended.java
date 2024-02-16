package org.opm.busybeaver.dto.Tasks;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import org.opm.busybeaver.annotations.PriorityValidation;
import org.opm.busybeaver.dto.Interfaces.TaskExtendedInterface;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.ArrayList;

public final class NewTaskDtoExtended implements TaskExtendedInterface {

    @NotBlank(message = "Missing 'username' attribute to make a new user")
    @Size(min = 3, max = 100, message = "Task title must be 3 to 100 characters")
    private final String title;

    @Size(max = 500, message = "Description can be max 500 characters")
    private final String description;

    @Min(value = 1, message = "columnID must be a positive non-zero integer ID of the associated column")
    private Integer columnID;

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


    public NewTaskDtoExtended(
            String title,
            @Nullable Integer columnID,
            @Nullable String description,
            @Nullable LocalDate dueDate,
            @Nullable Integer assignedTo,
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

    public void setColumnID(int columnID) {
        this.columnID = columnID;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Integer getColumnID() {
        return columnID;
    }

    @Override
    public Integer getAssignedTo() {
        return assignedTo;
    }

    @Override
    public LocalDate getDueDate() {
        return dueDate;
    }

    @Override
    public String getPriority() {
        return priority;
    }

    @Override
    public Integer getSprintID() {
        return sprintID;
    }

    public ArrayList<String> getCustomFields() {
        return customFields;
    }
}
