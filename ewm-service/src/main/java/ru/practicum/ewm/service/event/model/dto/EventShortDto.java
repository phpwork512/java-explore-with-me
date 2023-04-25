package ru.practicum.ewm.service.event.model.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.service.category.model.dto.CategoryDto;
import ru.practicum.ewm.service.user.model.dto.UserShortDto;

@Data
@Builder
public class EventShortDto {
    private long id;

    private String annotation;

    private CategoryDto category;

    private int confirmedRequests;

    private String eventDate;

    private UserShortDto initiator;

    private boolean paid;

    private String title;

    private int views;
}