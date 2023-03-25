package com.weather.exception;

public class WeatherCustomException extends RuntimeException{
    public WeatherCustomException(String message){
        super(message);
    }
}
