package com.weather.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.time.Instant;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Accessors(chain = true)
public class WeatherStatics {
    private String sensor;
    private Double temperature;
    private Double humidity;
    private Double windSpeed;
    private String statistic;
    private Instant fromDate;
    private Instant toDate;
}
