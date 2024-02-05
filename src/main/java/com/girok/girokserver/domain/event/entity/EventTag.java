package com.girok.girokserver.domain.event.entity;

import com.girok.girokserver.global.baseentity.AuditBase;
import jakarta.persistence.*;
import lombok.*;

@Entity
//@Table(name = "event_tag", uniqueConstraints = {
//        @UniqueConstraint(columnNames = {"event_id", "name"})
//})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class EventTag extends AuditBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    public void assignEvent(Event event) {
        this.event = event;
    }

}
