package com.weather;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weather.controller.WeatherController;
import com.weather.dto.WeatherQuery;
import com.weather.dto.WeatherStatics;
import com.weather.enums.Metrics;
import com.weather.enums.Statistics;
import com.weather.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Instant;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(SpringExtension.class)
@WebMvcTest(value = WeatherController.class)
public class WeatherControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeatherService weatherService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void getWeather() throws Exception {
        WeatherQuery weatherQuery = new WeatherQuery(Arrays.asList("sensor2"), Arrays.asList(Metrics.HUMIDITY, Metrics.TEMP, Metrics.WINDSPEED),
                Statistics.MIN, Instant.parse("2023-03-15T00:00:23Z"), Instant.parse("2023-03-22T23:47:23Z"));
        String response = "[{\"sensor\":\"sensor2\",\"humidity\":22.34,\"windSpeed\":5.4,\"statistic\":\"min\"}]";
        WeatherStatics weatherStatics = new WeatherStatics()
                .setSensor("sensor2")
                .setTemperature(44.0)
                .setHumidity(22.34)
                .setWindSpeed(5.4)
                .setStatistic("min")
                .setFromDate(Instant.parse("2023-03-15T00:00:23Z"))
                .setToDate(Instant.parse("2023-03-22T23:47:23Z"));
        ;
        ;
        Mockito.when(weatherService.getWeather(weatherQuery)).thenReturn(Arrays.asList(weatherStatics));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/weather/metric")
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(weatherQuery))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse resultResponse = result.getResponse();

        assertEquals(HttpStatus.OK.value(), resultResponse.getStatus());

        String expected = "[{\"sensor\":\"sensor2\",\"temperature\":44.0,\"humidity\":22.34,\"windSpeed\":5.4,\"statistic\":\"min\",\"fromDate\":\"2023-03-15T00:00:23Z\",\"toDate\":\"2023-03-22T23:47:23Z\"}]";
        JSONAssert.assertEquals(expected, result.getResponse()
                .getContentAsString(), false);
    }

    @Test
    public void getWeatherWithTemperatureAndMaxFilter() throws Exception {
        WeatherQuery weatherQuery = new WeatherQuery(Arrays.asList("sensor1"), Arrays.asList(Metrics.TEMP),
                Statistics.MAX, Instant.parse("2023-03-15T00:00:23Z"), Instant.parse("2023-03-17T23:47:23Z"));

        WeatherStatics weatherStatics = new WeatherStatics()
                .setSensor("sensor1")
                .setTemperature(74.0)
                .setStatistic("max")
                .setFromDate(Instant.parse("2023-03-15T00:00:23Z"))
                .setToDate(Instant.parse("2023-03-17T23:47:23Z"));
        Mockito.when(weatherService.getWeather(weatherQuery)).thenReturn(Arrays.asList(weatherStatics));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/weather/metric")
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(weatherQuery))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse resultResponse = result.getResponse();

        assertEquals(HttpStatus.OK.value(), resultResponse.getStatus());

        String expected = "[{\"sensor\":\"sensor1\",\"temperature\":74.0,\"statistic\":\"max\",\"fromDate\":\"2023-03-15T00:00:23Z\",\"toDate\":\"2023-03-17T23:47:23Z\"}]";
        JSONAssert.assertEquals(expected, result.getResponse()
                .getContentAsString(), false);
    }

    @Test
    public void getWeatherWithHumidityAndAvgFilter() throws Exception {
        WeatherQuery weatherQuery = new WeatherQuery(Arrays.asList("sensor3", "sensor1"), Arrays.asList(Metrics.HUMIDITY),
                Statistics.AVG, Instant.parse("2023-03-15T00:00:23Z"), Instant.parse("2023-03-22T23:47:23Z"));

        WeatherStatics weatherStatics = new WeatherStatics()
                .setSensor("sensor3")
                .setHumidity(21.67)
                .setStatistic("avg")
                .setFromDate(Instant.parse("2023-03-15T00:00:23Z"))
                .setToDate(Instant.parse("2023-03-22T23:47:23Z"));

        WeatherStatics weatherStatics1 = new WeatherStatics()
                .setSensor("sensor1")
                .setHumidity(22.34)
                .setStatistic("avg")
                .setFromDate(Instant.parse("2023-03-15T00:00:23Z"))
                .setToDate(Instant.parse("2023-03-22T23:47:23Z"));
        Mockito.when(weatherService.getWeather(weatherQuery)).thenReturn(Arrays.asList(weatherStatics, weatherStatics1));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/weather/metric")
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(weatherQuery))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse resultResponse = result.getResponse();

        assertEquals(HttpStatus.OK.value(), resultResponse.getStatus());

        String expected = "[{\"sensor\":\"sensor3\",\"humidity\":21.67,\"statistic\":\"avg\",\"fromDate\":\"2023-03-15T00:00:23Z\",\"toDate\":\"2023-03-22T23:47:23Z\"},{\"sensor\":\"sensor1\",\"humidity\":22.34,\"statistic\":\"avg\",\"fromDate\":\"2023-03-15T00:00:23Z\",\"toDate\":\"2023-03-22T23:47:23Z\"}]";
        JSONAssert.assertEquals(expected, result.getResponse()
                .getContentAsString(), false);
    }

    @Test
    public void getWeatherWithWindSpeedAndSumFilter() throws Exception {
        WeatherQuery weatherQuery = new WeatherQuery(Arrays.asList("sensor2"), Arrays.asList(Metrics.WINDSPEED),
                Statistics.SUM, Instant.parse("2023-03-17T00:00:23Z"), Instant.parse("2023-03-22T23:47:23Z"));
        WeatherStatics weatherStatics = new WeatherStatics().setSensor("sensor2")
                .setWindSpeed(12.8)
                .setStatistic("sum")
                .setFromDate(Instant.parse("2023-03-17T00:00:23Z"))
                .setToDate(Instant.parse("2023-03-22T23:47:23Z"));
        Mockito.when(weatherService.getWeather(weatherQuery)).thenReturn(Arrays.asList(weatherStatics));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/weather/metric")
                .accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(weatherQuery))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse resultResponse = result.getResponse();

        assertEquals(HttpStatus.OK.value(), resultResponse.getStatus());

        String expected = "[{\"sensor\":\"sensor2\",\"windSpeed\":12.8,\"statistic\":\"sum\",\"fromDate\":\"2023-03-17T00:00:23Z\",\"toDate\":\"2023-03-22T23:47:23Z\"}]";
        JSONAssert.assertEquals(expected, result.getResponse()
                .getContentAsString(), false);
    }

}