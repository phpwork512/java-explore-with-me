package ru.practicum.ewm.service.comments.model.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.service.event.model.dto.EventShortDto;
import ru.practicum.ewm.service.user.model.dto.UserShortDto;

@Data
@Builder
public class CommentDto {
    private long id;

    private EventShortDto event;

    private UserShortDto author;

    private String text;

    private String created;
}