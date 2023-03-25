package com.weather.enums;


import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Metrics {
    TEMP("temperature"),
    HUMIDITY("humidity"),
    WINDSPEED("windSpeed");

    @Getter
    @JsonValue
    private final String value;
}