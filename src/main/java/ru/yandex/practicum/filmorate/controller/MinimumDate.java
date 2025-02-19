package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Constraint;
import jakarta.validation.constraints.Past;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MinimumDateValidator.class)
@Past
public @interface MinimumDate {
    String message() default "Дата релиза должна быть не раньше 1895-12-28";
    Class<?>[] groups() default {};
    Class<?>[] payload() default {};
}
