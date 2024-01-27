package com.girok.girokserver.core.exception;

import lombok.Getter;

public class CustomException extends RuntimeException {

    @Getter
    private ErrorInfo errorInfo;

    private final String detailedMessage;

    public CustomException(ErrorInfo errorInfo) {
        super(errorInfo.getMessage());
        this.errorInfo = errorInfo;
        this.detailedMessage = null;
    }

    public CustomException(ErrorInfo errorInfo, String detailedMessage) {
        super(detailedMessage);
        this.errorInfo = errorInfo;
        this.detailedMessage = detailedMessage;
    }

    @Override
    public String getMessage() {
        return detailedMessage != null ? detailedMessage : super.getMessage();
    }
}
