package com.girok.girokserver.domain.event.controller.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CreateEventRequestValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCreateEventRequest {
    String message() default "Invalid event create request";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
