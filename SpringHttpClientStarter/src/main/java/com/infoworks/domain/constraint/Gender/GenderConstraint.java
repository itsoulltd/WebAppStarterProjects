package com.infoworks.domain.constraint.Gender;

import com.infoworks.domain.models.Gender;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class GenderConstraint implements ConstraintValidator<IsValidGender, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        if (!value.isEmpty()){
            try {
                return Gender.valueOf(value) != null;
            } catch (IllegalArgumentException e) {}
        }
        return false;
    }
}
