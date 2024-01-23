package com.joo.digimon.annotation.valid;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OrderValidator.class)
@Documented
public @interface OrderValid {
    String message() default "Invalid order option";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
