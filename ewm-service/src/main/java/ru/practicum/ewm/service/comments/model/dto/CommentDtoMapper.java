package ru.practicum.ewm.service.comments.model.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.service.comments.model.Comment;
import ru.practicum.ewm.service.common.EwmConstants;
import ru.practicum.ewm.service.event.model.dto.EventDtoMapper;
import ru.practicum.ewm.service.user.model.dto.UserDtoMapper;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommentDtoMapper {
    public static CommentDto toCommentDto(Comment comment) {
        if (comment != null) {
            return CommentDto.builder()
                    .id(comment.getId())
                    .event(EventDtoMapper.toEventShortDto(comment.getEvent()))
                    .author(UserDtoMapper.toUserShortDto(comment.getAuthor()))
                    .text(comment.getText())
                    .created(comment.getCreated() != null ? comment.getCreated().format(EwmConstants.DATE_TIME_FORMATTER) : null)
                    .build();
        } else {
            return null;
        }
    }

    public static List<CommentDto> toCommentDtoList(List<Comment> comments) {
        if (comments != null) {
            List<CommentDto> commentDtoList = new ArrayList<>();
            for (Comment comment : comments) {
                commentDtoList.add(toCommentDto(comment));
            }
            return commentDtoList;
        } else {
            return null;
        }
    }
}