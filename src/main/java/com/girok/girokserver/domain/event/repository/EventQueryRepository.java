package com.girok.girokserver.domain.event.repository;

import com.girok.girokserver.domain.event.entity.Event;
import com.girok.girokserver.domain.event.entity.QEvent;
import com.girok.girokserver.domain.event.entity.QEventTag;
import com.girok.girokserver.global.enums.EventPriority;
import com.girok.girokserver.global.enums.EventRepetitionType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class EventQueryRepository {

    private final JPAQueryFactory queryFactory;
    private static final QEvent event = QEvent.event;
    private static final QEventTag eventTag = QEventTag.eventTag;


    public List<Event> findEvents(Long memberId, LocalDate windowStartDate, LocalDate windowEndDate, List<Long> categoryIds, EventPriority priority, List<String> tagNames) {

        List<Event> nonRepeatingEvents = queryFactory
                .select(event)
                .from(event)
                .where(
                        event.member.id.eq(memberId),
                        metadataCombinedCondition(categoryIds, priority, tagNames),
                        event.eventDate.startDate.between(windowStartDate, windowEndDate)
                                .or(
                                        event.eventDate.startDate.before(windowStartDate)
                                                .and(event.eventDate.endDate.goe(windowStartDate))
                                                .and(event.repetitionType.isNull()) // actually unnecessary check, but leave for clarity.
                                )
                )
                .fetch();

        List<Event> candidateRepeatingEvents = queryFactory
                .select(event)
                .from(event)
                .where(
                        event.member.id.eq(memberId),
                        metadataCombinedCondition(categoryIds, priority, tagNames),
                        event.eventDate.startDate.before(windowStartDate),
                        event.repetitionType.isNotNull(),
                        event.repetitionEndDate.goe(windowStartDate)
                                .or(event.repetitionEndDate.isNull())
                )
                .fetch();

        List<Event> repeatingEvents = new ArrayList<>();
        for (Event event : candidateRepeatingEvents) {
            EventRepetitionType repetitionType = event.getRepetitionType();
            LocalDate repetitionEndDate = event.getRepetitionEndDate();

            // If yes repetitionEndDate, calculate whether it falls in
            switch (repetitionType) {
                case DAILY:
                    repeatingEvents.add(event);
                    break;
                case WEEKLY:
                    if (judgeFallInWeekly(event, windowStartDate, windowEndDate)) {
                        repeatingEvents.add(event);
                        break;
                    }
                case MONTHLY:
                    if (judgeFallInMonthly(event, windowStartDate, windowEndDate)) {
                        repeatingEvents.add(event);
                        break;
                    }
                case YEARLY:
                    if (judgeFallInYearly(event, windowStartDate, windowEndDate)) {
                        repeatingEvents.add(event);
                        break;
                    }
                default:
                    break;
            }
        }

        nonRepeatingEvents.addAll(repeatingEvents);
        return nonRepeatingEvents;
    }

    private BooleanExpression categoryIdEq(List<Long> categoryIds) {
        return categoryIds != null ? event.category.id.in(categoryIds) : null;
    }

    private BooleanExpression priorityEq(EventPriority priority) {
        return priority != null ? event.priority.eq(priority) : null;
    }

    private BooleanExpression tagsIn(List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return null;
        }

        BooleanExpression combinedCondition = null;

        for (String tagName : tagNames) {
            BooleanExpression condition = JPAExpressions
                    .selectOne()
                    .from(eventTag)
                    .where(eventTag.name.eq(tagName), eventTag.event.eq(event))
                    .exists();

            // Must contain ALL tags
            combinedCondition = (combinedCondition == null) ? condition : combinedCondition.and(condition);
        }

        return combinedCondition;
    }

    private BooleanExpression metadataCombinedCondition(List<Long> categoryIds, EventPriority priority, List<String> tagNames) {
        // Create individual conditions
        BooleanExpression categoryCondition = categoryIdEq(categoryIds);
        BooleanExpression priorityCondition = priorityEq(priority);
        BooleanExpression tagsCondition = tagsIn(tagNames);

        // Combine conditions
        BooleanExpression combinedCondition = null;

        if (categoryCondition != null) {
            combinedCondition = (combinedCondition == null) ? categoryCondition : combinedCondition.and(categoryCondition);
        }
        if (priorityCondition != null) {
            combinedCondition = (combinedCondition == null) ? priorityCondition : combinedCondition.and(priorityCondition);
        }
        if (tagsCondition != null) {
            combinedCondition = (combinedCondition == null) ? tagsCondition : combinedCondition.and(tagsCondition);
        }

        return combinedCondition;
    }

    private boolean judgeFallInWeekly(Event event, LocalDate windowStartDate, LocalDate windowEndDate) {
        // Weekly repeat
        LocalDate startDate = event.getEventDate().getStartDate();
        LocalDate repetitionEndDate = event.getRepetitionEndDate();

        long weeksBetween = ChronoUnit.WEEKS.between(startDate, windowStartDate);
        LocalDate lastRepetitionDate = startDate.plusWeeks(weeksBetween);

        if (lastRepetitionDate.isEqual(windowStartDate)) return true;
        LocalDate nextLastRepetitionDate = lastRepetitionDate.plusWeeks(1);
        return !nextLastRepetitionDate.isAfter(windowEndDate) && (repetitionEndDate == null || !nextLastRepetitionDate.isAfter(repetitionEndDate));
    }

    private boolean judgeFallInMonthly(Event event, LocalDate windowStartDate, LocalDate windowEndDate) {
        // Monthly repeat
        LocalDate startDate = event.getEventDate().getStartDate();
        LocalDate repetitionEndDate = event.getRepetitionEndDate();

        long monthsBetween = ChronoUnit.MONTHS.between(startDate, windowStartDate);
        LocalDate lastRepetitionDate = startDate.plusMonths(monthsBetween);

        if (lastRepetitionDate.isEqual(windowStartDate)) return true;
        LocalDate nextLastRepetitionDate = lastRepetitionDate.plusMonths(1);
        return !nextLastRepetitionDate.isAfter(windowEndDate) && (repetitionEndDate == null || !nextLastRepetitionDate.isAfter(repetitionEndDate));
    }

    private boolean judgeFallInYearly(Event event, LocalDate windowStartDate, LocalDate windowEndDate) {
        // Yearly Repeat
        LocalDate startDate = event.getEventDate().getStartDate();
        LocalDate repetitionEndDate = event.getRepetitionEndDate();

        long yearsBetween = ChronoUnit.YEARS.between(startDate, windowStartDate);
        LocalDate lastRepetitionDate = startDate.plusYears(yearsBetween);

        if (lastRepetitionDate.isEqual(windowStartDate)) return true;
        LocalDate nextLastRepetitionDate = lastRepetitionDate.plusYears(1);
        return !nextLastRepetitionDate.isAfter(windowEndDate) && (repetitionEndDate == null || !nextLastRepetitionDate.isAfter(repetitionEndDate));
    }
}
