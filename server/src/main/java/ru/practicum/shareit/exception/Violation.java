package ru.practicum.shareit.exception;

import lombok.Value;

@Value
public class Violation {

    String fieldName;
    String message;

}
