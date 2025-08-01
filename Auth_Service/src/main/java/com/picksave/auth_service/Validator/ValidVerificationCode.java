package com.picksave.auth_service.Validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = VerificationCodeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidVerificationCode {
    String message() default "Verification code is required. Check your mailbox";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

