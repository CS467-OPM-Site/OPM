package org.opm.busybeaver.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.opm.busybeaver.enums.DatabaseConstants;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;

@Target({FIELD, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = TaskPriorityValidator.class)
public @interface TaskPriorityValidation {
    public String message() default
            "Invalid priority value: must be '" + DatabaseConstants.Constants.PRIORITY_HIGH +
                    "', '" + DatabaseConstants.Constants.PRIORITY_MEDIUM +
                    "', '" + DatabaseConstants.Constants.PRIORITY_LOW +
                    "', or '" + DatabaseConstants.Constants.PRIORITY_NONE + "'";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};

}
