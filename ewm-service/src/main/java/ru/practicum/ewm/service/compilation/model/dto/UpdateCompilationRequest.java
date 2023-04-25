package ru.practicum.ewm.service.compilation.model.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Set;

@Data
public class UpdateCompilationRequest {
    private Set<Long> events;

    private Boolean pinned;

    private String title;
}
