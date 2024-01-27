package com.girok.girokserver.domain.category.exception;

public class RootCategoryNameAccessException extends RuntimeException {

    public RootCategoryNameAccessException() {
        super("Root category does not have a parent category.");
    }

    public RootCategoryNameAccessException(String message) {
        super(message);
    }
}
