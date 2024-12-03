package com.digivalle.sentinel.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class NoAccessGrantedException extends RuntimeException {
    public NoAccessGrantedException(String message){
        super(message);
    }
    
    public NoAccessGrantedException(String module, String action){
        super(String.format("El Usuario no puede realizar la acción: [%s] dentro del módulo [%s]",action,module));
    }
}
