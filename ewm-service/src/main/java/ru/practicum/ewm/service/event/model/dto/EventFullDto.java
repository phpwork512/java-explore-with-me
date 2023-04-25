package ru.practicum.ewm.service.event.model.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.service.category.model.dto.CategoryDto;
import ru.practicum.ewm.service.common.models.Location;
import ru.practicum.ewm.service.user.model.dto.UserShortDto;

@Data
@Builder
public class EventFullDto {
    private long id;

    private String annotation;

    private CategoryDto category;

    private int confirmedRequests;

    private String createdOn;

    private String description;

    private String eventDate;

    private UserShortDto initiator;

    private Location location;

    private boolean paid;

    private int participantLimit;

    private String publishedOn;

    private boolean requestModeration;

    private String state;

    private String title;

    private int views;
}