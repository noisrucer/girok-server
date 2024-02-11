package com.girok.girokserver.domain.event.repository;

import com.girok.girokserver.domain.event.controller.request.EventFilterCriteria;
import com.girok.girokserver.domain.event.entity.Event;
import com.girok.girokserver.domain.event.entity.EventDate;
import com.girok.girokserver.domain.member.entity.Member;
import com.girok.girokserver.domain.member.repository.MemberRepository;
import com.girok.girokserver.global.enums.EventRepetitionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static com.girok.girokserver.global.enums.EventRepetitionType.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
//@ContextConfiguration(classes = GirokserverApplication.class)
@ActiveProfiles("default")
class EventQueryRepositoryTest {

    @Autowired
    EventRepository eventRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EventQueryRepository eventQueryRepository;

    @Test
    public void single_day_only_event_no_repeat() throws Exception {
        Member member = Member.builder()
                .email("test1@gmail.com")
                .password("123123")
                .build();

        memberRepository.save(member);

        // Given
        Event event1 = createEvent(member, "event1", "2024-01-29", null, null, null);
        Event event2 = createEvent(member, "event1", "2024-01-30", null, null, null);
        Event event3 = createEvent(member, "event1", "2024-01-30", "2024-01-30", null, null);

        Event event4 = createEvent(member, "event1", "2024-02-02", null, null, null);
        Event event5 = createEvent(member, "event1", "2023-12-31", null, null, null);
        Event event6 = createEvent(member, "event1", "2023-12-31", "2023-12-31", null, null);

        eventRepository.save(event1);
        eventRepository.save(event2);
        eventRepository.save(event3);
        eventRepository.save(event4);
        eventRepository.save(event5);
        eventRepository.save(event6);

        EventFilterCriteria criteria = EventFilterCriteria.builder()
                .startDate(LocalDate.parse("2024-01-01"))
                .endDate(LocalDate.parse("2024-01-31"))
                .build();

        // When
        List<Event> events = eventQueryRepository.findEvents(member.getId(), LocalDate.parse("2024-01-01"), LocalDate.parse("2024-01-31"), null, null, null);

        // Then
        assertThat(events).containsExactlyInAnyOrder(event1, event2, event3);
    }

    @Test
    public void single_multi_day_no_repeat() throws Exception {
        Member member = Member.builder()
                .email("test1@gmail.com")
                .password("123123")
                .build();

        memberRepository.save(member);

        // Given
        Event event1 = createEvent(member, "event", "2024-01-01", null, null, null);
        Event event2 = createEvent(member, "event", "2024-01-02", null, null, null);
        Event event3 = createEvent(member, "event", "2024-01-25", "2024-01-25", null, null);
        Event event4 = createEvent(member, "event", "2024-01-01", "2024-01-25", null, null);
        Event event5 = createEvent(member, "event", "2023-12-30", "2024-01-05", null, null);
        Event event6 = createEvent(member, "event", "2024-01-25", "2024-02-05", null, null);
        Event event7 = createEvent(member, "event", "2023-12-25", "2024-02-05", null, null);

        Event event8 = createEvent(member, "event", "2023-12-31", null, null, null);
        Event event9 = createEvent(member, "event", "2024-02-02", null, null, null);
        Event event10 = createEvent(member, "event", "2023-12-31", "2023-12-31", null, null);
        Event event11 = createEvent(member, "event", "2023-12-30", "2023-12-31", null, null);
        Event event12 = createEvent(member, "event", "2024-02-01", "2024-02-01", null, null);
        Event event13 = createEvent(member, "event", "2024-02-01", "2024-02-10", null, null);

        saveEvents(event1, event2, event3, event4, event5, event6, event7, event8, event9, event10, event11, event12, event13);

        EventFilterCriteria criteria = EventFilterCriteria.builder()
                .startDate(LocalDate.parse("2024-01-01"))
                .endDate(LocalDate.parse("2024-01-31"))
                .build();

        // When
        List<Event> events = eventQueryRepository.findEvents(member.getId(), LocalDate.parse("2024-01-01"), LocalDate.parse("2024-01-31"), null, null, null);

        // Then
        assertThat(events).containsExactlyInAnyOrder(event1, event2, event3, event4, event5, event6, event7);
    }

