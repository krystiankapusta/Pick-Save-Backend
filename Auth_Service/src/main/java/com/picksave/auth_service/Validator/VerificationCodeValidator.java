package com.picksave.auth_service.Validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class VerificationCodeValidator implements ConstraintValidator<ValidVerificationCode, String> {

    @Override
    public boolean isValid(String code, ConstraintValidatorContext context) {
        return code != null && !code.trim().isEmpty() && code.length() == 6;
    }
}
