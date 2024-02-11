package com.girok.girokserver.domain.event.controller;

import com.girok.girokserver.domain.event.controller.request.CreateEventRequest;
import com.girok.girokserver.domain.event.facade.dto.CreateEventFacadeDto;

public class EventMapper {

    public static CreateEventFacadeDto toCreateEventFacadeDto(CreateEventRequest request) {
        CreateEventRequest.EventDate eventDate = request.getEventDate();
        CreateEventRequest.Repetition repetition = request.getRepetition();

        return CreateEventFacadeDto.builder()
                .categoryId(request.getCategoryId())
                .name(request.getName())
                .startDate(eventDate.getStartDate())
                .startTime(eventDate.getStartTime())
                .endDate(eventDate.getEndDate())
                .endTime(eventDate.getEndTime())
                .tags(request.getTags())
                .priority(request.getPriority())
                .memo(request.getMemo())
                .repetitionType(repetition != null ? repetition.getRepetitionType() : null)
                .repetitionEndDate(repetition != null ? repetition.getRepetitionEndDate() : null)
                .build();
    }
}
