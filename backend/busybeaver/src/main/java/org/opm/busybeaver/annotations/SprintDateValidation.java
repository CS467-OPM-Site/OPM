package org.opm.busybeaver.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SprintDateValidator.class)
public @interface SprintDateValidation {
    String message() default "Start date must be after end date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
