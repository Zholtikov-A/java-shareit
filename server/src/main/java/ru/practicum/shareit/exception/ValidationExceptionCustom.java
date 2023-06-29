package ru.practicum.shareit.exception;

public class ValidationExceptionCustom extends RuntimeException {
    public ValidationExceptionCustom(String message) {
        super(message);
    }
}
