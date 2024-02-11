package com.girok.girokserver.domain.event.controller.validation;

import com.girok.girokserver.domain.event.controller.request.CreateEventRequest;
import com.girok.girokserver.global.enums.EventRepetitionType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.LocalTime;

public class CreateEventRequestValidator implements ConstraintValidator<ValidCreateEventRequest, CreateEventRequest> {

    @Override
    public void initialize(ValidCreateEventRequest constraintAnnotation) {
    }

    @Override
    public boolean isValid(CreateEventRequest request, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        // TODO: field @valid error handling으로 변경하기
        if (
                request.getEventDate() == null ||
                request.getEventDate().getStartDate() == null ||
                request.getName() == null
        ) {
            return true;
        }

        CreateEventRequest.EventDate eventDate = request.getEventDate();


        LocalDate startDate = eventDate.getStartDate();
        LocalTime startTime = eventDate.getStartTime();
        LocalDate endDate = eventDate.getEndDate();
        LocalTime endTime = eventDate.getEndTime();

        boolean hasStartDate = startDate != null;
        boolean hasStartTime = startTime != null;
        boolean hasEndDate = endDate != null;
        boolean hasEndTime = endTime != null;

        boolean allowRepetition = false;

        if (hasStartDate && !hasStartTime && !hasEndDate && !hasEndTime) { // [Case 1 - Single] startDate
            allowRepetition = true;
        } else if (hasStartDate && hasStartTime && !hasEndDate && !hasEndTime) { // [Case 2 - Single] startDate, startTime
            allowRepetition = true;
        } else if (hasStartDate && !hasStartTime && hasEndDate && !hasEndTime) { // [Case 3 - Multi] startDate, endDate
            if (!endDate.isAfter(startDate)) {
                context.buildConstraintViolationWithTemplate("For a multi-day event without time, 'endDate' must be greater than 'startDate'")
                        .addConstraintViolation();
                return false;
            }
        } else if (hasStartDate && hasStartTime && hasEndDate && hasEndTime) { // [Case 4 - Multi] startDate, startTime, endDate, endTime
            if (startDate.isAfter(endDate)) {
                context.buildConstraintViolationWithTemplate(("For a multi-day event with time, 'endDate' must be greater than 'startDate'"))
                        .addConstraintViolation();
                return false;
            }

            // If startDate == endDate, startTime <= endTime
            if (startDate.isEqual(endDate)) {
                if (!endTime.isAfter(startTime)) {
                    context.buildConstraintViolationWithTemplate("For a multi-day event with same 'startDate' and 'endDate', 'endTime` must be greater than 'startTime'")
                            .addConstraintViolation();
                    return false;
                }
                allowRepetition = true;
            }
            // If startDate < endDate, no constraint for startTime & endTime
        } else {
            context.buildConstraintViolationWithTemplate("Invalid event date. The allowed combinations are (1) startDate, (2) startDate, startTime, (3) startDate, endDate, and (4) startDate, startTime, endDate, endTime")
                    .addConstraintViolation();
            return false;
        }

        CreateEventRequest.Repetition repetition = request.getRepetition();
        if (repetition == null) return true;

        EventRepetitionType repetitionType = repetition.getRepetitionType();
        LocalDate repetitionEndDate = repetition.getRepetitionEndDate();

        if (repetitionType != null) {
            if (!allowRepetition) {
                context.buildConstraintViolationWithTemplate("Event repetition is only allowed for a single-day event")
                        .addConstraintViolation();
                return false;
            }

            if (repetitionEndDate != null && !repetitionEndDate.isAfter(startDate)) {
                context.buildConstraintViolationWithTemplate("Event repetition end date must be greater than `startDate`")
                        .addConstraintViolation();
                return false;
            }
        } else { // No repetition
            if (repetitionEndDate != null) {
                context.buildConstraintViolationWithTemplate("If 'repetitionType' is null, 'repetitionEndDate' cannot be specified")
                        .addConstraintViolation();
                return false;
            }
        }

        return true;
    }
}
