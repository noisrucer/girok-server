package com.girok.girokserver.domain.event.service;

import com.girok.girokserver.core.exception.CustomException;
import com.girok.girokserver.core.exception.ErrorInfo;
import com.girok.girokserver.domain.category.entity.Category;
import com.girok.girokserver.domain.event.controller.request.EventFilterCriteria;
import com.girok.girokserver.domain.event.entity.Event;
import com.girok.girokserver.domain.event.entity.EventDate;
import com.girok.girokserver.domain.event.facade.dto.CreateEventFacadeDto;
import com.girok.girokserver.domain.event.repository.EventRepository;
import com.girok.girokserver.domain.member.entity.Member;
import com.girok.girokserver.global.enums.EventPriority;
import com.girok.girokserver.global.enums.EventRepetitionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.girok.girokserver.core.exception.ErrorInfo.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;

    @Transactional
    public Long createEvent(
            Member member,
            Category category,
            CreateEventFacadeDto createEventFacadeDto
    ) {
        EventDate eventDate = EventDate.builder()
                .startDate(createEventFacadeDto.getStartDate())
                .startTime(createEventFacadeDto.getStartTime())
                .endDate(createEventFacadeDto.getEndDate())
                .endTime(createEventFacadeDto.getEndTime())
                .build();

        Event event = Event.createEvent(
                createEventFacadeDto.getName(),
                category,
                member,
                eventDate,
                createEventFacadeDto.getRepetitionType(),
                createEventFacadeDto.getRepetitionEndDate(),
                createEventFacadeDto.getTags(),
                createEventFacadeDto.getPriority(),
                createEventFacadeDto.getMemo()
        );

        eventRepository.save(event);
        return event.getId();
    }

    public List<Event> getAllEvents(Member member, EventFilterCriteria criteria) {
        return new ArrayList<>();
    }

    public Event getSingleEvent(Member member, Long eventId) {
        return eventRepository.findByMemberAndId(member, eventId)
                .orElseThrow(() -> new CustomException(EVENT_NOT_FOUND));
    }

    @Transactional
    public void deleteEvent(Long memberId, Long eventId) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            return;
        }

        Event event = optionalEvent.get();
        if (!Objects.equals(event.getMember().getId(), memberId)) {
            throw new CustomException(UNAUTHORIZED_OPERATION_EXCEPTION);
        }

        eventRepository.delete(event);
    }
}
