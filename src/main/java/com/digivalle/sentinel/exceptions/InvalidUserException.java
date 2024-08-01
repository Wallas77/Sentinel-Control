package com.digivalle.sentinel.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class InvalidUserException extends Exception {
    public InvalidUserException(String message){
        super(message);
    }
}
