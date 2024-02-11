package com.girok.girokserver.domain.event.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetAllTagsResponse {
    private List<String> tags;
}
