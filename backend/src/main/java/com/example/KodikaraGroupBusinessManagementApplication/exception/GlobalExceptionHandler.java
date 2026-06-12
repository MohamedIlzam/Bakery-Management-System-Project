package com.example.KodikaraGroupBusinessManagementApplication.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handles validation errors (@Valid DTOs)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        // Return 400 Bad Request
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // Handles duplicate creation attempts (e.g., username exists)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Bad Request");
        body.put("message", ex.getMessage());
        // Return 409 Conflict for duplicates, or 400 Bad Request generally
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    // Handles trying to generate an already existing report
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Object> handleIllegalStateException(IllegalStateException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Conflict");
        body.put("message", ex.getMessage());
        // Return 409 Conflict
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }


    // Handles entities not found (e.g., getting a sale by a non-existent ID)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());
        // Return 404 Not Found
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    // Handles foreign key constraint / database integrity violations (e.g., deleting a referenced driver/vehicle)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Conflict");
        
        String message = "Cannot delete this record because it is referenced by other data in the system.";
        String description = request.getDescription(false); // e.g. "uri=/api/drivers/D001"
        
        if (description != null) {
            if (description.contains("/api/drivers")) {
                message = "Cannot delete this driver because they are assigned to existing delivery records or reports. Please delete or update those records first.";
            } else if (description.contains("/api/vehicles")) {
                message = "Cannot delete this vehicle because it is assigned to existing delivery records or reports. Please update or delete those records first.";
            } else if (description.contains("/api/shops")) {
                message = "Cannot delete this shop because it has associated delivery history. Please delete the delivery records for this shop first.";
            } else if (description.contains("/api/products")) {
                message = "Cannot delete this product because it has been used in sales or deliveries. You can mark it as inactive or soft-delete instead.";
            }
        }
        
        body.put("message", message);
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    // Optional: Generic handler for other unexpected errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Internal Server Error");
        body.put("message", "An unexpected error occurred: " + ex.getMessage());
        // Return 500 Internal Server Error
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}