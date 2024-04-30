package com.joo.digimon.global.annotation.valid;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.HashSet;
import java.util.Set;

public class OrderValidator implements ConstraintValidator<OrderValid, String> {

    private Set<String> options;
    @Override
    public void initialize(OrderValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        options = new HashSet<>();
        options.add("cardNo");
        options.add("cardName");
        options.add("dp");
        options.add("playCost");
        options.add("lv");

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return options.contains(value);
    }
}
