package com.sarveshsawant.webdev.exception;

import com.sarveshsawant.webdev.exception.custom.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLTransientConnectionException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    //Return 400 BAD REQUEST if message is not in right format
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequestException(BadRequestException badRequestException){
        log.error("Error occurred ", badRequestException);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }

    //Return 503 SERVICE UNAVAILABLE if there is data access exception
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<String> handleDataAccessException(DataAccessException dataAccessException){
        log.error("Error occurred ", dataAccessException);
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .build();
    }

    //Return 404 Not found exception if the URL is not supported
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<String> handleNoResponseFoundException(NoResourceFoundException noResourceFoundException){
        log.error("Error occured ", noResourceFoundException);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .build();
    }

    // Handles Validation Error
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException methodArgumentNotValidException) {
        log.error("Error occured ", methodArgumentNotValidException);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }

    // Return 404 when email does not exist in the database
    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<String> handleEmailNotFoundException(EmailNotFoundException emailNotFoundException){
        log.error("Error occured ", emailNotFoundException);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }

    // Wrong field was updated
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException dataIntegrityViolationException){
        log.error("Error occured ", dataIntegrityViolationException);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }

    //Throw BAD Request error when wrong format request body is sent
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException httpMessageNotReadableException){
        log.error("Error occured ", httpMessageNotReadableException);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }

    @ExceptionHandler(ImageAlreadyExists.class)
    public ResponseEntity<String> handleImageAlreadyExistsException(ImageAlreadyExists imageAlreadyExists){
        log.error("Error occured ", imageAlreadyExists);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }

    @ExceptionHandler(FileEmptyException.class)
    public ResponseEntity<String> handleFileEmptyException(FileEmptyException fileEmptyException){
        log.error("Error occured ", fileEmptyException);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }

    @ExceptionHandler(FileFormatInvalidException.class)
    public ResponseEntity<String> handleFileFormatInvalidException(FileFormatInvalidException fileFormatInvalidException){
        log.error("Error occured ", fileFormatInvalidException);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }

    @ExceptionHandler(FileSizeExceedException.class)
    public ResponseEntity<String> handleFileSizeExceedException(FileSizeExceedException fileSizeExceedException){
        log.error("Error occured ", fileSizeExceedException);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }

    @ExceptionHandler(ImageNotFoundException.class)
    public ResponseEntity<String> handleImageNotFoundException(ImageNotFoundException imageNotFoundException){
        log.error("Error occured ", imageNotFoundException);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .build();
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<String> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException httpMediaTypeNotSupportedException){
        log.error("Error occured ", httpMediaTypeNotSupportedException);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }

    @ExceptionHandler(VerificationException.class)
    public ResponseEntity<String> handleVerificationException(VerificationException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<String> handleTokenExpiredException(TokenExpiredException ex) {
        log.error("Error occurred ", ex);
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .build();
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<String> handleInvalidTokenException(InvalidTokenException ex) {
        log.error("Error occurred ", ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }

    @ExceptionHandler(UserAlreadyVerifiedException.class)
    public ResponseEntity<String> handleUserAlreadyVerifiedException(UserAlreadyVerifiedException ex) {
        log.error("Error occurred ", ex);
        return ResponseEntity
                .status(HttpStatus.CONFLICT) // 409 Conflict - user already verified
                .build();
    }

    //Return 500 for all the other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception exception){
        log.error("Error occurred ", exception);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }

}
