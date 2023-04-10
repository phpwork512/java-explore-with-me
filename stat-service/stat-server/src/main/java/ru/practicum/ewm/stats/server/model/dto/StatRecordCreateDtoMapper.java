package ru.practicum.ewm.stats.server.model.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.stats.common_dto.StatRecordCreateDto;
import ru.practicum.ewm.stats.server.model.StatRecord;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StatRecordCreateDtoMapper {
    public static StatRecord toStatRecord(StatRecordCreateDto statRecordCreateDto) {
        if (statRecordCreateDto != null) {
            return StatRecord.builder().
                    appName(statRecordCreateDto.getApp()).
                    uri(statRecordCreateDto.getUri()).
                    ip(statRecordCreateDto.getIp()).
                    timestamp(LocalDateTime.parse(statRecordCreateDto.getTimestamp(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).
                    build();
        }
        else {
            return null;
        }
    }
}
