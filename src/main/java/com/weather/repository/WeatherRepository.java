package com.weather.repository;

import com.weather.model.Weather;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface WeatherRepository extends MongoRepository<Weather, String> {

    public List<Weather> findBySensor(String sensor);

/*    @Aggregation( pipeline = {
            "{'$match':{'sensor':?0}}",
            "{ '$group' : { '_id' : '$sensor', 'sumTemperature' : { $sum: '$temperature' } } }"
    })
    List<WeatherAggregate> sumTemperature(String sensor);*/
}
