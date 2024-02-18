package org.opm.busybeaver.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.opm.busybeaver.dto.Interfaces.SprintInterface;
import org.opm.busybeaver.dto.Sprints.NewSprintDto;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.exceptions.Sprints.SprintsExceptions;

public class SprintDateValidator implements ConstraintValidator<SprintDateValidation, SprintInterface> {

    @Override
    public void initialize(SprintDateValidation sprintDateValidation){}

    @Override
    public boolean isValid(SprintInterface newSprintDto, ConstraintValidatorContext context) {
        if (newSprintDto == null) {
            return true; // Let @NotNull handle null check
        }

        if (newSprintDto.getStartDate() != null && newSprintDto.getEndDate() != null &&
                (newSprintDto.getStartDate().isAfter(newSprintDto.getEndDate()) ||
                        newSprintDto.getStartDate().isEqual(newSprintDto.getEndDate()))) {
            throw new SprintsExceptions.SprintDatesInvalid(ErrorMessageConstants.SPRINT_DATES_INVALID.getValue());
        }

        return true;
    }
}