    @Test
    public void single_multi_day_with_repeat() throws Exception {
        Member member = Member.builder()
                .email("test1@gmail.com")
                .password("123123")
                .build();

        memberRepository.save(member);

        // Given

        // Fall-in events
        Event event1 = createEvent(member, "event", "2024-01-01", null, null, null);
        Event event3 = createEvent(member, "event", "2024-01-25", "2024-01-25", null, null);
        Event event4 = createEvent(member, "event", "2024-01-01", "2024-01-25", null, null);
        Event event5 = createEvent(member, "event", "2023-12-30", "2024-01-05", null, null);
        Event event6 = createEvent(member, "event", "2024-01-25", "2024-02-05", null, null);
        Event event7 = createEvent(member, "event", "2023-12-25", "2024-02-05", null, null);

        Event event8 = createEvent(member, "event", "2023-12-25", "2024-12-25", DAILY, null);
        Event event9 = createEvent(member, "event", "2023-12-25", null, DAILY, null);
        Event event10 = createEvent(member, "event", "2023-12-25", "2024-12-25", DAILY, "2024-01-01");
        Event event11 = createEvent(member, "event", "2023-12-25", "2024-12-25", DAILY, "2024-01-10");
        Event event12 = createEvent(member, "event", "2023-12-25", null, DAILY, "2024-01-10");

        Event event13 = createEvent(member, "event", "2023-12-25", null, WEEKLY, null);
        Event event14 = createEvent(member, "event", "2023-12-25", null, WEEKLY, "2024-01-01");
        Event event15 = createEvent(member, "event", "2023-12-25", null, WEEKLY, "2024-01-03");

        Event event16 = createEvent(member, "event", "2023-12-25", null, MONTHLY, null);
        Event event17 = createEvent(member, "event", "2023-12-25", null, MONTHLY, "2024-01-25");
        Event event18 = createEvent(member, "event", "2023-12-25", null, MONTHLY, "2024-01-26");

        Event event19 = createEvent(member, "event", "2023-01-01", null, YEARLY, null);
        Event event20 = createEvent(member, "event", "2023-01-01", null, YEARLY, "2024-01-01");
        Event event21 = createEvent(member, "event", "2023-01-01", null, YEARLY, "2024-01-03");

        Event event22 = createEvent(member, "event", "2024-01-01", null, DAILY, null);
        Event event23 = createEvent(member, "event", "2024-01-01", null, DAILY, "2024-01-01");
        Event event24 = createEvent(member, "event", "2024-01-01", null, DAILY, "2024-01-05");

        Event event25 = createEvent(member, "event", "2024-01-01", null, WEEKLY, null);
        Event event26 = createEvent(member, "event", "2024-01-01", null, WEEKLY, "2024-01-01");
        Event event27 = createEvent(member, "event", "2024-01-01", null, WEEKLY, "2024-01-02");
        Event event28 = createEvent(member, "event", "2024-01-01", null, WEEKLY, "2024-01-08");

        Event event29 = createEvent(member, "event", "2024-01-01", null, MONTHLY, null);
        Event event30 = createEvent(member, "event", "2024-01-01", null, MONTHLY, "2024-01-01");


        // Not fall-in events
        Event event31 = createEvent(member, "event", "2023-12-31", null, null, null);
        Event event32 = createEvent(member, "event", "2024-02-02", null, null, null);
        Event event33 = createEvent(member, "event", "2023-12-31", "2023-12-31", null, null);
        Event event34 = createEvent(member, "event", "2023-12-30", "2023-12-31", null, null);
        Event event35 = createEvent(member, "event", "2024-02-01", "2024-02-01", null, null);


        Event event36 = createEvent(member, "event", "2024-02-01", "2024-02-10", null, null);
//
        Event event37 = createEvent(member, "event", "2023-12-30", null, DAILY, "2023-12-31");

        Event event38 = createEvent(member, "event", "2023-12-24", null, WEEKLY, "2024-01-01"); // 01-01 월

        Event event39 = createEvent(member, "event", "2023-11-30", null, MONTHLY, "2023-12-31");
        Event event40 = createEvent(member, "event", "2023-11-30", null, MONTHLY, "2023-12-30");

        Event event41 = createEvent(member, "event", "2023-11-30", null, YEARLY, null);
        Event event42 = createEvent(member, "event", "2023-11-30", null, YEARLY, "2024-11-30");


        saveEvents(
                event1, event3, event4, event5, event6, event7, event8, event9, event10, event11, event12, event13, event14, event15,
                event16, event17, event18, event19, event20, event21, event22, event23, event24, event25, event26, event27, event28, event29, event30,
                event31, event32, event33, event34, event35, event36, event37, event38, event39, event40, event41, event42
        );

        EventFilterCriteria criteria = EventFilterCriteria.builder()
                .startDate(LocalDate.parse("2024-01-01"))
                .endDate(LocalDate.parse("2024-01-31"))
                .build();

        // When
        List<Event> events = eventQueryRepository.findEvents(
                member.getId(), LocalDate.parse("2024-01-01"), LocalDate.parse("2024-01-31"), null, null, null
        );

        // Then
        assertThat(events).containsExactlyInAnyOrder(event1, event3, event4, event5, event6, event7, event8, event9, event10, event11, event12, event13, event14, event15,
                event16, event17, event18, event19, event20, event21, event22, event23, event24, event25, event26, event27, event28, event29, event30);
    }


    public void saveEvents(Event... events) {
        eventRepository.saveAll(Arrays.asList(events));
    }

    public Event createEvent(Member member, String name, String startDateString, String endDateString, EventRepetitionType repetitionType, String repetitionEndDateString) {
        EventDate.EventDateBuilder eventDateBuilder = EventDate.builder();

        if (startDateString != null) {
            eventDateBuilder.startDate(LocalDate.parse(startDateString));
        }
        if (endDateString != null) {
            eventDateBuilder.endDate(LocalDate.parse(endDateString));
        }

        EventDate eventDate = eventDateBuilder.build();

        Event.EventBuilder eventBuilder = Event.builder();

        eventBuilder
                .name(name)
                .member(member)
                .eventDate(eventDate);

        if (repetitionType != null) {
            eventBuilder.repetitionType(repetitionType);
        }

        if (repetitionEndDateString != null) {
            eventBuilder.repetitionEndDate(LocalDate.parse(repetitionEndDateString));
        }

        return eventBuilder.build();
    }

}