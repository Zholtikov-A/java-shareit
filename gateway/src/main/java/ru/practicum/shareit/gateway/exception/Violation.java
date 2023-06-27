package ru.practicum.shareit.gateway.exception;

import lombok.Value;

@Value
public class Violation {

    String fieldName;
    String message;

}
