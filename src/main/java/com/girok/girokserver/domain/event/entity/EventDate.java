package com.girok.girokserver.domain.event.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class EventDate {

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "end_time")
    private LocalTime endTime;

    public boolean isSingleDayEvent() {
        boolean hasStartDate = startDate != null;
        boolean hasStartTime = startTime != null;
        boolean hasEndDate = endDate != null;
        boolean hasEndTime = endTime != null;

        return (hasStartDate && !hasStartTime && !hasEndDate && !hasEndTime) ||
                (hasStartDate && hasStartTime && !hasEndDate && !hasEndTime) ||
                (hasStartDate && hasStartTime && hasEndDate && hasEndTime && startDate.isEqual(endDate));
    }
}
