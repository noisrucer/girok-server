package com.girok.girokserver.domain.event.controller.request;

import com.girok.girokserver.global.enums.EventPriority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class EventFilterCriteria {
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(example = "2024-05-01", type = "string")
    private LocalDate startDate;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(example = "2024-05-31", type = "string")
    private LocalDate endDate;

    private String category;

    private EventPriority priority;

    private List<String> tags;
}
