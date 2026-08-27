package com.infoworks.domain.constraint.Gender;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = GenderConstraint.class)
public @interface IsValidGender {
    String message() default "e.g. MALE or FEMALE or TRANSGENDER or NONE";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
