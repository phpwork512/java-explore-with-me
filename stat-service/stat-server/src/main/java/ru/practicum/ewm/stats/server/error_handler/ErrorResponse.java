package ru.practicum.ewm.stats.server.error_handler;

import lombok.Data;

@Data
public class ErrorResponse {
    // название ошибки
    private final String error;
    // подробное описание
    private final String description;
}
