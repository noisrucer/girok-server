package com.girok.girokserver.domain.event.entity;

import com.girok.girokserver.domain.category.entity.Category;
import com.girok.girokserver.domain.member.entity.Member;
import com.girok.girokserver.global.baseentity.AuditBase;
import com.girok.girokserver.global.enums.CategoryColor;
import com.girok.girokserver.global.enums.EventPriority;
import com.girok.girokserver.global.enums.EventRepetitionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class Event extends AuditBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    private String color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Embedded
    private EventDate eventDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "repetition_type")
    private EventRepetitionType repetitionType;

    @Column(name = "repetition_end_date")
    private LocalDate repetitionEndDate;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<EventTag> tags = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private EventPriority priority;

    @Column(name = "memo", length = 1000)
    private String memo;

    // Constructor
    public static Event createEvent(
            String name,
            Category category,
            Member member,
            EventDate eventDate,
            EventRepetitionType repetitionType,
            LocalDate repetitionEndDate,
            List<String> tags,
            EventPriority priority,
            String memo
    ) {
        Event event = Event.builder()
                .name(name)
                .category(category)
                .member(member)
                .eventDate(eventDate)
                .repetitionType(repetitionType)
                .repetitionEndDate(repetitionEndDate)
                .priority(priority)
                .memo(memo)
                .build();

        // Member
        member.addEvent(event);

        // Category
        if (category != null) {
            category.addEvent(event);
        }

        // Tags
        if (tags != null && !tags.isEmpty()) {
            for (String tagName : tags) {
                event.addTag(EventTag.builder()
                        .name(tagName)
                        .event(event)
                        .build()
                );
            }
        }

        return event;
    }

    // Relationship Methods
    public void addTag(EventTag tag) {
        this.tags.add(tag);
        tag.assignEvent(this);
    }

    public void assignMember(Member member) {
        this.member = member;
    }

    public void assignCategory(Category category) {
        this.category = category;
    }

    // Business Logic
    public boolean isSingleDayEvent() {
        return eventDate.isSingleDayEvent() && (repetitionType == null);
    }

    public CategoryColor getColor() {
        return category != null ? category.getColor() : CategoryColor.getDefaultColor();
    }

}
