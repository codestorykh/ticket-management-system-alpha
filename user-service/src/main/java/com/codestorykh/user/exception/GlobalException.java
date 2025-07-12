package com.codestorykh.user.exception;

import com.codestorykh.user.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(UserValidationException.class)
    public ResponseEntity<ErrorResponse> handleUserValidationException(UserValidationException e) {

        ErrorResponse errorResponse = new ErrorResponse("400", e.getMessage(), e.getField(), e.getValue());
        // ResponseEntity.badRequest().body(errorResponse);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RoleValidationException.class)
    public ResponseEntity<ErrorResponse> handleRoleValidationException(RoleValidationException e) {
        ErrorResponse errorResponse = new ErrorResponse("400", e.getMessage(), e.getField(), e.getValue());
        // ResponseEntity.badRequest().body(errorResponse);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}
