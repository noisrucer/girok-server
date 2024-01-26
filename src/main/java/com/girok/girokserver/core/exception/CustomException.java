package com.girok.girokserver.core.exception;

import lombok.Getter;

public class CustomException extends RuntimeException {

    @Getter
    private ErrorInfo errorInfo;

    public CustomException(ErrorInfo errorInfo) {
        super(errorInfo.getMessage());
        this.errorInfo = errorInfo;
    }
}
