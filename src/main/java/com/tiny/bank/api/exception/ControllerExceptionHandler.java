//package com.tiny.bank.api.exception;
//
//import com.tiny.bank.api.model.response.ErrorResponse;
//import jakarta.validation.ConstraintViolation;
//import jakarta.validation.ConstraintViolationException;
//import org.apache.commons.lang3.exception.ExceptionUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.HttpStatusCode;
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.FieldError;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import org.springframework.web.context.request.WebRequest;
//import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//
//@RestControllerAdvice
//public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerExceptionHandler.class);
//
//    @ExceptionHandler(value = { Exception.class })
//    protected ResponseEntity<Object> handle(Exception ex, WebRequest request) {
//        LOGGER.error("An unexpected error has occurred", ex);
//        ErrorResponse response = new ErrorResponse("An unexpected error has occurred", ExceptionUtils.getMessage(ex));
//        return handleExceptionInternal(ex, response, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
//    }
//
//    @Override
//    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
//        Map<String, String> errors = new HashMap<>();
//        ex.getBindingResult().getAllErrors().forEach((error) -> {
//            String fieldName = ((FieldError) error).getField();
//            String errorMessage = error.getDefaultMessage();
//            errors.put(fieldName, errorMessage);
//        });
//
//        ErrorResponse response = new ErrorResponse("Invalid Payload", errors.toString());
//
//        return handleExceptionInternal(ex, response, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
//    }
//
//    @ExceptionHandler(ConstraintViolationException.class)
//    public ResponseEntity handle(ConstraintViolationException ex, WebRequest request) {
//        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
//        String errorMessage = "";
//        Map<String, String> errors = new HashMap<>();
//
//        if (!violations.isEmpty()) {
//            violations.forEach(violation ->  {
//                String fieldName = violation.getPropertyPath().toString();
//                String message = violation.getMessage();
//                errors.put(fieldName, message);
//
//            });
//            errorMessage = errors.toString();
//        } else {
//            errorMessage = "ConstraintViolationException occurred.";
//        }
//        ErrorResponse response = new ErrorResponse("Invalid Payload", errorMessage);
//
//        return handleExceptionInternal(ex, response, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
//    }
//
//}
