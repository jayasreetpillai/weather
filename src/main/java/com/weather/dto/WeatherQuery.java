package com.weather.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.weather.enums.Metrics;
import com.weather.enums.Statistics;
import com.weather.validator.DateDeserializer;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Data
@ToString
@AllArgsConstructor
@Validated
public class WeatherQuery implements Serializable {

    @NonNull
    private List<String> sensors;

    @NonNull
    private List<Metrics> metrics;

    @NonNull
    private Statistics statistics;

    @JsonDeserialize(using = DateDeserializer.class)
    private Instant fromDate;

    @JsonDeserialize(using = DateDeserializer.class)
    private Instant toDate;
}
