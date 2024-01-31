package com.girok.girokserver.core.exception;

import lombok.Getter;

@Getter
public enum ErrorInfo {
    /** Authentication **/
    MEMBER_ALREADY_EXIST(400, "MEMBER_ALREADY_EXIST", "Member with the given email already exists."),
    MEMBER_NOT_FOUND(400, "MEMBER_NOT_FOUND", "Member with the given email does not exist."),
    EMAIL_VERIFICATION_NOT_FOUND(400, "EMAIL_VERIFICATION_NOT_FOUND", "Email verification with the given email is not found."),
    EMAIL_ALREADY_VERIFIED(400, "EMAIL_ALREADY_VERIFIED", "Email address is already verified."),
    EMAIL_NOT_VERIFIED(400, "EMAIL_NOT_VERIFIED", "Email is not verified."),
    INVALID_VERIFICATION_CODE(400, "INVALID_VERIFICATION_CODE", "Invalid verification code."),
    VERIFICATION_CODE_EXPIRED(400, "VERIFICATION_CODE_EXPIRED", "Verification code is expired"),
    INVALID_PASSWORD(400, "INVALID_PASSWORD", "Password is invalid."),

    /** Category **/
    PARENT_CATEGORY_NOT_FOUND(400, "PARENT_CATEGORY_NOT_FOUND", "Parent Category does not exist."),
    DUPLICATE_CATEGORY(400, "DUPLICATE_CATEGORY", "Parent category cannot have multiple child categories with the same name"),
    CATEGORY_NOT_FOUND(400, "CATEGORY_NOT_FOUND", "Category with the given id is not found"),
    EMPTY_CATEGORY_NAME(400, "EMPTY_CATEGORY_NAME", "Category name cannot be empty."),
    NON_TOP_LEVEL_CATEGORY_COLOR_UPDATE_ATTEMPT_EXCEPTION(400, "NON_TOP_LEVEL_CATEGORY_COLOR_UPDATE_ATTEMPT_EXCEPTION", "You can only update the top level category colors"),

    /** Event **/
    EVENT_NOT_FOUND(400, "EVENT_NOT_FOUND", "Event not found."),

    /** JWT Exceptions **/
    INVALID_JWT_TOKEN(401, "INVALID_JWT_TOKEN", "JWT token is invalid."),
    EXPIRED_JWT_TOKEN(401, "EXPIRED_JWT_TOKEN", "JWT token is expired."),

    /** Global Exceptions **/
    UNAUTHORIZED_OPERATION_EXCEPTION(403, "UNAUTHORIZED_OPERATION_EXCEPTION", "You do not have permission to perform the operation");


    private final int statusCode;
    private final String errorCode;
    private final String message;

    ErrorInfo(int statusCode, String errorCode, String message) {
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.message = message;
    }
}
