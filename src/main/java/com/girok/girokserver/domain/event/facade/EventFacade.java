package com.girok.girokserver.domain.event.facade;

import com.girok.girokserver.domain.category.entity.Category;
import com.girok.girokserver.domain.category.service.CategoryService;
import com.girok.girokserver.domain.event.controller.request.EventFilterCriteria;
import com.girok.girokserver.domain.event.controller.response.CreateEventResponse;
import com.girok.girokserver.domain.event.controller.response.GetAllEventsResponse;
import com.girok.girokserver.domain.event.controller.response.GetSingleEventResponse;
import com.girok.girokserver.domain.event.entity.Event;
import com.girok.girokserver.domain.event.entity.EventDate;
import com.girok.girokserver.domain.event.entity.EventTag;
import com.girok.girokserver.domain.event.facade.dto.CreateEventFacadeDto;
import com.girok.girokserver.domain.event.service.EventService;
import com.girok.girokserver.domain.member.entity.Member;
import com.girok.girokserver.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.stream.EventFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventFacade {

    private final MemberService memberService;
    private final CategoryService categoryService;
    private final EventService eventService;

    @Transactional
    public CreateEventResponse createEvent(Long memberId, CreateEventFacadeDto createEventFacadeDto) {
        Member member = memberService.getMemberById(memberId);

        Long categoryId = createEventFacadeDto.getCategoryId();
        Category category = null;
        if (categoryId != null) {
            category = categoryService.getCategoryByMemberAndId(member, categoryId);
        }

        Long eventId = eventService.createEvent(
                member,
                category,
                createEventFacadeDto
        );

        return new CreateEventResponse(eventId);
    }

    public GetAllEventsResponse getAllEvents(Long memberId, EventFilterCriteria criteria) {
        Member member = memberService.getMemberById(memberId);
        List<Event> events = eventService.getAllEvents(member, criteria);
        return new GetAllEventsResponse();
    }

    public GetSingleEventResponse getSingleEvent(Long memberId, Long eventId) {
        Member member = memberService.getMemberById(memberId);
        Event event = eventService.getSingleEvent(member, eventId);

        EventDate eventDate = event.getEventDate();

        // Build category path
        Category category = event.getCategory();
        List<GetSingleEventResponse.Category> categoryPath = new ArrayList<>();

        while (category != null) {
            categoryPath.add(new GetSingleEventResponse.Category(category.getId(), category.getName()));
            category = category.getParent();
        }
        Collections.reverse(categoryPath);

        return GetSingleEventResponse.builder()
                .name(event.getName())
                .color(event.getColor())
                .tags(event.getTags().stream().map(EventTag::getName).toList())
                .priority(event.getPriority())
                .memo(event.getMemo())
                .eventDate(
                        GetSingleEventResponse.EventDate.builder()
                                .startDate(eventDate.getStartDate())
                                .startTime(eventDate.getStartTime())
                                .endDate(eventDate.getEndDate())
                                .endTime(eventDate.getEndTime())
                                .build()
                )
                .repetition(
                        GetSingleEventResponse.Repetition.builder()
                                .repetitionType(event.getRepetitionType())
                                .repetitionEndDate(event.getRepetitionEndDate())
                                .build()
                )
                .categoryPath(categoryPath)
                .singleDayEvent(event.isSingleDayEvent())
                .build();
    }

    @Transactional
    public void deleteEvent(Long memberId, Long eventId) {
        eventService.deleteEvent(memberId, eventId);
    }
}
