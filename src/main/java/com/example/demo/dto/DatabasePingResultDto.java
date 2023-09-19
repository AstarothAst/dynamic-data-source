package com.example.demo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@RequiredArgsConstructor
@Getter
public class DatabasePingResultDto {

    private final String databaseName;
    private final boolean result;
}
