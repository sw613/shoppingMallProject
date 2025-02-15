package com.project.elice2.users.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AgreeToWithdrawalValidator implements ConstraintValidator<MustBeTrue, Boolean> {

    @Override
    public boolean isValid(Boolean value, ConstraintValidatorContext context) {
        return value != null && value;
    }
}
