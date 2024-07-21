package com.luckvicky.blur.global.model.dto;

import static com.luckvicky.blur.global.constant.StringFormat.TIMESTAMP_FORMAT;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Result<T> {

    private String timestamp;
    private UUID trackingId;
    private T data;

    @Builder
    public Result(T data) {
        this.timestamp = LocalDateTime.now(Clock.systemDefaultZone()).format(DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT));
        this.trackingId = UUID.randomUUID();
        this.data = data;
    }

}
