package org.opm.busybeaver.dto.Sprints;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
import org.opm.busybeaver.annotations.SprintDateValidation;
import org.opm.busybeaver.dto.Interfaces.SprintInterface;

import javax.annotation.Nullable;
import java.time.LocalDate;

@SprintDateValidation
public class EditSprintDto implements SprintInterface {
    @Nullable
    @Size(min = 3, max = 50, message = "Sprint name must be between 3 and 50 characters")
    private final String sprintName;
    @Nullable
    @JsonFormat(pattern = "yyyy-MM-dd")
    @FutureOrPresent(message = "Start date must be today, or a future date, in the 'yyyy-MM-dd' format")
    private final LocalDate startDate;
    @Nullable
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Future(message = "End date must be a future date, in the 'yyyy-MM-dd' format")
    private final LocalDate endDate;

    public EditSprintDto(@Nullable String sprintName, @Nullable LocalDate startDate, @Nullable LocalDate endDate) {
        this.sprintName = sprintName;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Nullable
    @Override
    public String getSprintName() {
        return sprintName;
    }

    @Nullable
    @Override
    public LocalDate getStartDate() {
        return startDate;
    }

    @Nullable
    @Override
    public LocalDate getEndDate() {
        return endDate;
    }
}
