package ru.practicum.ewm.service.exceptions;

public class CommentEventIsNotPublishedException extends RuntimeException {
    public CommentEventIsNotPublishedException(String s) {
        super(s);
    }
}
