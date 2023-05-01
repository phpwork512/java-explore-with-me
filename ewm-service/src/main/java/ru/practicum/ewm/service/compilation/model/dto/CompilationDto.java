package ru.practicum.ewm.service.compilation.model.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.service.event.model.dto.EventShortDto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Data
@Builder
public class CompilationDto {
    @PositiveOrZero
    private long id;

    private List<EventShortDto> events;

    @NotNull
    private Boolean pinned;

    @NotEmpty
    private String title;
}
