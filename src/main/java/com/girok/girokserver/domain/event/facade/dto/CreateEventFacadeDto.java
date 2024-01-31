package com.girok.girokserver.domain.event.facade.dto;

import com.girok.girokserver.global.enums.EventPriority;
import com.girok.girokserver.global.enums.EventRepetitionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
public class CreateEventFacadeDto {

    private Long categoryId;
    String name;
    private LocalDate startDate;
    private LocalTime startTime;
    private LocalDate endDate;
    private LocalTime endTime;
    private List<String> tags;
    private EventPriority priority;
    private String memo;
    private EventRepetitionType repetitionType;
    private LocalDate repetitionEndDate;

}
