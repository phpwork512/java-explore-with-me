package ru.practicum.ewm.service.event.model.dto;

import lombok.Data;
import ru.practicum.ewm.service.common.models.Location;

@Data
public class UpdateEventAdminRequest {
    private String annotation;

    private Long category;

    private String description;

    private String eventDate;

    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private String stateAction;

    private String title;
}