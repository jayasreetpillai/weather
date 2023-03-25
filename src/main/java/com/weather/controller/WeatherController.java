package com.weather.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.weather.dto.WeatherDto;
import com.weather.dto.WeatherQuery;
import com.weather.dto.WeatherStatics;
import com.weather.service.WeatherService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@Validated
public class WeatherController {
    @Autowired
    WeatherService weatherService;

    @PostMapping("/weather")
    public void saveWeatherMetrics(@RequestBody WeatherDto weatherDto) {
        weatherService.saveWeather(weatherDto);
    }

    @PostMapping("/weather/metric")
    public List<WeatherStatics> getWeather(@Valid @RequestBody WeatherQuery weatherQuery) throws JsonProcessingException {
        return weatherService.getWeather(weatherQuery);
    }
}
