package ru.practicum.explore_with_me.error.controller;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import ru.practicum.explore_with_me.error.model.*;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleAlreadyExistException(AlreadyExistsException e) {
        String reasonMessage = "Field already exists";
        log.error("CONFLICT: {}", reasonMessage, e);
        return ErrorResponse.builder()
                .errors(List.of(e.getMessage()))
                .message(e.getMessage())
                .reason(reasonMessage)
                .status(HttpStatus.CONFLICT.toString())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleAlreadyPublishedException(AlreadyPublishedException e) {
        String reasonMessage = "Event already published";
        log.error("CONFLICT: {}", reasonMessage, e);
        return ErrorResponse.builder()
                .errors(List.of(e.getMessage()))
                .message(e.getMessage())
                .reason(reasonMessage)
                .status(HttpStatus.CONFLICT.toString())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handlePublicationException(PublicationException e) {
        String reasonMessage = "Publication failed";
        log.error("CONFLICT: {}", reasonMessage, e);
        return ErrorResponse.builder()
                .errors(List.of(e.getMessage()))
                .message(e.getMessage())
                .reason(reasonMessage)
                .status(HttpStatus.CONFLICT.toString())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        String reasonMessage = "Body of the request is not readable";
        log.error("CONFLICT: {}", reasonMessage, e);
        return ErrorResponse.builder()
                .errors(List.of(e.getMessage()))
                .message(e.getMessage())
                .reason(reasonMessage)
                .status(HttpStatus.CONFLICT.toString())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(ValidationException e) {
        String reasonMessage = "Validation failed";
        log.error("BAD_REQUEST: {}", reasonMessage, e);
        return ErrorResponse.builder()
                .errors(List.of(e.getMessage()))
                .message(e.getMessage())
                .reason(reasonMessage)
                .status(HttpStatus.BAD_REQUEST.toString())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUpdateStartDateException(UpdateStartDateException e) {
        String reasonMessage = "Update start date failed";
        log.error("BAD_REQUEST: {}", reasonMessage, e);
        return ErrorResponse.builder()
                .errors(List.of(e.getMessage()))
                .message(e.getMessage())
                .reason(reasonMessage)
                .status(HttpStatus.BAD_REQUEST.toString())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadableException(MethodArgumentNotValidException e) {
        String reasonMessage = "Method argument not valid";
        log.error("BAD_REQUEST: {}", reasonMessage, e);
        return ErrorResponse.builder()
                .errors(List.of(e.getMessage()))
                .message(e.getMessage())
                .reason(reasonMessage)
                .status(HttpStatus.BAD_REQUEST.toString())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHandlerMethodValidationException(HandlerMethodValidationException e) {
        String reasonMessage = "Handler method not valid";
        log.error("BAD_REQUEST: {}", reasonMessage, e);
        return ErrorResponse.builder()
                .errors(List.of(e.getMessage()))
                .message(e.getMessage())
                .reason(reasonMessage)
                .status(HttpStatus.BAD_REQUEST.toString())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        String reasonMessage = "Entity not found";
        log.error("NOT_FOUND: {}", reasonMessage, e);
        return ErrorResponse.builder()
                .errors(List.of(e.getMessage()))
                .message(e.getMessage())
                .reason("Entity not found")
                .status(HttpStatus.NOT_FOUND.toString())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        String reasonMessage = "Related objects cannot be deleted";
        log.error("CONFLICT: {}", reasonMessage, e);
        return ErrorResponse.builder()
                .errors(List.of(e.getMessage()))
                .message(e.getMessage())
                .reason(reasonMessage)
                .status(HttpStatus.CONFLICT.toString())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(RuntimeException e) {
        String reasonMessage = "Unknown error";
        log.error("INTERNAL_SERVER_ERROR: {}", reasonMessage, e);
        return ErrorResponse.builder()
                .errors(List.of(e.getMessage()))
                .message(e.getMessage())
                .reason(reasonMessage)
                .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .build();
    }
}
