package com.ebbe3000.spring_boot_library.advice;

import com.ebbe3000.spring_boot_library.dto.ErrorResponse;
import com.ebbe3000.spring_boot_library.exception.NoCredentials;
import com.ebbe3000.spring_boot_library.exception.OutstandingFees;
import com.ebbe3000.spring_boot_library.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse("NOT_FOUND", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(NoCredentials.class)
    public ResponseEntity<ErrorResponse> handleNoCredentials(NoCredentials ex) {
        ErrorResponse error = new ErrorResponse("NO_CREDENTIALS", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED).body(error);
    }

    @ExceptionHandler(OutstandingFees.class)
    public ResponseEntity<ErrorResponse> handleOutstandingFees(OutstandingFees ex) {
        ErrorResponse error = new ErrorResponse("OUTSTANDING_FEES", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(error);
    }
}
