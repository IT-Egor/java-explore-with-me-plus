package ru.practicum.explore_with_me.error.model;

public class DeleteLinkedObjectException extends RuntimeException {
    public DeleteLinkedObjectException(String message) {
        super(message);
    }
    public DeleteLinkedObjectException(String message, Throwable cause){super(message, cause);}
}
