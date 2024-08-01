/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * @author Waldir.Valle
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends Throwable {
    public EntityNotFoundException(String entityType, String identity) {
        super(String.format("El objeto de tipo [%s] con identificador [%s] no existe", entityType, identity));
    }
    public EntityNotFoundException(Class entityClass, String identity) {
        super(String.format("El objeto de tipo [%s] con identificador [%s] no existe", entityClass.getCanonicalName(), identity));
    }
}
