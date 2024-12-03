package com.digivalle.sentinel.exceptions;

public class ExistentEntityException extends RuntimeException {
    public ExistentEntityException(String entityType, String identity) {
        super(String.format("Un objeto de tipo [%s] con identificador [%s] ya existe", entityType, identity));
    }
    public ExistentEntityException(Class entityClass, String identity) {
        super(String.format("Un objeto de tipo [%s] con identificador [%s] ya existe", entityClass.getCanonicalName(), identity));
    }
}
