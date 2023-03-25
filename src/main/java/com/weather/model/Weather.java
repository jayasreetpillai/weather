package com.weather.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Calendar;

@Data
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Document("weather_metrics")
public class Weather {
    @Id
    private String id;
    private String sensor;
    private Instant timestamp;
    private double temperature;
    private double humidity;
    private double windSpeed;
}

