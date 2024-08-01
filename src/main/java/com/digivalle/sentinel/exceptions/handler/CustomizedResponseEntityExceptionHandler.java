/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.exceptions.handler;


import com.digivalle.sentinel.exceptions.BadRequestException;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.exceptions.NoAccessGrantedException;
import com.digivalle.sentinel.exceptions.handler.model.ErrorDetails;
import java.util.Date;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 *
 * @author Waldir.Valle
 */
@ControllerAdvice
@RestController
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  
  
  @ExceptionHandler(ExistentEntityException.class)
  public final ResponseEntity<ErrorDetails> handleExistentEntityException(ExistentEntityException ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(),
        "ExistentEntityException");
    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
  }
  
  @ExceptionHandler(BusinessLogicException.class)
  public final ResponseEntity<ErrorDetails> handleBusinessLogicException(BusinessLogicException ex, WebRequest request) {
      //System.out.println("CustomizedResponseEntityExceptionHandler ex.getMessage()=>"+ex.getMessage());
    ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(),
        "BusinessLogicException");
    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
  }
  
  @ExceptionHandler(EntityNotExistentException.class)
  public final ResponseEntity<ErrorDetails> handleEntityNotExistentException(EntityNotExistentException ex, WebRequest request) {
      //System.out.println("CustomizedResponseEntityExceptionHandler ex.getMessage()=>"+ex.getMessage());
    ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(),
        "EntityNotExistentException");
    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
  }
  
  @ExceptionHandler(NoAccessGrantedException.class)
  public final ResponseEntity<ErrorDetails> handleNoAccessGrantedException(NoAccessGrantedException ex, WebRequest request) {
      //System.out.println("CustomizedResponseEntityExceptionHandler ex.getMessage()=>"+ex.getMessage());
    ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(),
        "NoAccessGrantedException");
    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
  }
  
  @ExceptionHandler(PSQLException.class)
  public final ResponseEntity<ErrorDetails> handlePSQLException(PSQLException ex, WebRequest request) {
      //System.out.println("CustomizedResponseEntityExceptionHandler ex.getMessage()=>"+ex.getMessage());
    ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(),
        "PSQLException");
    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
  }
  
  @ExceptionHandler(BadRequestException.class)
  public final ResponseEntity<ErrorDetails> handleBadRequestException(BadRequestException ex, WebRequest request) {
      System.out.println("CustomizedResponseEntityExceptionHandler ex.getMessage()=>"+ex.getMessage());
      System.out.println("request=>"+request);
    ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(),
        "BadRequestException");
    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
  }
  
  @Override
     protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    
         System.out.println("CustomizedResponseEntityExceptionHandler ex.getMessage()=>"+ex.getMessage());
    ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(),
        "BadRequestException");
    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
  }
}
