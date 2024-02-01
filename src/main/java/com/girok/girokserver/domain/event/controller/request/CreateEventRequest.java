package com.girok.girokserver.domain.event.controller.request;

import com.girok.girokserver.domain.event.controller.validation.ValidCreateEventRequest;
import com.girok.girokserver.global.enums.EventPriority;
import com.girok.girokserver.global.enums.EventRepetitionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.xml.transform.OutputKeys;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@ValidCreateEventRequest
@Getter
public class CreateEventRequest {

    @Schema(example = "1")
    private Long categoryId;

    @NotBlank
    @Schema(example = "Meeting with Alexander")
    private String name;

    @Valid
    @NotNull
    private EventDate eventDate;

    @Valid
    private Repetition repetition;

    @Schema(example = "[\"meeting\", \"friend\"]")
    private List<String> tags;

    @Schema(example = "HIGH")
    private EventPriority priority;

    @Schema(example = "Don't forget to bring the HDMI port.")
    private String memo;

    @Getter
    public static class EventDate {
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @NotNull
        @Schema(example = "2024-05-21", type = "string")
        private LocalDate startDate;

        @DateTimeFormat(pattern = "HH:mm:ss")
        @Schema(example = "13:20:00", type = "string")
        private LocalTime startTime;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @Schema(example = "2024-05-21", type = "string")
        private LocalDate endDate;

        @DateTimeFormat(pattern = "HH:mm:ss")
        @Schema(example = "17:20:00", type = "string")
        private LocalTime endTime;
    }

    @Getter
    public static class Repetition {
        private EventRepetitionType repetitionType;
        private LocalDate repetitionEndDate;
    }
}
