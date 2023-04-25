package ru.practicum.ewm.service.event.model.dto;

import lombok.Data;
import ru.practicum.ewm.service.common.models.Location;

@Data
public class UpdateEventUserRequest {
    private String annotation;

    private Long categoryId;

    private String description;

    private String eventDate;

    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private String stateAction;

    private String title;
}