package ru.practicum.ewm.stats.server.model.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.ewm.stats.common_dto.StatRecordCreateDto;
import ru.practicum.ewm.stats.server.model.StatRecord;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class StatRecordCreateDtoMapperTest {

    @Test
    void toStatRecord() {
        StatRecordCreateDto statRecordCreateDto = StatRecordCreateDto.builder()
                .app("test-app")
                .uri("/test/uri")
                .ip("1.1.1.1")
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();

        StatRecord statRecord = StatRecord.builder()
                .appName("test-app")
                .uri("/test/uri")
                .ip("1.1.1.1")
                .timestamp(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .build();

        StatRecord mappedStatRecord = StatRecordCreateDtoMapper.toStatRecord(statRecordCreateDto);
        assertEquals(statRecord, mappedStatRecord);
    }
}