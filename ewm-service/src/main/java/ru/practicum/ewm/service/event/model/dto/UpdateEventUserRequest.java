package ru.practicum.ewm.service.event.model.dto;

import lombok.Data;
import ru.practicum.ewm.service.common.models.Location;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

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