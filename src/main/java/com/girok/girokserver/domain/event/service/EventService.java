package com.girok.girokserver.domain.event.service;

import com.girok.girokserver.core.exception.CustomException;
import com.girok.girokserver.domain.category.entity.Category;
import com.girok.girokserver.domain.category.repository.CategoryRepository;
import com.girok.girokserver.domain.event.controller.request.EventFilterCriteria;
import com.girok.girokserver.domain.event.entity.Event;
import com.girok.girokserver.domain.event.entity.EventDate;
import com.girok.girokserver.domain.event.facade.dto.CreateEventFacadeDto;
import com.girok.girokserver.domain.event.repository.EventQueryRepository;
import com.girok.girokserver.domain.event.repository.EventRepository;
import com.girok.girokserver.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.girok.girokserver.core.exception.ErrorInfo.EVENT_NOT_FOUND;
import static com.girok.girokserver.core.exception.ErrorInfo.UNAUTHORIZED_OPERATION_EXCEPTION;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final EventQueryRepository eventQueryRepository;
    private final CategoryRepository categoryRepository;

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

    @Transactional
    public void updateEvent(
            Member member,
            Long eventId,
            Category category,
            CreateEventFacadeDto updateEventFacadeDto
    ) {
        Event event = eventRepository.findByMemberAndId(member, eventId)
                .orElseThrow(() -> new CustomException(EVENT_NOT_FOUND));

        EventDate eventDate = EventDate.builder()
                .startDate(updateEventFacadeDto.getStartDate())
                .startTime(updateEventFacadeDto.getStartTime())
                .endDate(updateEventFacadeDto.getEndDate())
                .endTime(updateEventFacadeDto.getEndTime())
                .build();

        event.updateEvent(
                updateEventFacadeDto.getName(),
                category,
                member,
                eventDate,
                updateEventFacadeDto.getRepetitionType(),
                updateEventFacadeDto.getRepetitionEndDate(),
                updateEventFacadeDto.getTags(),
                updateEventFacadeDto.getPriority(),
                updateEventFacadeDto.getMemo()
        );
    }


    public List<Event> getAllEvents(Member member, List<Long> categoryIds, EventFilterCriteria criteria) {
        return eventQueryRepository.findEvents(
                member.getId(), criteria.getStartDate(), criteria.getEndDate(), categoryIds, criteria.getPriority(), criteria.getTags()
        );
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
