package ru.practicum.ewm.stats.common_dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ViewStatsDto {
    private String app;
    private String uri;
    private long hits;
}
