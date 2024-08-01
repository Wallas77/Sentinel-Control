package com.digivalle.sentinel.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.ALREADY_REPORTED)
public class ResourceExistentException extends Exception {
    public ResourceExistentException(String entityType, Object identifier) {
        super(String.format("Resource '%s' of type '%s' already found", identifier, entityType));
    }
    public ResourceExistentException(String message) {
        super(message);
    }
}
