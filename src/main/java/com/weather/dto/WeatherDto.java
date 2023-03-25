package com.weather.dto;

import lombok.Data;

@Data
public class WeatherDto {
    private String sensor;
    private String date;
    private double temperature;
    private double humidity;
    private double windSpeed;
}
