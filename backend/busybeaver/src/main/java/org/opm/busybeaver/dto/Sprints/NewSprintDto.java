package org.opm.busybeaver.dto.Sprints;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import org.opm.busybeaver.annotations.SprintDateValidation;
import org.opm.busybeaver.dto.Interfaces.SprintInterface;

import java.time.LocalDate;

@SprintDateValidation
public record NewSprintDto(
        @NotBlank(message = "Missing 'sprintName' attribute to generate a new sprint")
        @Size(min = 3, max = 50, message = "Sprint name must be between 3 and 50 characters")
        String sprintName,
        @NotNull(message = "Missing 'startDate' attribute in 'yyyy-MM-dd' format")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate,
        @NotNull(message = "Missing 'startDate' attribute in 'yyyy-MM-dd' format")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate endDate) implements SprintInterface {
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
}
