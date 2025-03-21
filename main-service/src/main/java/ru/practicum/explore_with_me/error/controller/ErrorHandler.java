package ru.practicum.explore_with_me.error.controller;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import ru.practicum.explore_with_me.error.model.AlreadyExistsException;
import ru.practicum.explore_with_me.error.model.ErrorResponse;
import ru.practicum.explore_with_me.error.model.NotFoundException;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleAlreadyExistException(AlreadyExistsException e) {
        String reasonMessage = "Field already exists";
        log.error(String.format("CONFLICT: %s", reasonMessage), e);
        return ErrorResponse.builder()
                .errors(List.of(e.getMessage()))
                .message(e.getMessage())
                .reason(reasonMessage)
                .status(String.format("%s %s", HttpStatus.CONFLICT, HttpStatus.CONFLICT.getReasonPhrase()))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(ValidationException e) {
        String reasonMessage = "Validation failed";
        log.error(String.format("BAD_REQUEST: %s", reasonMessage), e);
        return ErrorResponse.builder()
                .errors(List.of(e.getMessage()))
                .message(e.getMessage())
                .reason(reasonMessage)
                .status(String.format("%s %s", HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadableException(MethodArgumentNotValidException e) {
        String reasonMessage = "Method argument not valid";
        log.error(String.format("BAD_REQUEST: %s", reasonMessage), e);
        return ErrorResponse.builder()
                .errors(List.of(e.getMessage()))
                .message(e.getMessage())
                .reason(reasonMessage)
                .status(String.format("%s %s", HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHandlerMethodValidationException(HandlerMethodValidationException e) {
        String reasonMessage = "Handler method not valid";
        log.error(String.format("BAD_REQUEST: %s", reasonMessage), e);
        return ErrorResponse.builder()
                .errors(List.of(e.getMessage()))
                .message(e.getMessage())
                .reason(reasonMessage)
                .status(String.format("%s %s", HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        String reasonMessage = "Entity not found";
        log.error(String.format("NOT_FOUND: %s", reasonMessage), e);
        return ErrorResponse.builder()
                .errors(List.of(e.getMessage()))
                .message(e.getMessage())
                .reason("Entity not found")
                .status(String.format("%s %s", HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase()))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(RuntimeException e) {
        String reasonMessage = "Unknown error";
        log.error(String.format("INTERNAL_SERVER_ERROR: %s", reasonMessage), e);
        return ErrorResponse.builder()
                .errors(List.of(e.getMessage()))
                .message(e.getMessage())
                .reason(reasonMessage)
                .status(String.format("%s %s", HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()))
                .build();
    }
}
