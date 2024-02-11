package com.girok.girokserver.domain.event.controller;

import com.girok.girokserver.core.security.jwt.JwtTokenProvider;
import com.girok.girokserver.core.security.jwt.dto.JwtUserInfo;
import com.girok.girokserver.domain.event.controller.response.GetAllTagsResponse;
import com.girok.girokserver.domain.event.service.EventTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "4. Event Tag")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class TagController {

    private final EventTagService eventTagService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/tags")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all tags")
    public ResponseEntity<GetAllTagsResponse> getAllTags() {
        JwtUserInfo jwtUserInfo = jwtTokenProvider.getCurrentUserInfo();
        Long memberId = jwtUserInfo.getMemberId();

        GetAllTagsResponse responseBody = eventTagService.getAllEventTags(memberId);
        return ResponseEntity.ok().body(responseBody);
    }

}
