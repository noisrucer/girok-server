package com.girok.girokserver.domain.event.controller;

import com.girok.girokserver.core.security.jwt.JwtTokenProvider;
import com.girok.girokserver.core.security.jwt.dto.JwtUserInfo;
import com.girok.girokserver.domain.event.controller.request.CreateEventRequest;
import com.girok.girokserver.domain.event.controller.request.EventFilterCriteria;
import com.girok.girokserver.domain.event.controller.request.UpdateEventRequest;
import com.girok.girokserver.domain.event.controller.response.CreateEventResponse;
import com.girok.girokserver.domain.event.controller.response.GetAllEventsResponse;
import com.girok.girokserver.domain.event.controller.response.GetSingleEventResponse;
import com.girok.girokserver.domain.event.facade.EventFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "3. Event")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class EventController {

    private final EventFacade eventFacade;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create an event")
    public ResponseEntity<CreateEventResponse> createEvent(@Valid @RequestBody CreateEventRequest request) {
        JwtUserInfo jwtUserInfo = jwtTokenProvider.getCurrentUserInfo();
        Long memberId = jwtUserInfo.getMemberId();

        CreateEventResponse responseBody = eventFacade.createEvent(memberId, EventMapper.toCreateEventFacadeDto(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @PutMapping("/events/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Update an event")
    public void updateEvent(@PathVariable(name = "id") Long eventId, @RequestBody UpdateEventRequest request) {
        JwtUserInfo jwtUserInfo = jwtTokenProvider.getCurrentUserInfo();
        Long memberId = jwtUserInfo.getMemberId();

        eventFacade.updateEvent(memberId, eventId, EventMapper.toCreateEventFacadeDto(request));
    }

    @GetMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all events")
    public ResponseEntity<GetAllEventsResponse> getAllEvents(
            @Valid EventFilterCriteria eventFilterCriteria
    ) {
        JwtUserInfo jwtUserInfo = jwtTokenProvider.getCurrentUserInfo();
        Long memberId = jwtUserInfo.getMemberId();

        GetAllEventsResponse responseBody = eventFacade.getAllEvents(memberId, eventFilterCriteria);
        return ResponseEntity.ok().body(responseBody);
    }

    @GetMapping("/events/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get a single event")
    public ResponseEntity<GetSingleEventResponse> getSingleEvent(@PathVariable(name = "id") Long eventId) {
        JwtUserInfo jwtUserInfo = jwtTokenProvider.getCurrentUserInfo();
        Long memberId = jwtUserInfo.getMemberId();

        GetSingleEventResponse responseBody = eventFacade.getSingleEvent(memberId, eventId);
        return ResponseEntity.ok().body(responseBody);
    }

    @DeleteMapping("/events/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an event")
    public void deleteEvent(@PathVariable(name = "id") Long eventId) {
        JwtUserInfo jwtUserInfo = jwtTokenProvider.getCurrentUserInfo();
        Long memberId = jwtUserInfo.getMemberId();
        eventFacade.deleteEvent(memberId, eventId);
    }
}
