package com.girok.girokserver.domain.event.controller.response;

import com.girok.girokserver.global.enums.CategoryColor;
import com.girok.girokserver.global.enums.EventPriority;
import com.girok.girokserver.global.enums.EventRepetitionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class GetSingleEventResponse {

    private String name;
    private CategoryColor color;
    private List<String> tags;
    private EventPriority priority;
    private String memo;
    private EventDate eventDate;
    private Repetition repetition;
    private List<Category> categoryPath;
    private boolean singleDayEvent;

    @Getter
    @Builder
    public static class EventDate {
        private LocalDate startDate;
        private LocalTime startTime;
        private LocalDate endDate;
        private LocalTime endTime;
    }

    @Getter
    @Builder
    public static class Repetition {
        private EventRepetitionType repetitionType;
        private LocalDate repetitionEndDate;
    }

    @Getter
    @AllArgsConstructor
    public static class Category {
        private Long categoryId;
        private String categoryName;
    }

}

