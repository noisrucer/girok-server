package com.girok.girokserver.domain.event.service;

import com.girok.girokserver.domain.event.controller.response.GetAllTagsResponse;
import com.girok.girokserver.domain.event.repository.EventRepository;
import com.girok.girokserver.domain.event.repository.EventTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventTagService {

    private final EventRepository eventRepository;
    private final EventTagRepository eventTagRepository;

    public GetAllTagsResponse getAllEventTags(Long memberId) {
        List<Long> eventIds = eventRepository.findEventIdsByMemberId(memberId);
        List<String> tagNames = eventTagRepository.findAllDistinctTagNamesByEventIds(eventIds);
        List<String> tagDtos = new ArrayList<>(tagNames);
        return new GetAllTagsResponse(tagDtos);
    }
}
