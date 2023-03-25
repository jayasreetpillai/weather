package com.weather.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weather.dto.WeatherDto;
import com.weather.dto.WeatherQuery;
import com.weather.dto.WeatherStatics;
import com.weather.enums.Metrics;
import com.weather.enums.Statistics;
import com.weather.exception.WeatherCustomException;
import com.weather.model.Weather;
import com.weather.model.WeatherMetricsAggregate;
import com.weather.repository.WeatherRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.lang.runtime.ObjectMethods;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Slf4j
@Service
public class WeatherService {

    @Autowired
    WeatherRepository weatherRepository;

    @Autowired
    MongoTemplate mongoTemplate;

@Autowired
    ObjectMapper objectMapper;

    private static final String GROUPING_ID = "sensor";
    private static final String MATCHING_ID_TIMESTAMP = "timestamp";
    @Value("${weather.collection}")
    private String weatherCollection;

    public void saveWeather(WeatherDto weatherDto) {
        Weather weather = new Weather();
        weather.setSensor(weatherDto.getSensor());
        weather.setTimestamp(Instant.parse(weatherDto.getDate()));
        weather.setTemperature(weatherDto.getTemperature());
        weather.setHumidity(weatherDto.getHumidity());
        weather.setWindSpeed(weatherDto.getWindSpeed());
        weatherRepository.save(weather);
    }

    public List<WeatherStatics> getWeather(WeatherQuery weatherQuery) throws JsonProcessingException {
        List<WeatherStatics> weatherStaticsList = new ArrayList<>();
        validate(weatherQuery);

        Aggregation aggregation
                = Aggregation.newAggregation(getMatchOperation(weatherQuery), getGroupOperation(weatherQuery.getStatistics(), weatherQuery.getMetrics()),
                getProjectOperation(weatherQuery.getMetrics()));

        log.info("weatherCollection====="+weatherCollection);
        log.debug("{}", aggregation.toString());
        AggregationResults<WeatherMetricsAggregate> weatherMetricsAggregates
                = mongoTemplate.aggregate(aggregation, weatherCollection, WeatherMetricsAggregate.class);

        for (WeatherMetricsAggregate weatherMetricsAggregate : weatherMetricsAggregates) {
            WeatherStatics weatherStatics = new WeatherStatics();
            weatherStatics.setSensor(weatherMetricsAggregate.getSensor());
            weatherStatics.setStatistic(weatherQuery.getStatistics().getStatistic());
            if (Optional.ofNullable(weatherMetricsAggregate.getTemperature()).isPresent()) {
                weatherStatics.setTemperature(weatherMetricsAggregate.getTemperature());
            }
            if (Optional.ofNullable(weatherMetricsAggregate.getHumidity()).isPresent()) {
                weatherStatics.setHumidity(weatherMetricsAggregate.getHumidity());
            }
            if (Optional.ofNullable(weatherMetricsAggregate.getWindSpeed()).isPresent()) {
                weatherStatics.setWindSpeed(weatherMetricsAggregate.getWindSpeed());
            }
            weatherStatics.setFromDate(weatherQuery.getFromDate());
            weatherStatics.setToDate(weatherQuery.getToDate());
            weatherStaticsList.add(weatherStatics);
            log.info(objectMapper.writeValueAsString(weatherStatics));
        }

        return weatherStaticsList;
    }

    private void validate(WeatherQuery weatherQuery) {
        Instant fromDate;
        Instant toDate;
        if (weatherQuery.getFromDate() != null && weatherQuery.getToDate() != null) {
            fromDate = weatherQuery.getFromDate();
            toDate = weatherQuery.getToDate();
            if (fromDate.compareTo(toDate) > 0) {
                throw new WeatherCustomException("'fromDate' should be less than 'toDate'");
            }
            if (toDate.compareTo(Instant.now()) > 0) {
                throw new WeatherCustomException("'toDate' should be less than current time");
            }
        } else if (weatherQuery.getFromDate() == null && weatherQuery.getToDate() == null) {
            weatherQuery.setToDate(Instant.now());
            weatherQuery.setFromDate(Instant.now().minusSeconds(24 * 60 * 60L));
        } else {
            throw new WeatherCustomException("Both 'fromDate' and 'toDate' should be present or not present at all");
        }
    }

    private MatchOperation getMatchOperation(WeatherQuery weatherQuery) {
        Criteria criteria = Criteria.where(GROUPING_ID)
                .in(weatherQuery.getSensors())
                .andOperator(Criteria.where(MATCHING_ID_TIMESTAMP)
                        .gt(weatherQuery.getFromDate())
                        .lt(weatherQuery.getToDate()));
        MatchOperation matchOperation = match(criteria);
        return matchOperation;
    }

    private GroupOperation getGroupOperation(Statistics statistics, List<Metrics> metrics) {
        GroupOperation groupOperation = group(GROUPING_ID);

        for (Metrics metric : metrics) {
            if ("min".equals(statistics.getStatistic())) {
                groupOperation = groupOperation.min(metric.getValue()).as(metric.getValue());
            } else if ("max".equals(statistics.getStatistic())) {
                groupOperation = groupOperation.max(metric.getValue()).as(metric.getValue());
            } else if ("sum".equals(statistics.getStatistic())) {
                groupOperation = groupOperation.sum(metric.getValue()).as(metric.getValue());
            } else if ("avg".equals(statistics.getStatistic())) {
                groupOperation = groupOperation.avg(metric.getValue()).as(metric.getValue());
            }
        }
        return groupOperation;
    }

    private ProjectionOperation getProjectOperation(List<Metrics> metrics) {
        List<String> metricValue = metrics.stream()
                .map(Metrics::getValue)
                .collect(Collectors.toList());
        return project(metricValue.toArray(new String[0])).and(GROUPING_ID).previousOperation();
    }
}
