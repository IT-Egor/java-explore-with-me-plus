package ru.practicum.explore_with_me.error.model;

public class PatchCommentByNotCorrectEventId extends RuntimeException {
    public PatchCommentByNotCorrectEventId(String message) {
        super(message);
    }
}
