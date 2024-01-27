package com.girok.girokserver.domain.category.exception;

import com.girok.girokserver.core.exception.CustomInternalException;

public class RootCategoryParentAccessException extends CustomInternalException {

    public RootCategoryParentAccessException() {
        super("Root category does not have a parent category.");
    }

    public RootCategoryParentAccessException(String message) {
        super(message);
    }
}
