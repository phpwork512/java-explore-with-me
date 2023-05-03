package ru.practicum.ewm.service.comments.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class NewCommentDto {
    @Size(min = 3, max = 2000, message = "Текст комментария должен быть от 3 до 2000 символов")
    private String text;
}
