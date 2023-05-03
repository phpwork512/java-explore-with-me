package ru.practicum.ewm.service.comments.model.dto;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class UpdateCommentDto {
    private long id;

    @Size(min = 3, max = 2000, message = "Текст комментария должен быть от 3 до 2000 символов")
    private String text;
}