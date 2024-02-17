package org.opm.busybeaver.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.opm.busybeaver.enums.DatabaseConstants;

import javax.annotation.Nullable;
import java.util.Set;

public class TaskPriorityValidator implements ConstraintValidator<TaskPriorityValidation, String> {

    public static final Set<String> ALLOWED_PRIORITIES = Set.of(
            DatabaseConstants.PRIORITY_HIGH.getValue(),
            DatabaseConstants.PRIORITY_MEDIUM.getValue(),
            DatabaseConstants.PRIORITY_LOW.getValue(),
            DatabaseConstants.PRIORITY_NONE.getValue()
    );

    public boolean isValid(@Nullable String priority, ConstraintValidatorContext context) {
        if (priority == null) return true;

        return ALLOWED_PRIORITIES.contains(priority);
    }
}
