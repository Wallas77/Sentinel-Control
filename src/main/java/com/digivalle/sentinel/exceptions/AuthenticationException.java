package com.digivalle.sentinel.exceptions;

public class AuthenticationException extends Throwable {
    public AuthenticationException(String message) {
        super(message);
    }
    
    public AuthenticationException(Class entityClass, String identity) {
        super(String.format("El objeto de tipo [%s] con identificador [%s] no existe", entityClass.getCanonicalName(), identity));
    }
    
}
