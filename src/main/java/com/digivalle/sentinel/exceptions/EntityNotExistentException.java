package com.digivalle.sentinel.exceptions;

public class EntityNotExistentException extends RuntimeException {
    public EntityNotExistentException(String message) {
        super(message);
    }
    
    public EntityNotExistentException(Class entityClass, String identity) {
        super(String.format("El objeto de tipo [%s] con identificador [%s] no existe", entityClass.getCanonicalName(), identity));
    }
    
}
