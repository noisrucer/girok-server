package com.girok.girokserver.domain.event.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class EventTag {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    public EventTag(String name, Event event) {
        this.name = name;
        this.event = event;
    }

    public void assignEvent(Event event) {
        this.event = event;
    }
}
