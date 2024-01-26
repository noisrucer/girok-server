package com.girok.girokserver.core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private final int statusCode;
    private final String errorCode;
    private final String message;
}
