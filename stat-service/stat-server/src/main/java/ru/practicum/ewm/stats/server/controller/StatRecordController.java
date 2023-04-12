package ru.practicum.ewm.stats.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.stats.common_dto.StatRecordCreateDto;
import ru.practicum.ewm.stats.common_dto.ViewStatsDto;
import ru.practicum.ewm.stats.server.service.StatRecordService;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatRecordController {
    private final StatRecordService statRecordService;
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void postStatRecord(@RequestBody @Valid StatRecordCreateDto statRecordCreateDto) {
        log.info("New hit: {}", statRecordCreateDto);
        statRecordService.newStatRecord(statRecordCreateDto);
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStatsDto> getStats(@RequestParam @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime start,
                                       @RequestParam @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime end,
                                       @RequestParam(defaultValue = "") List<String> uris,
                                       @RequestParam(defaultValue = "false") boolean unique) {
        if (start.isBefore(end)) {
            List<ViewStatsDto> statsList = statRecordService.getStats(start, end, uris, unique);
            log.info("Get stats from {} to {}, uris: {}, unique {}. Found {} records", start, end, uris, unique, statsList.size());
            return statsList;
        } else {
            throw new ValidationException("Время начала интервала должно быть меньше времени конца");
        }
    }
}