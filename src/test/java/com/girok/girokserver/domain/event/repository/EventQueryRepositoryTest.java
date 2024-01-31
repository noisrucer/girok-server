package com.girok.girokserver.domain.event.repository;

import com.girok.girokserver.GirokserverApplication;
import com.girok.girokserver.domain.event.controller.request.EventFilterCriteria;
import com.girok.girokserver.domain.event.entity.Event;
import com.girok.girokserver.domain.event.entity.EventDate;
import com.girok.girokserver.domain.member.entity.Member;
import com.girok.girokserver.domain.member.repository.MemberRepository;
import net.bytebuddy.asm.Advice;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
//@ContextConfiguration(classes = GirokserverApplication.class)
@ActiveProfiles("default")
class EventQueryRepositoryTest {

    @Autowired EventRepository eventRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired EventQueryRepository eventQueryRepository;

    @Test
    public void single_day_event() throws Exception {
        Member member = Member.builder()
                .email("test1@gmail.com")
                .password("123123")
                .build();

        memberRepository.save(member);

        // Given
        Event event1 = createEvent(member, "event1", "2024-01-29", null);
        Event event2 = createEvent(member, "event1", "2024-01-30", null);
        Event event3 = createEvent(member, "event1", "2024-02-02", null);
        Event event4 = createEvent(member, "event1", "2023-12-31", null);

        eventRepository.save(event1);
        eventRepository.save(event2);
        eventRepository.save(event3);

        EventFilterCriteria criteria = EventFilterCriteria.builder()
                .startDate(LocalDate.parse("2024-01-01"))
                .endDate(LocalDate.parse("2024-01-31"))
                .build();

        // When
        List<Event> events = eventQueryRepository.findEvents(member.getId(), criteria);

        // Then
        Assertions.assertThat(events.size()).isEqualTo(2);
    }

    public Event createEvent(Member member, String name, String startDateString, String endDateString) {
        EventDate.EventDateBuilder eventDateBuilder = EventDate.builder();

        if (startDateString != null) {
            eventDateBuilder.startDate(LocalDate.parse(startDateString));
        }
        if (endDateString != null) {
            eventDateBuilder.endDate(LocalDate.parse(endDateString));
        }

        EventDate eventDate = eventDateBuilder.build();

        return Event.builder()
                .name(name)
                .member(member)
                .eventDate(eventDate)
                .build();
    }

}