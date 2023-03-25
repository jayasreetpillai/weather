package com.weather.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Statistics {
    MIN("min"),
    MAX("max"),
    SUM("sum"),
    AVG("avg");

    @Getter
    @JsonValue
    private final String statistic;
}
