package com.weather.model;

import lombok.Data;

@Data
public class WeatherMetricsAggregate {
    private String sensor;
    private Double temperature;
    private Double humidity;
    private Double windSpeed;
}
