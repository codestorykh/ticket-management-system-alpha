package com.codestorykh.common.exception;

import com.codestorykh.common.dto.EmptyObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalException {

    // This class can be used to handle global exceptions for the application.
    // You can define methods here to handle specific exceptions and return custom responses.
    // For example, you can use @ExceptionHandler annotations to catch specific exceptions
    // and return a ResponseEntity with a custom error message or status code.

    // Example:
    // @ExceptionHandler(ResourceNotFoundException.class)
    // public ResponseEntity<ResponseErrorTemplate> handleResourceNotFoundException(ResourceNotFoundException ex) {
    //     return new ResponseEntity<>(GeneralErrorResponse.generalError(), HttpStatus.NOT_FOUND);
    // }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseErrorTemplate> handle(Exception e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(
                GeneralErrorResponse.generalError(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CustomMessageException.class)
    public ResponseEntity<ResponseErrorTemplate> handle(CustomMessageException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(
                new ResponseErrorTemplate(
                        e.getMessage(),
                        e.getCode(),
                        e.getObject(),
                        true),
                e.getHttpStatus());
    }
}
