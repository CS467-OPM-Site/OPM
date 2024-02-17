package org.opm.busybeaver.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.opm.busybeaver.dto.Sprints.NewSprintDto;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.exceptions.Sprints.SprintsExceptions;

public class SprintDateValidator implements ConstraintValidator<SprintDateValidation, NewSprintDto> {

    @Override
    public void initialize(SprintDateValidation sprintDateValidation){}

    @Override
    public boolean isValid(NewSprintDto newSprintDto, ConstraintValidatorContext context) {
        if (newSprintDto == null) {
            return true; // Let @NotNull handle null check
        }

        if (newSprintDto.startDate() != null && newSprintDto.endDate() != null &&
                (newSprintDto.startDate().isAfter(newSprintDto.endDate()) ||
                        newSprintDto.startDate().isEqual(newSprintDto.endDate()))) {
            throw new SprintsExceptions.SprintDatesInvalid(ErrorMessageConstants.SPRINT_DATES_INVALID.getValue());
        }

        return true;
    }
}
