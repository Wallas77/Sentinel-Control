package com.digivalle.sentinel.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends Exception {
    public ResourceNotFoundException(String entityType, Object identifier) {
        super(String.format("Resource '%s' of type '%s' wasn't found", identifier, entityType));
    }
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
